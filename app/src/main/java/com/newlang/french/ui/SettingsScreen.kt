package com.newlang.french.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newlang.french.NewLangApp
import com.newlang.french.data.AVAILABLE_MODELS
import com.newlang.french.data.AppSettings
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.Gold
import com.newlang.french.ui.theme.Ink
import com.newlang.french.ui.theme.InkMuted
import com.newlang.french.ui.theme.Paper
import com.newlang.french.ui.theme.SageSoft

@Composable
fun SettingsScreen(onSaved: () -> Unit) {
    val app = NewLangApp.instance
    val context = LocalContext.current
    var settings by remember { mutableStateOf(app.settings.load()) }
    var revealKey by remember { mutableStateOf(false) }
    var savedFlash by remember { mutableStateOf(false) }

    val notifyPerm = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            settings = settings.copy(notificationsEnabled = true)
            persist(app, settings, context)
            onSaved()
        }
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("הגדרות", style = MaterialTheme.typography.headlineLarge, color = Burgundy)
        Text("מפתח נשמר מוצפן במכשיר. אף פעם לא עולה לענן שלנו.", color = InkMuted)
        Spacer(Modifier.height(16.dp))

        SoftCard(modifier = Modifier.fillMaxWidth()) {
            Text("מפתח OpenAI", fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = settings.apiKey,
                onValueChange = { settings = settings.copy(apiKey = it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("sk-…") },
                visualTransformation = if (revealKey) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            GhostButton(if (revealKey) "להסתיר מפתח" else "להציג מפתח") { revealKey = !revealKey }
            Text("בלי מפתח תקבלו מורה דמו. עם מפתח — תיקון חי.", color = InkMuted, fontSize = 13.sp)
        }

        Spacer(Modifier.height(12.dp))
        SoftCard(modifier = Modifier.fillMaxWidth()) {
            Text("מודל", fontWeight = FontWeight.Bold, color = Ink)
            Text("ברירת המחדל: GPT-4o mini — מהיר מספיק לשיחה, חכם מספיק לתיקון.", color = InkMuted, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            AVAILABLE_MODELS.forEach { model ->
                val selected = settings.model == model.id
                SoftCard(
                    color = if (selected) SageSoft else Paper,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onClick = { settings = settings.copy(model = model.id) }
                ) {
                    Text(model.label, fontWeight = FontWeight.SemiBold, color = Ink)
                    Text(model.blurb, color = InkMuted, fontSize = 13.sp)
                    if (model.id == "gpt-4o-mini") {
                        Text("מומלץ", color = Burgundy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        SoftCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("התראות מוטיבציה", fontWeight = FontWeight.Bold, color = Ink)
                    Text("אפשר לכוון שעות כדי שלא יעירו בלילה.", color = InkMuted, fontSize = 13.sp)
                }
                Switch(
                    checked = settings.notificationsEnabled,
                    onCheckedChange = { on ->
                        if (on && Build.VERSION.SDK_INT >= 33) {
                            notifyPerm.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        settings = settings.copy(notificationsEnabled = on)
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = Burgundy)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("כמה פעמים ביום: ${settings.notifyCount}", color = Ink)
            Slider(
                value = settings.notifyCount.toFloat(),
                onValueChange = { settings = settings.copy(notifyCount = it.toInt().coerceIn(1, 3)) },
                valueRange = 1f..3f,
                steps = 1
            )
            Text("חלונות זמן (לא יישלח בשעות השקטות)", color = InkMuted, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            settings.slotHours.indices.take(3).forEach { index ->
                TimeSlotRow(
                    index = index,
                    hour = settings.slotHours.getOrElse(index) { 9 },
                    minute = settings.slotMinutes.getOrElse(index) { 0 },
                    enabled = index < settings.notifyCount,
                    onChange = { h, m ->
                        val hours = settings.slotHours.toMutableList()
                        val minutes = settings.slotMinutes.toMutableList()
                        while (hours.size < 3) hours += listOf(9, 13, 18)[hours.size]
                        while (minutes.size < 3) minutes += listOf(0, 30, 30)[minutes.size]
                        hours[index] = h
                        minutes[index] = m
                        settings = settings.copy(slotHours = hours, slotMinutes = minutes)
                    }
                )
            }
            Spacer(Modifier.height(10.dp))
            Text("שעות שקטות — לא מפריעים", fontWeight = FontWeight.SemiBold)
            Text("ברירת מחדל 22:00–08:00. אפשר לשנות.", color = InkMuted, fontSize = 13.sp)
            QuietRow("מתחילות", settings.quietStartHour) {
                settings = settings.copy(quietStartHour = it)
            }
            QuietRow("נגמרות", settings.quietEndHour) {
                settings = settings.copy(quietEndHour = it)
            }
            if (Build.VERSION.SDK_INT >= 31) {
                GhostButton("הרשאת התראות מדויקות (אופציונלי)") {
                    val am = context.getSystemService(AlarmManager::class.java)
                    if (!am.canScheduleExactAlarms()) {
                        context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        PrimaryButton("שמירת הגדרות") {
            persist(app, settings, context)
            savedFlash = true
            onSaved()
        }
        if (savedFlash) {
            Spacer(Modifier.height(8.dp))
            Text("נשמר. המפתח נשאר רק אצלכם במכשיר.", color = Burgundy)
        }
        Spacer(Modifier.height(12.dp))
        Text("מזהה האפליקציה: com.newlang.french — לא משתנה בין עדכונים.", color = InkMuted, fontSize = 12.sp)
        Spacer(Modifier.height(28.dp))
    }
}

private fun persist(app: NewLangApp, settings: AppSettings, context: Context) {
    app.settings.save(settings)
    app.notifications.schedule()
}

@Composable
private fun TimeSlotRow(
    index: Int,
    hour: Int,
    minute: Int,
    enabled: Boolean,
    onChange: (Int, Int) -> Unit
) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(
            "חלון ${index + 1}${if (!enabled) " (כבוי לפי התדירות)" else ""}",
            color = if (enabled) Ink else InkMuted,
            fontSize = 13.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(8, 9, 12, 13, 17, 18, 20, 21).forEach { h ->
                FilterChip(
                    selected = hour == h && enabled,
                    onClick = { if (enabled) onChange(h, minute) },
                    enabled = enabled,
                    label = { Text("%02d:%02d".format(h, minute)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Burgundy,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(0, 30).forEach { m ->
                FilterChip(
                    selected = minute == m && enabled,
                    onClick = { if (enabled) onChange(hour, m) },
                    enabled = enabled,
                    label = { Text(if (m == 0) ":00" else ":30") }
                )
            }
        }
    }
}

@Composable
private fun QuietRow(label: String, value: Int, onChange: (Int) -> Unit) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text("$label · %02d:00".format(value), color = Ink)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val hours = if (label.contains("מתחילות")) listOf(21, 22, 23, 0) else listOf(6, 7, 8, 9)
            hours.forEach { h ->
                FilterChip(
                    selected = value == h,
                    onClick = { onChange(h) },
                    label = { Text("%02d".format(h)) }
                )
            }
        }
    }
}
