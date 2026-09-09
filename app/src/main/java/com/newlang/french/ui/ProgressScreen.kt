package com.newlang.french.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newlang.french.data.Copy
import com.newlang.french.data.LocalContent
import com.newlang.french.data.ProgressSnapshot
import com.newlang.french.ui.theme.Blush
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.GoldSoft
import com.newlang.french.ui.theme.Ink
import com.newlang.french.ui.theme.InkMuted
import com.newlang.french.ui.theme.Paper

@Composable
fun ProgressScreen(progress: ProgressSnapshot) {
    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text("ההתקדמות שלכם", style = MaterialTheme.typography.headlineLarge, color = Burgundy)
            Text("רצף, נקודות, תגים. בלי כוכבים מזויפים.", color = InkMuted)
            Spacer(Modifier.height(16.dp))
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Text("${Copy.level} ${progress.level}", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                Spacer(Modifier.height(8.dp))
                XpBar(progress)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile("רצף נוכחי", "${progress.streak}")
                StatTile("רצף שיא", "${progress.longestStreak}")
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile("דקות תרגול", "${progress.minutesLearned}")
                StatTile("XP כולל", "${progress.xp}")
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile("שיחות", "${progress.conversations}")
                StatTile("חידונים", "${progress.quizzes}")
            }
            Spacer(Modifier.height(20.dp))
            Text("תגים", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Ink)
            Spacer(Modifier.height(8.dp))
        }
        items(LocalContent.badges) { badge ->
            val unlocked = progress.badges.contains(badge.id)
            SoftCard(
                color = if (unlocked) GoldSoft else Paper,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("${badge.icon}  ${badge.title}", fontWeight = FontWeight.Bold, color = Ink)
                Text(
                    if (unlocked) badge.detail else "עדיין סגור. ${badge.detail}",
                    color = InkMuted
                )
            }
        }
        if (progress.xp == 0) {
            item {
                SoftCard(color = Blush, modifier = Modifier.fillMaxWidth()) {
                    Text(Copy.emptyProgress, color = Ink)
                }
                Spacer(Modifier.height(24.dp))
            }
        } else {
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}
