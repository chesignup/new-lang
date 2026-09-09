package com.newlang.french.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newlang.french.NewLangApp
import com.newlang.french.ai.TutorEngine
import com.newlang.french.data.Copy
import com.newlang.french.data.Correction
import com.newlang.french.data.Lesson
import com.newlang.french.data.LocalContent
import com.newlang.french.data.QuizItem
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.Ink
import com.newlang.french.ui.theme.InkMuted
import com.newlang.french.ui.theme.Paper
import com.newlang.french.ui.theme.Sage
import com.newlang.french.ui.theme.SageSoft
import kotlinx.coroutines.launch

@Composable
fun TranslateScreen(onClose: () -> Unit, onFinished: () -> Unit) {
    val app = NewLangApp.instance
    val scope = rememberCoroutineScope()
    val item = remember { LocalContent.quizzes.random() }
    var stage by remember { mutableIntStateOf(1) }
    var choice by remember { mutableStateOf<String?>(null) }
    var typed by remember { mutableStateOf("") }
    var heard by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf(listOf<String>()) }
    var correction by remember { mutableStateOf<Correction?>(null) }
    var loading by remember { mutableStateOf(false) }

    fun judge(answer: String, expected: String): Boolean {
        val a = answer.trim().lowercase().replace(Regex("[!.?]"), "")
        val b = expected.trim().lowercase().replace(Regex("[!.?]"), "")
        return a == b || a.contains(b) || b.contains(a)
    }

    fun explain(user: String, ok: Boolean) {
        loading = true
        scope.launch {
            val reply = app.tutor.reply(
                TutorEngine.Mode.Translate,
                emptyList(),
                user,
                "Quiz sentence in Hebrew: ${item.promptHe}. Expected French: ${item.answerFr}. Student answered: $user. Correct? $ok. Explain the mistake in Hebrew if any."
            )
            notes = notes + reply.hebrew.ifBlank { if (ok) "נכון. עכשיו בלי לחגוג יותר מדי — לדרך הבאה." else "לא מדויק. תסתכלו על התיקון." }
            correction = reply.correction
            loading = false
            if (!ok) app.voice.speak(item.answerFr)
        }
    }

    ScreenColumn(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        LessonTopBar("תרגום + 3 חידונים", "משפט אחד. שלוש דרכים.", onClose, "$stage/3")
        SoftCard(modifier = Modifier.fillMaxWidth()) {
            Text("עברית", color = InkMuted, fontSize = 13.sp)
            Text(item.promptHe, color = Ink, fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            Text("רמז: ${item.hint}", color = InkMuted)
        }
        Spacer(Modifier.height(14.dp))
        when (stage) {
            1 -> QuizChoices(item, choice) { selected ->
                choice = selected
                explain(selected, judge(selected, item.answerFr))
            }
            2 -> {
                Text("דרך 2 · כתבו את הצרפתית בעצמכם", color = Ink)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Traduisez…") },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(10.dp))
                PrimaryButton(Copy.check, enabled = typed.isNotBlank() && !loading) {
                    explain(typed, judge(typed, item.answerFr))
                    stage = 3
                }
            }
            3 -> {
                Text("דרך 3 · האזנה. מה נאמר?", color = Ink)
                Spacer(Modifier.height(8.dp))
                PrimaryButton("השמיעו את המשפט") { app.voice.speak(item.answerFr) }
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.choices.shuffled().forEach { option ->
                        SoftCard(
                            color = if (heard == option) SageSoft else Paper,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { heard = option }
                        ) { Text(option, fontSize = 16.sp) }
                    }
                }
                Spacer(Modifier.height(10.dp))
                PrimaryButton("סגירת השיעור", enabled = heard != null && !loading) {
                    heard?.let { explain(it, judge(it, item.answerFr)) }
                    app.progress.award(Lesson.Translate, 3)
                    onFinished()
                }
            }
        }
        if (stage == 1 && choice != null) {
            Spacer(Modifier.height(10.dp))
            PrimaryButton("לחידון הבא") { stage = 2 }
        }
        correction?.let {
            Spacer(Modifier.height(12.dp))
            CorrectionCard(it)
        }
        notes.forEach {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Burgundy)
        }
        if (loading) LoadingRow()
        Spacer(Modifier.height(24.dp))
        GhostButton("התשובה הנכונה: ${item.answerFr}") {}
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun QuizChoices(item: QuizItem, selected: String?, onPick: (String) -> Unit) {
    Text("דרך 1 · בחירה מרובה", color = Ink)
    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item.choices.forEach { option ->
            val ok = selected != null && option == item.answerFr
            val bad = selected == option && option != item.answerFr
            SoftCard(
                color = when {
                    ok -> SageSoft
                    bad -> Paper
                    else -> Paper
                },
                modifier = Modifier.fillMaxWidth(),
                onClick = { if (selected == null) onPick(option) }
            ) {
                Row {
                    Text(option, fontSize = 16.sp, color = if (ok) Sage else Ink, modifier = Modifier.weight(1f))
                    if (ok) Text("נכון", color = Sage)
                    if (bad) Text("לא זה", color = Burgundy)
                }
            }
        }
    }
}
