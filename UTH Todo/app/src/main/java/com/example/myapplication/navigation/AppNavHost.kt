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
import com.example.myapplication.viewmodel.AuthViewModel

/** Chuyển màn và xoá sạch back stack (bấm Back sẽ không quay lại màn cũ). */
private fun NavController.navigateClearStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // ViewModel dùng chung cho Login, Home, Profile
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    // Đã đăng nhập từ trước -> vào thẳng trang chủ
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
                onOpenProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                profile = authViewModel.profile,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigateClearStack(Screen.Login.route)
                }
            )
        }
    }
}