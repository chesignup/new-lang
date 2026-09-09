package com.newlang.french.data

object LocalContent {
    val phrases = listOf(
        Phrase("C'est trop bien !", "זה ממש מעולה!", "כשמשהו כיף באמת — לא בסגנון ספר לימוד.", "Le concert ? C'est trop bien.", "הקונצרט? זה ממש מעולה."),
        Phrase("J'ai la flemme.", "אין לי כוח / התעצלתי.", "כשאין אנרגיה לעשות משהו. יומיומי לגמרי.", "Je devais sortir, mais j'ai la flemme.", "הייתי אמור לצאת, אבל אין לי כוח."),
        Phrase("Grave.", "לגמרי. ממש כן.", "הסכמה חזקה. קצר, מודרני, לא 'tout à fait'.", "— C'était nul. — Grave.", "— זה היה גרוע. — לגמרי."),
        Phrase("C'est chaud.", "זה קשה / לחוץ / מסובך.", "לא מדברים על טמפרטורה. מדברים על סיטואציה.", "Le métro à 18h, c'est chaud.", "המטרו ב־18:00 זה סיוט."),
        Phrase("T'inquiète.", "אל תדאג/י.", "קיצור של ne t'inquiète pas. ככה אומרים.", "T'inquiète, je gère.", "אל תדאג, אני על זה."),
        Phrase("Je kiffe.", "אני ממש אוהב/ת את זה.", "מערבית־צרפתית של הרחוב. חם וטבעי.", "Je kiffe trop cette série.", "אני ממש מכור לסדרה הזאת."),
        Phrase("C'est ouf.", "זה מטורף.", "ouf = fou הפוך. תגובה ספונטנית.", "Le prix du café ? C'est ouf.", "מחיר הקפה? מטורף."),
        Phrase("On se capte.", "נתראה / נהיה בקשר.", "לא au revoir מנומס. סגירת שיחה בין חברים.", "Bon allez, on se capte demain.", "יאללה, נתראה מחר."),
        Phrase("Nickel.", "מושלם / סבבה לגמרי.", "כשהכול מסתדר. קצר וענייני.", "L'appart est nickel.", "הדירה מושלמת."),
        Phrase("Pas de ouf.", "לא משהו מיוחד.", "אכזבה עדינה, לא דרמה.", "Le film est pas de ouf.", "הסרט לא משהו."),
        Phrase("Du coup…", "אז בעצם… / אז…", "מילת מעבר שהצרפתים זורקים כל שני משפטים.", "Du coup, tu viens ?", "אז בעצם, אתה בא?"),
        Phrase("C'est claqué.", "זה חלש / על הפנים.", "ביקורת כנה, לא ספרותית.", "Le Wi-Fi ici c'est claqué.", "הוויי־פיי פה על הפנים.")
    )

    val quizzes = listOf(
        QuizItem("אני מתעצל היום.", "I'm lazy today.", "J'ai la flemme aujourd'hui.", listOf("J'ai la flemme aujourd'hui.", "Je suis très fatigué formellement.", "J'ai de la paresse académique.", "Je ne veux pas travailler s'il vous plaît."), "חשבו סלנג, לא מילון."),
        QuizItem("אל תדאג, אני על זה.", "Don't worry, I got this.", "T'inquiète, je gère.", listOf("Ne vous inquiétez pas monsieur.", "T'inquiète, je gère.", "S'il vous plaît calmez-vous.", "Je suis responsable officiellement."), "קיצור + je gère."),
        QuizItem("זה ממש מעולה!", "That's so good!", "C'est trop bien !", listOf("C'est très bien, merci.", "Cela est excellent.", "C'est trop bien !", "Je trouve cela satisfaisant."), "trop bien = מחמאה אמיתית."),
        QuizItem("נתראה מחר.", "Catch you tomorrow.", "On se capte demain.", listOf("Au revoir pour demain.", "On se capte demain.", "À un prochain rendez-vous.", "Je vous salue demain."), "capte = להיות בקשר."),
        QuizItem("המצב לחוץ.", "It's a tough situation.", "C'est chaud.", listOf("Il fait chaud.", "C'est chaud.", "La température est élevée.", "C'est difficile selon le livre."), "chaud כאן = סיטואציה, לא מזג אוויר.")
    )

    val conversationScript = listOf(
        ChatTurn("tutor", "Salut ! Ça va aujourd'hui ?", "היי! מה שלומך היום? תענו בצרפתית, אפילו קצר."),
        ChatTurn("tutor", "Et ce matin, t'as fait quoi ?", "ומה עשית הבוקר? נסו משפט אחד אמיתי."),
        ChatTurn("tutor", "Pas mal. Et pour manger, tu prends quoi d'habitude ?", "יפה. ומה אתם בדרך כלל אוכלים?"),
        ChatTurn("tutor", "Grave. Moi je kiffe un café crème et c'est parti. T'es plus thé ou café ?", "לגמרי. תה או קפה?"),
        ChatTurn("tutor", "Nickel. Du coup, t'as un plan pour plus tard ?", "סבבה. אז יש תוכנית להמשך היום?"),
        ChatTurn("tutor", "Ok je te laisse — on se capte vite fait demain ?", "סוגרים בעדינות, כמו חבר. ענו כן/לא בצרפתית.")
    )

    val cafeScript = listOf(
        ChatTurn("tutor", "Hey, ça va ? Tu veux t'asseoir là ?", "המלצר/חבר מדבר קז׳ואל, לא Bonjour monsieur."),
        ChatTurn("tutor", "Tu prends quoi ? Expresso, noisette, café crème ?", "מה שותים. noisette = אספרסו עם טיפת חלב."),
        ChatTurn("tutor", "Et à manger — un croissant, ou t'as déjà pris un truc ?", "אוכל ליד הקפה. תענו בכנות."),
        ChatTurn("tutor", "C'est ouf les prix en ce moment, nan ?", "תלונה פריזאית קלאסית. הסכימו או תתווכחו."),
        ChatTurn("tutor", "Ok je te prends ça. Tu payes maintenant ou plus tard ?", "עכשיו או בסוף — תבחרו."),
        ChatTurn("tutor", "Nickel. Je reviens. T'as besoin d'autre chose ?", "אם אין — אפשר לסגור עם Ça sera tout.")
    )

    val routineSteps = listOf(
        RoutineStep("listen", "האזנה · 4 דקות", "שמעו משפט, חזרו אחריו בקול. בלי לתרגם בראש קודם."),
        RoutineStep("speak", "דיבור · 5 דקות", "שיחת יומיום קצרה. טעויות מותרות — שתיקה לא."),
        RoutineStep("write", "כתיבה · 4 דקות", "שלושה משפטים על היום שלכם. אחר כך תיקון."),
        RoutineStep("review", "סיכום · 2 דקות", "משפט אחד מודרני + נקודות. יוצאים עם ניצחון קטן.")
    )

    val badges = listOf(
        Badge("first_words", "מילים ראשונות", "סיימתם שיעור אחד. זה הרגע שבו זה נהיה אמיתי.", "🌱"),
        Badge("talker", "מדברים, לא משננים", "3 שיחות של 2 דקות.", "💬"),
        Badge("cafe_regular", "קבוע בבית הקפה", "3 סיבובים בפריז.", "☕"),
        Badge("writer", "כתיבה עם תיקון", "3 טקסטים שתוקנו באמת.", "📝"),
        Badge("slang_kid", "לא מספר לימוד", "עברתם על המשפטים המודרניים.", "🔥"),
        Badge("quiz_brain", "שלוש דרכים", "5 חידוני תרגום.", "🧠"),
        Badge("routine_hero", "15 דקות שלא נשברו", "שיגרה יומית אחת מלאה.", "⏱️"),
        Badge("streak_3", "3 ימים ברצף", "עקביות > כישרון.", "🔥"),
        Badge("streak_7", "שבוע שלם", "זה כבר הרגל.", "🏆"),
        Badge("streak_30", "חודש בצרפתית", "נדיר. מגיע לכם.", "👑")
    )

    val motivation = listOf(
        "שתי דקות בצרפתית — והרצף נשאר חי.",
        "בפריז כבר שותים קפה. בואו נתאמן על הסלנג.",
        "לא לשנן. לדבר. אפילו משפט אחד.",
        "משפט מודרני אחד היום = ביטחון מחר.",
        "הרצף שלכם מחכה. חבל לשבור אותו על סרטון נוסף.",
        "15 דקות. בלי שחיקה. רק ניצחון קטן.",
        "תיקון אחד אמיתי שווה יותר מעשרה כוכבים זהובים."
    )
}

data class RoutineStep(
    val id: String,
    val title: String,
    val body: String
)
