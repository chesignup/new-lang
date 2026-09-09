package com.newlang.french.data

enum class Lesson(val id: String, val xp: Int) {
    Conversation("conversation", 35),
    Translate("translate", 25),
    Cafe("cafe", 30),
    Writing("writing", 25),
    Phrases("phrases", 15),
    Routine("routine", 50)
}

data class ChatTurn(
    val role: String,
    val french: String,
    val hebrew: String = "",
    val correction: Correction? = null,
    val slangNote: String = ""
)

data class Correction(
    val original: String,
    val fixed: String,
    val why: String,
    val rule: String
)

data class Phrase(
    val french: String,
    val hebrew: String,
    val whenToUse: String,
    val example: String,
    val exampleHe: String
)

data class QuizItem(
    val promptHe: String,
    val promptFr: String,
    val answerFr: String,
    val choices: List<String>,
    val hint: String
)

data class Badge(
    val id: String,
    val title: String,
    val detail: String,
    val icon: String
)

data class ProgressSnapshot(
    val xp: Int = 0,
    val streak: Int = 0,
    val lastActiveDay: String = "",
    val completedToday: Set<String> = emptySet(),
    val badges: Set<String> = emptySet(),
    val conversations: Int = 0,
    val quizzes: Int = 0,
    val writings: Int = 0,
    val cafeSessions: Int = 0,
    val routines: Int = 0,
    val minutesLearned: Int = 0,
    val dailyXp: Int = 0,
    val dailyXpDate: String = "",
    val longestStreak: Int = 0
) {
    val level: Int get() = (xp / 120) + 1
    val xpIntoLevel: Int get() = xp % 120
    val xpForLevel: Int get() = 120
    val dailyGoal: Int get() = 80
}

data class AppSettings(
    val apiKey: String = "",
    val model: String = DEFAULT_MODEL,
    val notificationsEnabled: Boolean = true,
    val notifyCount: Int = 2,
    val slotHours: List<Int> = listOf(9, 13, 18),
    val slotMinutes: List<Int> = listOf(0, 30, 30),
    val quietStartHour: Int = 22,
    val quietEndHour: Int = 8
) {
    val hasKey: Boolean get() = apiKey.isNotBlank()
}

const val DEFAULT_MODEL = "gpt-4o-mini"

val AVAILABLE_MODELS = listOf(
    ModelChoice("gpt-4o-mini", "GPT-4o mini", "ברירת המחדל — מהיר, זול, מצוין לשיחה יומיומית"),
    ModelChoice("gpt-4o", "GPT-4o", "איכות גבוהה יותר לתיקונים והסברים"),
    ModelChoice("gpt-4.1-mini", "GPT-4.1 mini", "מהיר עם הקשר ארוך"),
    ModelChoice("gpt-4.1", "GPT-4.1", "הכי מדויק לתיקון כתיבה"),
    ModelChoice("o4-mini", "o4-mini", "חשיבה עמוקה יותר, קצת יותר איטי")
)

data class ModelChoice(val id: String, val label: String, val blurb: String)
