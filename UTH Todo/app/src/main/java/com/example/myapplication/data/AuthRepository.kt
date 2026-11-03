package com.example.myapplication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/** Firebase Authentication. */
class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    fun signInWithGoogle(idToken: String, onResult: (Result<FirebaseUser>) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            val user = task.result?.user
            if (task.isSuccessful && user != null) onResult(Result.success(user))
            else onResult(Result.failure(task.exception ?: Exception("Không xác định")))
        }
    }

    fun signInAnonymously(onResult: (Result<FirebaseUser>) -> Unit) {
        auth.signInAnonymously().addOnCompleteListener { task ->
            val user = task.result?.user
            if (task.isSuccessful && user != null) onResult(Result.success(user))
            else onResult(Result.failure(task.exception ?: Exception("Không xác định")))
        }
    }

    fun signOut() = auth.signOut()
}