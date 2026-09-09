package com.newlang.french.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newlang.french.data.Copy
import com.newlang.french.data.Correction
import com.newlang.french.data.ProgressSnapshot
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.Cream
import com.newlang.french.ui.theme.Gold
import com.newlang.french.ui.theme.Ink
import com.newlang.french.ui.theme.InkMuted
import com.newlang.french.ui.theme.Paper
import com.newlang.french.ui.theme.Sage
import com.newlang.french.ui.theme.SageSoft

@Composable
fun ScreenColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Cream)
            .padding(horizontal = 20.dp),
        content = content
    )
}

@Composable
fun LessonTopBar(title: String, subtitle: String = "", onClose: () -> Unit, trailing: String = "") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Paper,
            modifier = Modifier
                .size(42.dp)
                .clickable(onClick = onClose)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Close, contentDescription = Copy.back, tint = Ink)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = Ink)
            if (subtitle.isNotBlank()) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = InkMuted)
            }
        }
        if (trailing.isNotBlank()) {
            Text(
                trailing,
                color = Burgundy,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Burgundy, contentColor = Color.White)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
    }
}

@Composable
fun GhostButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(text, color = Burgundy, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    color: Color = Paper,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickMod = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(color)
            .then(clickMod)
            .padding(18.dp),
        content = content
    )
}

@Composable
fun XpBar(progress: ProgressSnapshot) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${Copy.level} ${progress.level}", fontWeight = FontWeight.SemiBold, color = Ink)
            Text("${progress.xpIntoLevel}/${progress.xpForLevel} XP", color = InkMuted)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.xpIntoLevel / progress.xpForLevel.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = Gold,
            trackColor = Gold.copy(alpha = 0.22f)
        )
    }
}

@Composable
fun StreakChip(days: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (days > 0) Burgundy else Ink.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🔥", fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            if (days > 0) "$days ${Copy.days}" else "אין רצף עדיין",
            color = if (days > 0) Color.White else InkMuted,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun Banner(text: String, live: Boolean) {
    val color by animateColorAsState(if (live) SageSoft else Gold.copy(alpha = 0.25f), label = "banner")
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(14.dp),
        color = Ink,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun ChatBubble(french: String, hebrew: String, fromUser: Boolean) {
    val bg = if (fromUser) Burgundy else Paper
    val fg = if (fromUser) Color.White else Ink
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (fromUser) Alignment.Start else Alignment.End
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(22.dp))
                .background(bg)
                .border(
                    width = if (fromUser) 0.dp else 1.dp,
                    color = Gold.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(14.dp)
        ) {
            Text(french, color = fg, fontSize = 18.sp, lineHeight = 26.sp)
            if (hebrew.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(hebrew, color = if (fromUser) Color.White.copy(alpha = 0.85f) else InkMuted, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun CorrectionCard(correction: Correction) {
    SoftCard(color = SageSoft, modifier = Modifier.fillMaxWidth()) {
        Text("תיקון אמיתי", fontWeight = FontWeight.Bold, color = Sage)
        Spacer(Modifier.height(8.dp))
        Text("מה שכתבתם", color = InkMuted, fontSize = 13.sp)
        Text(correction.original, color = Ink, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Text("איך אומרים", color = InkMuted, fontSize = 13.sp)
        Text(correction.fixed, color = Sage, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Text(correction.why, color = Ink)
        if (correction.rule.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text("כלל: ${correction.rule}", color = InkMuted, fontSize = 13.sp)
        }
    }
}

@Composable
fun LoadingRow() {
    Row(
        Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Burgundy, strokeWidth = 3.dp)
        Spacer(Modifier.width(10.dp))
        Text("המורה חושב…", color = InkMuted)
    }
}

@Composable
fun RowScope.StatTile(label: String, value: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(Paper)
            .padding(14.dp)
    ) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Burgundy)
        Text(label, color = InkMuted, fontSize = 13.sp)
    }
}
