package com.newlang.french

import android.app.Application
import com.newlang.french.ai.TutorEngine
import com.newlang.french.data.ProgressRepository
import com.newlang.french.data.SecureSettings
import com.newlang.french.notify.NotificationScheduler
import com.newlang.french.notify.ensureChannel
import com.newlang.french.speech.VoiceTools

class NewLangApp : Application() {
    lateinit var settings: SecureSettings
        private set
    lateinit var progress: ProgressRepository
        private set
    lateinit var tutor: TutorEngine
        private set
    lateinit var voice: VoiceTools
        private set
    lateinit var notifications: NotificationScheduler
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        settings = SecureSettings(this)
        progress = ProgressRepository(this)
        tutor = TutorEngine(settings)
        voice = VoiceTools(this)
        notifications = NotificationScheduler(this)
        ensureChannel(this)
        notifications.schedule()
    }

    companion object {
        lateinit var instance: NewLangApp
            private set
    }
}
