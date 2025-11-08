package com.example.babel.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.*

class NotificationPreferenceManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "babel_prefs"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val notifier = WeatherBookNotifier(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var runningJob: Job? = null

    fun setNotificationsEnabled(enabled: Boolean, apiKey: String) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).commit()

        if (enabled) {
            runningJob?.cancel() // ensure only one running instance
            runningJob = scope.launch { notifier.start(apiKey) }
        } else {
            runningJob?.cancel()
            runningJob = null
            notifier.stop() // stop all notifications immediately
        }
    }

    fun areNotificationsEnabled(): Boolean =
        prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

    fun resumeIfEnabled(apiKey: String) {
        if (areNotificationsEnabled()) {
            setNotificationsEnabled(true, apiKey)
        }
    }
}
