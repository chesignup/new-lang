# ניו לאנג · New Lang

מורה צרפתית אינטראקטיבית בעברית לאנדרואיד. לא שינון — שיחה, תיקון אמיתי, סלנג של בית קפה, ושיגרה של 15 דקות.

**מזהה קבוע:** `com.newlang.french` — עדכונים מותקנים מעל האפליקציה הקיימת, בלי הסרה.

## הורדת ה-APK

**[ניו לאנג 1.0.0 — newlang-1.0.0.apk](https://github.com/chesignup/new-lang/releases/tag/v1.0.0)**

Install: download the APK, allow unknown sources. Same app ID + keystore = update in place.

## בנייה מקומית

```bash
base64 -d keystore/newlang-release.jks.b64 > keystore/newlang-release.jks
echo "sdk.dir=$ANDROID_HOME" > local.properties
./gradlew assembleRelease
```

## שיעורים

1. שיחה של 2 דקות על היומיום
2. תרגום + 3 חידונים + תיקון טעויות
3. בית קפה בפריז (סלנג)
4. תיקון כתיבה כמו מורה
5. 10 משפטים מודרניים
6. שיגרת 15 דקות

## OpenAI

בהגדרות: מפתח נשמר מוצפן. מודל ברירת מחדל `gpt-4o-mini`. בלי מפתח — מצב דמו.

## התראות

1–3 ביום, חלונות זמן, שעות שקטות 22:00–08:00 כברירת מחדל.
