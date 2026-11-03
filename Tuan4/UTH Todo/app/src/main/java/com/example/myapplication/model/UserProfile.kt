package com.example.myapplication.model

import com.google.firebase.auth.FirebaseUser

/** OOP: data class chứa thông tin người dùng đã đăng nhập, tách khỏi FirebaseUser. */
data class UserProfile(
    val name: String,
    val email: String,
    val isGoogleLinked: Boolean,
    val isAnonymous: Boolean
) {
    /** Chữ cái đầu hiển thị trong avatar, ví dụ "Minh Anh" -> "MA". */
    val initials: String
        get() {
            val words = name.split(" ").filter { it.isNotBlank() }
            return when {
                words.isEmpty() -> "U"
                words.size == 1 -> words.first().first().uppercase()
                else -> "${words.first().first()}${words.last().first()}".uppercase()
            }
        }

    /** Tên dùng trong lời chào ở trang chủ. */
    val greetingName: String get() = if (isAnonymous) "UTH" else name

    companion object {
        fun from(user: FirebaseUser) = UserProfile(
            name = user.displayName?.takeIf { it.isNotBlank() }
                ?: if (user.isAnonymous) "Khách" else "Người dùng",
            email = user.email?.takeIf { it.isNotBlank() }
                ?: if (user.isAnonymous) "Chưa đăng nhập tài khoản" else "",
            isGoogleLinked = user.providerData.any { it.providerId == "google.com" },
            isAnonymous = user.isAnonymous
        )
    }
}