package com.example.myapplication.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * Remote Config: đổi nội dung app từ Console mà không cần cập nhật app.
 * Các khoá: welcome_message (String), show_progress_card (Boolean).
 */
class RemoteConfigRepository {

    private val config = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(60).build()
        )
        setDefaultsAsync(
            mapOf<String, Any>(
                KEY_WELCOME to "Bắt đầu từ việc nhỏ nhất nhé.",
                KEY_SHOW_PROGRESS to true
            )
        )
    }

    fun fetch(onDone: () -> Unit) {
        config.fetchAndActivate().addOnCompleteListener { onDone() }
    }

    fun welcomeMessage(): String = config.getString(KEY_WELCOME)
    fun showProgressCard(): Boolean = config.getBoolean(KEY_SHOW_PROGRESS)

    companion object {
        const val KEY_WELCOME = "welcome_message"
        const val KEY_SHOW_PROGRESS = "show_progress_card"
    }
}