package com.newlang.french.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureSettings(context: Context) {

    private val encrypted: SharedPreferences = runCatching {
        val master = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "newlang_secret_prefs",
            master,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrElse {
        context.getSharedPreferences("newlang_secret_fallback", Context.MODE_PRIVATE)
    }

    private val plain = context.getSharedPreferences("newlang_settings", Context.MODE_PRIVATE)

    fun load(): AppSettings {
        val hours = plain.getString("slotHours", "9,13,18")
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?.ifEmpty { listOf(9, 13, 18) }
            ?: listOf(9, 13, 18)
        val minutes = plain.getString("slotMinutes", "0,30,30")
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?.ifEmpty { listOf(0, 30, 30) }
            ?: listOf(0, 30, 30)
        return AppSettings(
            apiKey = encrypted.getString("apiKey", "") ?: "",
            model = plain.getString("model", DEFAULT_MODEL) ?: DEFAULT_MODEL,
            notificationsEnabled = plain.getBoolean("notificationsEnabled", true),
            notifyCount = plain.getInt("notifyCount", 2).coerceIn(1, 3),
            slotHours = hours.take(3),
            slotMinutes = minutes.take(3),
            quietStartHour = plain.getInt("quietStartHour", 22),
            quietEndHour = plain.getInt("quietEndHour", 8)
        )
    }

    fun save(settings: AppSettings) {
        encrypted.edit().putString("apiKey", settings.apiKey.trim()).apply()
        plain.edit()
            .putString("model", settings.model)
            .putBoolean("notificationsEnabled", settings.notificationsEnabled)
            .putInt("notifyCount", settings.notifyCount.coerceIn(1, 3))
            .putString("slotHours", settings.slotHours.joinToString(","))
            .putString("slotMinutes", settings.slotMinutes.joinToString(","))
            .putInt("quietStartHour", settings.quietStartHour)
            .putInt("quietEndHour", settings.quietEndHour)
            .apply()
    }

    fun saveKey(key: String) {
        encrypted.edit().putString("apiKey", key.trim()).apply()
    }
}
