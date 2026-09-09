package com.newlang.french.ui

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newlang.french.NewLangApp
import com.newlang.french.data.Lesson
import com.newlang.french.ui.theme.Ink
import com.newlang.french.ui.theme.InkMuted
import com.newlang.french.ui.theme.Sage
import kotlinx.coroutines.launch

@Composable
fun WritingScreen(onClose: () -> Unit, onFinished: () -> Unit) {
    val app = NewLangApp.instance
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var overall by remember { mutableStateOf("") }
    var corrected by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(listOf<com.newlang.french.data.Correction>()) }

    ScreenColumn(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        LessonTopBar("תיקון כתיבה", "מה שגוי, למה, ואיך מתקנים.", onClose)
        Text(
            "כתבו 3–6 משפטים בצרפתית על היום שלכם. המורה לא יחלק כוכבים — רק תיקונים שאפשר להשתמש בהם.",
            color = InkMuted
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            placeholder = { Text("Aujourd'hui j'ai…") },
            shape = RoundedCornerShape(18.dp)
        )
        Spacer(Modifier.height(12.dp))
        PrimaryButton("לבדוק כמו מורה", enabled = text.length >= 8 && !loading) {
            loading = true
            scope.launch {
                val report = app.tutor.correctWriting(text)
                overall = report.overall
                corrected = report.corrected
                items = report.items
                submitted = true
                loading = false
                app.progress.award(Lesson.Writing, 4)
                onFinished()
            }
        }
        if (loading) LoadingRow()
        if (submitted) {
            Spacer(Modifier.height(16.dp))
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Text("גרסה מתוקנת", color = Sage, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Text(corrected, color = Ink, fontSize = 18.sp, lineHeight = 26.sp)
                Spacer(Modifier.height(8.dp))
                Text(overall, color = InkMuted)
            }
            items.forEach {
                Spacer(Modifier.height(10.dp))
                CorrectionCard(it)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
