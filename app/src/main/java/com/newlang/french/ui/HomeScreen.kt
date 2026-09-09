package com.newlang.french.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newlang.french.data.Copy
import com.newlang.french.data.ProgressSnapshot
import com.newlang.french.ui.theme.Blush
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.Gold
import com.newlang.french.ui.theme.Ink
import com.newlang.french.ui.theme.InkMuted
import com.newlang.french.ui.theme.Paper
import com.newlang.french.ui.theme.Sage

@Composable
fun HomeScreen(
    progress: ProgressSnapshot,
    live: Boolean,
    onOpen: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {
                Text("ניו לאנג", style = MaterialTheme.typography.headlineLarge, color = Burgundy)
                Text(Copy.tagline, color = InkMuted)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StreakChip(progress.streak)
                }
                Spacer(Modifier.height(16.dp))
                SoftCard(color = Paper, modifier = Modifier.fillMaxWidth()) {
                    Text(Copy.homeHello, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Ink)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "שישה מצבים. שיחה, תיקון, סלנג, שיגרה. בלי כוכבים מזויפים.",
                        color = InkMuted
                    )
                    Spacer(Modifier.height(14.dp))
                    XpBar(progress)
                    Spacer(Modifier.height(14.dp))
                    Text(Copy.dailyGoal, color = InkMuted, fontSize = 13.sp)
                    LinearProgressIndicator(
                        progress = { (progress.dailyXp / progress.dailyGoal.toFloat()).coerceAtMost(1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        color = Sage,
                        trackColor = Sage.copy(alpha = 0.18f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("${progress.dailyXp} / ${progress.dailyGoal} XP היום", color = Ink, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(12.dp))
                Banner(
                    text = if (live) Copy.liveBanner else Copy.offlineBanner,
                    live = live
                )
            }
        }
        items(Copy.lessons, key = { it.id }) { lesson ->
            val done = progress.completedToday.contains(lesson.id)
            SoftCard(
                color = if (done) Blush else Paper,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOpen(lesson.id) }
            ) {
                Text(lesson.emoji, fontSize = 28.sp)
                Spacer(Modifier.height(10.dp))
                Text(lesson.title, fontWeight = FontWeight.Bold, color = Ink, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(lesson.subtitle, color = InkMuted, fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    if (done) "הושלם היום" else "להתחלה",
                    color = if (done) Sage else Gold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
        item(span = { GridItemSpan(2) }) {
            Spacer(Modifier.height(24.dp))
        }
    }
}
