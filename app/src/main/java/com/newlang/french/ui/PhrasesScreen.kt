package com.newlang.french.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newlang.french.NewLangApp
import com.newlang.french.data.Lesson
import com.newlang.french.data.LocalContent
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.Gold
import com.newlang.french.ui.theme.Ink
import com.newlang.french.ui.theme.InkMuted

@Composable
fun PhrasesScreen(onClose: () -> Unit, onFinished: () -> Unit) {
    val app = NewLangApp.instance
    val phrases = remember { LocalContent.phrases.take(10) }
    var awarded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!awarded) {
            app.progress.award(Lesson.Phrases, 5)
            awarded = true
            onFinished()
        }
    }
    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
    ) {
        item {
            LessonTopBar("10 משפטים מודרניים", "שטף זה לא פורמלי.", onClose)
            Text(
                "אלה משפטים ששומעים בצרפת. לא בספר. לחצו על הרמקול — ואז אמרו אותם בקול.",
                color = InkMuted
            )
            Spacer(Modifier.height(12.dp))
        }
        items(phrases) { phrase ->
            SoftCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text(phrase.french, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Burgundy)
                        Text(phrase.hebrew, fontSize = 16.sp, color = Ink)
                    }
                    IconButton(onClick = { app.voice.speak(phrase.french) }) {
                        Icon(Icons.Outlined.VolumeUp, contentDescription = null, tint = Gold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(phrase.whenToUse, color = InkMuted)
                Spacer(Modifier.height(6.dp))
                Text(phrase.example, fontWeight = FontWeight.Medium, color = Ink)
                Text(phrase.exampleHe, color = InkMuted, fontSize = 13.sp)
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
