package com.newlang.french.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newlang.french.NewLangApp
import com.newlang.french.ai.TutorEngine
import com.newlang.french.data.Copy
import com.newlang.french.data.Lesson
import com.newlang.french.data.LocalContent
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.Gold
import com.newlang.french.ui.theme.Ink
import com.newlang.french.ui.theme.InkMuted
import kotlinx.coroutines.launch

@Composable
fun RoutineScreen(onClose: () -> Unit, onFinished: () -> Unit) {
    val app = NewLangApp.instance
    val scope = rememberCoroutineScope()
    var step by remember { mutableIntStateOf(0) }
    val steps = LocalContent.routineSteps
    var reply by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val phrase = remember { LocalContent.phrases.random() }

    ScreenColumn(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        LessonTopBar("שיגרת 15 דקות", "האזנה · דיבור · כתיבה", onClose, "${step + 1}/4")
        LinearProgressIndicator(
            progress = { (step + 1) / 4f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = Gold
        )
        Spacer(Modifier.height(16.dp))
        val current = steps[step]
        SoftCard(modifier = Modifier.fillMaxWidth()) {
            Text(current.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Burgundy)
            Spacer(Modifier.height(6.dp))
            Text(current.body, color = Ink)
        }
        Spacer(Modifier.height(14.dp))
        when (current.id) {
            "listen" -> {
                Text(phrase.french, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text(phrase.hebrew, color = InkMuted)
                Spacer(Modifier.height(12.dp))
                PrimaryButton("השמעה — ואז לחזור אחריו") { app.voice.speak(phrase.french) }
            }
            "speak" -> {
                Text("ענו בצרפתית: Ça va aujourd'hui, tu fais quoi ?", color = Ink)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reply,
                    onValueChange = { reply = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(Copy.typeHint) },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(8.dp))
                PrimaryButton("שליחה למורה", enabled = reply.isNotBlank() && !loading) {
                    loading = true
                    scope.launch {
                        val turn = app.tutor.reply(TutorEngine.Mode.Routine, emptyList(), reply)
                        note = turn.hebrew.ifBlank { turn.french }
                        app.voice.speak(turn.french)
                        loading = false
                    }
                }
                if (note.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(note, color = Burgundy)
                }
            }
            "write" -> {
                OutlinedTextField(
                    value = reply,
                    onValueChange = { reply = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    placeholder = { Text("Trois phrases sur votre journée…") },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(8.dp))
                PrimaryButton("תיקון", enabled = reply.length > 6 && !loading) {
                    loading = true
                    scope.launch {
                        val report = app.tutor.correctWriting(reply)
                        note = report.overall + "\n" + report.corrected
                        loading = false
                    }
                }
                if (note.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(note, color = Ink)
                }
            }
            else -> {
                Text("ניצחון קטן להיום: ${phrase.french}", fontSize = 18.sp, color = Ink)
                Text(phrase.hebrew, color = InkMuted)
            }
        }
        if (loading) LoadingRow()
        Spacer(Modifier.height(16.dp))
        PrimaryButton(if (step == steps.lastIndex) "סגירת השיגרה" else Copy.next) {
            if (step == steps.lastIndex) {
                app.progress.award(Lesson.Routine, 15)
                onFinished()
                onClose()
            } else {
                reply = ""
                note = ""
                step += 1
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
