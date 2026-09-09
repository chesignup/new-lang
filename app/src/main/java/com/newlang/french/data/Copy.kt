package com.newlang.french.data

object Copy {
    const val app = "ניו לאנג"
    const val tagline = "צרפתית כמו שמדברים בחיים"
    const val homeHello = "בואו נדבר, לא נשנן"
    const val streak = "רצף"
    const val days = "ימים"
    const val level = "רמה"
    const val dailyGoal = "יעד יומי"
    const val xp = "נקודות"
    const val continueCta = "להמשיך"
    const val settings = "הגדרות"
    const val progress = "התקדמות"
    const val home = "בית"
    const val back = "חזרה"
    const val send = "שליחה"
    const val speak = "דיבור"
    const val listen = "האזנה"
    const val typeHint = "כתבו בצרפתית…"
    const val needMic = "צריך הרשאת מיקרופון כדי לדבר"
    const val needNotify = "כדי לקבל תזכורות, אשרו התראות"
    const val offlineBanner = "בלי מפתח OpenAI תקבלו מורה דמו. להוספת מפתח — הגדרות."
    const val liveBanner = "המורה החי מחובר. תשובות אמיתיות, לא כוכבים זהובים."
    const val errorGeneric = "משהו השתבש. נסו שוב בעוד רגע."
    const val apiError = "OpenAI החזיר שגיאה. בדקו את המפתח והמודל בהגדרות."
    const val emptyProgress = "עדיין אין רצף. שיעור אחד קטן — וזה מתחיל."
    const val done = "סיימתי"
    const val next = "הבא"
    const val check = "בדיקה"
    const val skip = "דלג"
    const val restart = "שיעור חדש"
    const val minutesShort = "דק׳"

    val lessons = listOf(
        LessonCopy("conversation", "שיחה של 2 דקות", "חיי היומיום. מדברים — לא משננים.", "💬"),
        LessonCopy("translate", "תרגום + 3 חידונים", "משפט אחד, שלוש דרכים, תיקון אמיתי.", "✍️"),
        LessonCopy("cafe", "בית קפה בפריז", "סלנג טבעי, כמו מקומיים.", "☕"),
        LessonCopy("writing", "תיקון כתיבה", "מה שגוי, למה, ואיך מתקנים.", "📝"),
        LessonCopy("phrases", "10 משפטים מודרניים", "שטף זה לא פורמלי.", "🔥"),
        LessonCopy("routine", "שיגרת 15 דקות", "האזנה, דיבור, כתיבה. בלי שחיקה.", "⏱️")
    )
}

data class LessonCopy(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String
)
