package com.example.myapplication.navigation

/** Sealed class quản lý route của 4 màn hình. */
sealed class AuthScreen(val route: String) {
    object Email : AuthScreen("email")
    object Verify : AuthScreen("verify")
    object NewPassword : AuthScreen("new_password")
    object Confirm : AuthScreen("confirm")
}