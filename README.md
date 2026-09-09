# ניו לאנג · New Lang

מורה צרפתית אינטראקטיבית בעברית לאנדרואיד.

**מזהה קבוע:** `com.newlang.french`

## איפה ה-APK?

הקובץ החתום **לא נמצא בתוך עץ המקור בגיטהאב** (קובץ בינארי של 12MB). כדי להתקין:

1. **בנו אותו כאן:**
   ```bash
   echo "sdk.dir=$ANDROID_HOME" > local.properties
   ./gradlew assembleRelease
   ```
   הפלט: `app/build/outputs/apk/release/app-release.apk`

2. **או העלו Release בגיטהאב:** Releases → Draft a new release → תג `v1.0.0` → העלו את ה-APK.

3. ה-keystore לחתימה נמצא ב-`keystore/newlang-release.jks.b64` (פענוח: `base64 -d keystore/newlang-release.jks.b64 > keystore/newlang-release.jks`).

## שיעורים
שיחה של 2 דקות, תרגום+3 חידונים, בית קפה בפריז, תיקון כתיבה, 10 משפטים מודרניים, שיגרת 15 דקות. XP, רצף, התראות עם שעות שקטות.

## OpenAI
בהגדרות — מפתח מוצפן במכשיר. ברירת מחדל `gpt-4o-mini`. בלי מפתח יש מצב דמו.
