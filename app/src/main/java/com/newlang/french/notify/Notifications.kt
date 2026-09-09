package com.newlang.french.notify

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.newlang.french.MainActivity
import com.newlang.french.R
import com.newlang.french.data.LocalContent
import com.newlang.french.data.ProgressRepository
import com.newlang.french.data.SecureSettings
import java.util.Calendar

const val CHANNEL_ID = "newlang_motivation"
private const val ACTION_NUDGE = "com.newlang.french.NUDGE"
private const val EXTRA_SLOT = "slot"

fun ensureChannel(context: Context) {
    val manager = context.getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(
        CHANNEL_ID,
        "תזכורות תרגול",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "מוטיבציה לתרגול צרפתית — לא באמצע הלילה"
        setShowBadge(true)
    }
    manager.createNotificationChannel(channel)
}

class NotificationScheduler(private val context: Context) {
    private val alarms = context.getSystemService(AlarmManager::class.java)

    fun schedule() {
        ensureChannel(context)
        val settings = SecureSettings(context).load()
        cancelAll()
        if (!settings.notificationsEnabled) return
        val count = settings.notifyCount.coerceIn(1, 3)
        repeat(count) { index ->
            val hour = settings.slotHours.getOrElse(index) { listOf(9, 13, 18)[index] }
            val minute = settings.slotMinutes.getOrElse(index) { listOf(0, 30, 30)[index] }
            if (isQuietHour(hour, settings.quietStartHour, settings.quietEndHour)) return@repeat
            val trigger = nextTrigger(hour, minute)
            val intent = Intent(context, MotivationReceiver::class.java).apply {
                action = ACTION_NUDGE
                putExtra(EXTRA_SLOT, index)
            }
            val pending = PendingIntent.getBroadcast(
                context,
                700 + index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            runCatching {
                if (Build.VERSION.SDK_INT >= 31 && !alarms.canScheduleExactAlarms()) {
                    alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
                } else {
                    alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
                }
            }.onFailure {
                alarms.set(AlarmManager.RTC_WAKEUP, trigger, pending)
            }
        }
    }

    fun cancelAll() {
        repeat(3) { index ->
            val intent = Intent(context, MotivationReceiver::class.java).apply { action = ACTION_NUDGE }
            val pending = PendingIntent.getBroadcast(
                context,
                700 + index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarms.cancel(pending)
        }
    }

    private fun nextTrigger(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis() + 15_000) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return cal.timeInMillis
    }
}

fun isQuietHour(hour: Int, start: Int, end: Int): Boolean {
    return if (start == end) false
    else if (start < end) hour in start until end
    else hour >= start || hour < end
}

class MotivationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val settings = SecureSettings(context).load()
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (settings.notificationsEnabled &&
            !isQuietHour(hour, settings.quietStartHour, settings.quietEndHour)
        ) {
            showNudge(context)
        }
        NotificationScheduler(context).schedule()
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationScheduler(context).schedule()
    }
}

private fun showNudge(context: Context) {
    ensureChannel(context)
    val progress = ProgressRepository(context).load()
    val base = LocalContent.motivation.random()
    val text = if (progress.streak > 0) {
        "רצף של ${progress.streak} ימים. $base"
    } else base
    val open = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_nudge)
        .setContentTitle("ניו לאנג · תרגול קטן")
        .setContentText(text)
        .setStyle(NotificationCompat.BigTextStyle().bigText(text))
        .setContentIntent(open)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
    context.getSystemService(NotificationManager::class.java).notify(42, notification)
}
