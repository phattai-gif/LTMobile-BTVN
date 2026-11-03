package com.example.myapplication.data

import com.example.myapplication.util.CrashReporter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

/** Cloud Messaging: lấy token của thiết bị và lưu vào users/{uid}.fcmToken. */
object PushRepository {

    fun saveToken(uid: String, token: String) {
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .addOnFailureListener { CrashReporter.record(it) }
    }

    fun register(uid: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) saveToken(uid, task.result) else CrashReporter.record(task.exception)
        }
    }
}