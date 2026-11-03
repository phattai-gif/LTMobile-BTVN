package com.example.myapplication.navigation

/** Sealed class quản lý toàn bộ route của app. */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")

    object Onboarding : Screen("onboarding/{step}") {
        const val ARG_STEP = "step"
        fun createRoute(step: Int) = "onboarding/$step"
    }
}