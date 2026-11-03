package com.example.myapplication.navigation

import com.example.myapplication.model.ResetPasswordState
import com.example.myapplication.ui.screens.ConfirmScreen
import com.example.myapplication.ui.screens.EmailScreen
import com.example.myapplication.ui.screens.NewPasswordScreen
import com.example.myapplication.ui.screens.VerifyCodeScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SmartTasksNavHost() {
    val navController = rememberNavController()
    // 1 object dùng chung cho cả luồng
    val state = remember { ResetPasswordState() }

    NavHost(navController = navController, startDestination = AuthScreen.Email.route) {

        composable(AuthScreen.Email.route) {
            EmailScreen(state) { navController.navigate(AuthScreen.Verify.route) }
        }

        composable(AuthScreen.Verify.route) {
            VerifyCodeScreen(
                state,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(AuthScreen.NewPassword.route) }
            )
        }

        composable(AuthScreen.NewPassword.route) {
            NewPasswordScreen(
                state,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(AuthScreen.Confirm.route) }
            )
        }

        composable(AuthScreen.Confirm.route) {
            ConfirmScreen(
                state,
                onBack = { navController.popBackStack() },
                onSubmit = {
                    state.submit()
                    // Quay về màn 1, bỏ hết màn 2-3-4 khỏi back stack
                    navController.popBackStack(AuthScreen.Email.route, inclusive = false)
                }
            )
        }
    }
}