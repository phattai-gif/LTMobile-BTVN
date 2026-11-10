package com.example.myapplication.model

import com.google.firebase.auth.FirebaseUser

/** OOP: data class chứa thông tin người dùng đã đăng nhập, tách khỏi FirebaseUser. */
data class UserProfile(
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val isGoogleLinked: Boolean,
    val isAnonymous: Boolean
) {
    /**
     * Quy tắc đặc tả:
     * Header | Chữ cái đầu của 2 từ cuối trong tên (“MA”), “Chào Minh Anh!”. Khách: “K”, “Chào bạn!”.
     */
    val initials: String
        get() {
            if (isAnonymous) return "K"
            val words = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
            return when {
                words.isEmpty() -> "K"
                words.size == 1 -> words.first().first().uppercase()
                else -> {
                    val lastTwo = words.takeLast(2)
                    "${lastTwo[0].first()}${lastTwo[1].first()}".uppercase()
                }
            }
        }

    /** Tên dùng trong lời chào ở trang chủ. Khách: "Chào bạn!", Đăng nhập: "Chào Minh Anh!". */
    val greetingName: String
        get() {
            if (isAnonymous) return "bạn"
            val words = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
            return when {
                words.isEmpty() -> "bạn"
                words.size == 1 -> words.first()
                else -> words.takeLast(2).joinToString(" ")
            }
        }

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
