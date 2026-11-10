package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.model.OnboardingPage
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.OnboardingScreen
import com.example.myapplication.ui.screens.ProfileScreen
import com.example.myapplication.ui.screens.SplashScreen
import com.example.myapplication.ui.screens.TaskDetailScreen
import com.example.myapplication.ui.screens.TaskListScreen
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.TaskViewModel

private fun NavController.navigateClearStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    if (authViewModel.isLoggedIn) navController.navigateClearStack(Screen.Home.route)
                    else navController.navigateClearStack(Screen.Onboarding.createRoute(0))
                }
            )
        }

        composable(
            route = Screen.Onboarding.route,
            arguments = listOf(navArgument(Screen.Onboarding.ARG_STEP) { type = NavType.IntType })
        ) { entry ->
            val step = entry.arguments?.getInt(Screen.Onboarding.ARG_STEP) ?: 0
            val pages = OnboardingPage.all
            val goLogin = { navController.navigateClearStack(Screen.Login.route) }

            OnboardingScreen(
                page = pages[step],
                index = step,
                total = pages.size,
                onNext = {
                    if (step < pages.lastIndex) navController.navigate(Screen.Onboarding.createRoute(step + 1))
                    else goLogin()
                },
                onSkip = goLogin
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onSuccess = { navController.navigateClearStack(Screen.Home.route) }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                profile = authViewModel.profile,
                uid = authViewModel.uid,
                taskViewModel = taskViewModel,
                onOpenProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToTaskList = { category ->
                    navController.navigate(Screen.TaskList.createRoute(category)) {
                        launchSingleTop = true
                    }
                },
                onOpenTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        composable(
            route = Screen.TaskList.route,
            arguments = listOf(
                navArgument(Screen.TaskList.ARG_CATEGORY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            val cat = entry.arguments?.getString(Screen.TaskList.ARG_CATEGORY)
            TaskListScreen(
                initialCategory = cat,
                taskViewModel = taskViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onOpenTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(
                navArgument(Screen.TaskDetail.ARG_TASK_ID) {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val taskId = entry.arguments?.getLong(Screen.TaskDetail.ARG_TASK_ID) ?: 0L
            TaskDetailScreen(
                taskId = taskId,
                taskViewModel = taskViewModel,
                onBack = { navController.popBackStack() },
                onEditTask = {
                    // Tính năng mở form sửa công việc
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                profile = authViewModel.profile,
                tasks = taskViewModel.tasks,
                lastSync = taskViewModel.lastSync,
                onBack = { navController.popBackStack() },
                onLogout = {
                    taskViewModel.stop()
                    authViewModel.signOut()
                    navController.navigateClearStack(Screen.Login.route)
                }
            )
        }
    }
}