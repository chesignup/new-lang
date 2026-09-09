package com.newlang.french.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.newlang.french.NewLangApp
import com.newlang.french.ai.TutorEngine
import com.newlang.french.data.ChatTurn
import com.newlang.french.data.Copy
import com.newlang.french.data.Lesson
import com.newlang.french.data.LocalContent
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.InkMuted
import com.newlang.french.ui.theme.Paper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ConversationScreen(
    cafe: Boolean,
    onClose: () -> Unit,
    onFinished: () -> Unit
) {
    val app = NewLangApp.instance
    val scope = rememberCoroutineScope()
    val mode = if (cafe) TutorEngine.Mode.Cafe else TutorEngine.Mode.Conversation
    val title = if (cafe) "בית קפה בפריז" else "שיחה של 2 דקות"
    val subtitle = if (cafe) "סלנג. כמו מקומיים." else "חיי היומיום. מדברים."
    val opening = if (cafe) LocalContent.cafeScript.first() else LocalContent.conversationScript.first()

    val turns = remember { mutableStateListOf(opening) }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var remaining by remember { mutableIntStateOf(120) }
    var wrapped by remember { mutableStateOf(false) }
    var listening by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val mic = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startListen(app, { listening = it }, { input = it })
    }

    LaunchedEffect(Unit) {
        app.voice.prepare()
        app.voice.speak(opening.french)
        while (remaining > 0) {
            delay(1000)
            remaining--
        }
        if (!wrapped) {
            wrapped = true
            turns += ChatTurn(
                "tutor",
                "On s'arrête là. C'était vivant — pas du par cœur.",
                "שתי דקות. זה השיעור. קיבלתם נקודות על דיבור, לא על שינון."
            )
            app.progress.award(if (cafe) Lesson.Cafe else Lesson.Conversation, 2)
            onFinished()
        }
    }

    LaunchedEffect(turns.size) {
        listState.animateScrollToItem(turns.lastIndex.coerceAtLeast(0))
    }

    fun send(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || loading || wrapped) return
        input = ""
        turns += ChatTurn("user", trimmed)
        loading = true
        scope.launch {
            val reply = app.tutor.reply(mode, turns.toList(), trimmed)
            turns += reply
            loading = false
            app.voice.speak(reply.french)
        }
    }

    ScreenColumn(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        val clock = "%d:%02d".format(remaining / 60, remaining % 60)
        LessonTopBar(title, subtitle, onClose, clock)
        Banner(
            if (cafe) "דחפו תשובות קצרות וטבעיות. Grave, nickel, t'inquiète."
            else "ענו בצרפתית. טעות עדיפה על שתיקה.",
            live = app.settings.load().hasKey
        )
        Spacer(Modifier.height(10.dp))
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(turns) { turn ->
                ChatBubble(turn.french, turn.hebrew, fromUser = turn.role == "user")
                turn.correction?.let { CorrectionCard(it) }
                if (turn.slangNote.isNotBlank()) {
                    Text(turn.slangNote, color = InkMuted)
                }
            }
            if (loading) item { LoadingRow() }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                val last = turns.lastOrNull { it.role != "user" } ?: return@IconButton
                app.voice.speak(last.french)
            }) {
                Icon(Icons.Outlined.VolumeUp, contentDescription = Copy.listen, tint = Burgundy)
            }
            IconButton(onClick = {
                if (!app.voice.hasMicPermission()) {
                    mic.launch(Manifest.permission.RECORD_AUDIO)
                } else {
                    startListen(app, { listening = it }, { send(it) })
                }
            }) {
                Icon(
                    Icons.Outlined.Mic,
                    contentDescription = Copy.speak,
                    tint = if (listening) Burgundy else InkMuted
                )
            }
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (listening) "מקשיב…" else Copy.typeHint) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { send(input) }),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Paper,
                    unfocusedContainerColor = Paper,
                    focusedBorderColor = Burgundy
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        PrimaryButton(Copy.send, enabled = input.isNotBlank() && !loading) { send(input) }
        Spacer(Modifier.height(12.dp))
    }
}

private fun startListen(
    app: NewLangApp,
    listening: (Boolean) -> Unit,
    onText: (String) -> Unit
) {
    listening(true)
    app.voice.listen(
        onResult = {
            listening(false)
            onText(it)
        },
        onError = { listening(false) }
    )
}
