package com.example.myapplication.util

import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/** Firebase Analytics: ghi lại hành vi người dùng. */
object Analytics {
    private val analytics by lazy {
        FirebaseAnalytics.getInstance(FirebaseApp.getInstance().applicationContext)
    }

    fun log(event: String, vararg params: Pair<String, String>) {
        val bundle = Bundle().apply { params.forEach { (k, v) -> putString(k, v) } }
        analytics.logEvent(event, bundle)
    }

    fun login(method: String) = log(FirebaseAnalytics.Event.LOGIN, FirebaseAnalytics.Param.METHOD to method)
    fun screen(name: String) = log(FirebaseAnalytics.Event.SCREEN_VIEW, FirebaseAnalytics.Param.SCREEN_NAME to name)
    fun setUser(uid: String?) = analytics.setUserId(uid)
}

/** Firebase Crashlytics: ghi lại lỗi để xem trên Console. */
object CrashReporter {
    fun record(t: Throwable?) {
        if (t != null) FirebaseCrashlytics.getInstance().recordException(t)
    }

    fun log(message: String) = FirebaseCrashlytics.getInstance().log(message)
    fun setUser(uid: String?) = FirebaseCrashlytics.getInstance().setUserId(uid ?: "")
}