package com.newlang.french.ai

import com.newlang.french.data.AppSettings
import com.newlang.french.data.ChatTurn
import com.newlang.french.data.Correction
import com.newlang.french.data.LocalContent
import com.newlang.french.data.SecureSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TutorEngine(private val settingsStore: SecureSettings) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(75, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun reply(
        mode: Mode,
        history: List<ChatTurn>,
        userFrench: String,
        extra: String = ""
    ): ChatTurn = withContext(Dispatchers.IO) {
        val settings = settingsStore.load()
        if (!settings.hasKey) {
            return@withContext demoReply(mode, history, userFrench)
        }
        runCatching { liveReply(settings, mode, history, userFrench, extra) }
            .getOrElse { err ->
                ChatTurn(
                    role = "tutor",
                    french = "On reprend dans une seconde.",
                    hebrew = "OpenAI לא ענה כמו שצריך: ${err.message ?: "שגיאה"}. בדקו מפתח ומודל בהגדרות, או המשיכו עם מצב הדמו."
                )
            }
    }

    suspend fun correctWriting(text: String): WritingReport = withContext(Dispatchers.IO) {
        val settings = settingsStore.load()
        if (!settings.hasKey) {
            return@withContext localWriting(text)
        }
        runCatching {
            val turn = liveReply(
                settings,
                Mode.Writing,
                emptyList(),
                text,
                "Correct this French writing. Be a strict but kind teacher."
            )
            WritingReport(
                overall = turn.hebrew.ifBlank { "הנה התיקון." },
                corrected = turn.french,
                items = listOfNotNull(turn.correction)
            )
        }.getOrElse { localWriting(text) }
    }

    private fun liveReply(
        settings: AppSettings,
        mode: Mode,
        history: List<ChatTurn>,
        userFrench: String,
        extra: String
    ): ChatTurn {
        val messages = JSONArray()
        messages.put(JSONObject().put("role", "system").put("content", systemPrompt(mode)))
        history.takeLast(12).forEach { turn ->
            val role = if (turn.role == "user") "user" else "assistant"
            val content = if (role == "user") turn.french else buildString {
                append(turn.french)
                if (turn.hebrew.isNotBlank()) append("\nHE: ").append(turn.hebrew)
            }
            messages.put(JSONObject().put("role", role).put("content", content))
        }
        if (userFrench.isNotBlank()) {
            val payload = if (extra.isBlank()) userFrench else "$userFrench\n\nCONTEXT: $extra"
            messages.put(JSONObject().put("role", "user").put("content", payload))
        }
        val body = JSONObject()
            .put("model", settings.model)
            .put("messages", messages)
        if (!settings.model.startsWith("o")) {
            body.put("temperature", 0.6)
            body.put("response_format", JSONObject().put("type", "json_object"))
        }
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer ${settings.apiKey}")
            .addHeader("Content-Type", "application/json")
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                error("HTTP ${response.code}: ${raw.take(180)}")
            }
            val root = json.parseToJsonElement(raw).jsonObject
            val content = root["choices"]
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonObject
                ?.get("message")
                ?.jsonObject
                ?.get("content")
                ?.jsonPrimitive
                ?.contentOrNull
                .orEmpty()
            return parseTurn(content)
        }
    }

    private fun parseTurn(content: String): ChatTurn {
        val obj = runCatching {
            val trimmed = content.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            json.parseToJsonElement(trimmed).jsonObject
        }.getOrNull()
        if (obj == null) {
            return ChatTurn("tutor", content.take(400), "המורה ענה בטקסט חופשי.")
        }
        fun str(key: String) = obj[key]?.jsonPrimitive?.contentOrNull.orEmpty()
        val corrObj = obj["correction"] as? JsonObject
        val correction = corrObj?.let {
            val original = it["original"]?.jsonPrimitive?.contentOrNull.orEmpty()
            val fixed = it["fixed"]?.jsonPrimitive?.contentOrNull.orEmpty()
            if (original.isBlank() && fixed.isBlank()) null
            else Correction(
                original = original,
                fixed = fixed,
                why = it["why"]?.jsonPrimitive?.contentOrNull.orEmpty(),
                rule = it["rule"]?.jsonPrimitive?.contentOrNull.orEmpty()
            )
        }
        return ChatTurn(
            role = "tutor",
            french = str("french").ifBlank { str("reply") },
            hebrew = str("hebrew").ifBlank { str("feedback") },
            correction = correction,
            slangNote = str("slang")
        )
    }

    private fun demoReply(mode: Mode, history: List<ChatTurn>, userFrench: String): ChatTurn {
        val script = when (mode) {
            Mode.Cafe -> LocalContent.cafeScript
            Mode.Writing -> emptyList()
            else -> LocalContent.conversationScript
        }
        val userTurns = history.count { it.role == "user" }
        val next = script.getOrElse(userTurns.coerceAtMost(script.lastIndex)) {
            ChatTurn("tutor", "On se capte bientôt. C'était nickel.", "סגרנו יפה. זה בדיוק הקצב של שיחה אמיתית.")
        }
        val correction = inferLocalCorrection(userFrench)
        val slang = if (mode == Mode.Cafe && userFrench.isNotBlank()) {
            "מקומיים מקצרים: tu veux → tu veux, ne pas → pas. תשמרו על קצר."
        } else ""
        return next.copy(correction = correction, slangNote = slang.ifBlank { next.slangNote })
    }

    private fun inferLocalCorrection(text: String): Correction? {
        val t = text.trim()
        if (t.isBlank()) return null
        return when {
            t.contains("je suis flemme", true) -> Correction(
                t, "J'ai la flemme",
                "בצרפתית לא אומרים je suis flemme. יש לך את העצלות — j'ai.",
                "avoir la flemme"
            )
            t.contains("je suis chaud", true) && !t.contains("j'ai chaud", true) -> Correction(
                t, "C'est chaud  /  J'ai chaud",
                "je suis chaud נשמע כמו סלנג אחר לגמרי. למזג אוויר: j'ai chaud. לסיטואציה: c'est chaud.",
                "avoir chaud / c'est chaud"
            )
            t.length < 3 -> Correction(
                t, "Oui, ça va.",
                "קצר מדי לשיחה. אפילו שלוש מילים עושות את זה אמיתי.",
                "ענו במשפט, לא במילה בודדת"
            )
            else -> null
        }
    }

    private fun localWriting(text: String): WritingReport {
        val items = mutableListOf<Correction>()
        if (text.contains("je suis aller", true)) {
            items += Correction("je suis aller", "je suis allé / allée", "הבינוני חייב התאמה והצורה הנכונה של aller.", "passé composé")
        }
        if (text.contains("beaucoup des", true)) {
            items += Correction("beaucoup des", "beaucoup de", "אחרי beaucoup בא de, לא des.", "beaucoup de + שם עצם")
        }
        if (items.isEmpty() && text.isNotBlank()) {
            items += Correction(
                text.take(40),
                text.replace(" ca ", " ça ", ignoreCase = true),
                "בדמו בלי מפתח אני תופס רק טעויות נפוצות. הוסיפו OpenAI לתיקון מורה אמיתי.",
                "ça / accord / articles"
            )
        }
        return WritingReport(
            overall = if (items.isEmpty()) "אין טקסט לבדיקה." else "הנה מה שצריך לתקן — בלי כוכבים זהובים.",
            corrected = items.firstOrNull()?.fixed ?: text,
            items = items
        )
    }

    private fun systemPrompt(mode: Mode): String = when (mode) {
        Mode.Conversation -> """
            You are a French tutor for Hebrew speakers. Hold a natural 2-minute spoken conversation about daily life.
            Push the student to answer in French. Never give empty praise. Correct only what matters.
            Reply as JSON: {"french":"...","hebrew":"short teacher note in Hebrew","correction":{"original":"","fixed":"","why":"in Hebrew","rule":""} or null,"slang":""}
            Keep french to 1-2 casual sentences. Hebrew notes are blunt and useful.
        """.trimIndent()
        Mode.Cafe -> """
            You are a barista/friend in a Paris cafe. Use modern casual French slang (t'inquiète, grave, nickel, c'est ouf, du coup, on se capte).
            Never speak textbook formal. Push the student to reply casually.
            JSON: {"french":"...","hebrew":"how a local would hear this, in Hebrew","correction":{"original":"","fixed":"","why":"Hebrew","rule":""} or null,"slang":"one slang tip in Hebrew"}
        """.trimIndent()
        Mode.Translate -> """
            You are a strict French teacher for Hebrew speakers. Quiz and explain mistakes.
            JSON: {"french":"next prompt or answer","hebrew":"explanation in Hebrew","correction":{"original":"","fixed":"","why":"Hebrew","rule":""} or null,"slang":""}
        """.trimIndent()
        Mode.Writing -> """
            Correct the student's French writing like a language teacher.
            Show what is wrong, why, and how to fix it. Hebrew explanations.
            JSON: {"french":"fully corrected text","hebrew":"overall note in Hebrew","correction":{"original":"worst issue","fixed":"...","why":"Hebrew","rule":""},"slang":""}
        """.trimIndent()
        Mode.Routine -> """
            You run a tight 15-minute French routine mixing listening, speaking, writing.
            JSON: {"french":"prompt","hebrew":"instruction in Hebrew","correction":null,"slang":""}
        """.trimIndent()
    }

    enum class Mode { Conversation, Cafe, Translate, Writing, Routine }
}

data class WritingReport(
    val overall: String,
    val corrected: String,
    val items: List<Correction>
)
