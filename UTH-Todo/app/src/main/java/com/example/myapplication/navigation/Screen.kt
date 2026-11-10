package com.example.myapplication.navigation

/** Sealed class quản lý toàn bộ route của app. */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")

    object TaskList : Screen("task_list?category={category}") {
        const val ARG_CATEGORY = "category"
        fun createRoute(category: String? = null) =
            if (category != null) "task_list?category=$category" else "task_list"
    }

    object TaskDetail : Screen("task_detail/{taskId}") {
        const val ARG_TASK_ID = "taskId"
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }

    object Onboarding : Screen("onboarding/{step}") {
        const val ARG_STEP = "step"
        fun createRoute(step: Int) = "onboarding/$step"
    }
}
