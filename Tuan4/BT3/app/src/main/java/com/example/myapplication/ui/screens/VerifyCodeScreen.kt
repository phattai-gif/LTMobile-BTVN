package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Màn 2: Verify Code */
@Composable
fun VerifyCodeScreen(state: ResetPasswordState, onBack: () -> Unit, onNext: () -> Unit) {
    AuthLayout(
        title = "Verify Code",
        subtitle = "Enter the code\nwe just sent you on your registered Email",
        onBack = onBack
    ) {
        OtpInput(value = state.otp, onValueChange = { state.otp = it })
        Spacer(Modifier.height(28.dp))
        PrimaryButton("Next", enabled = state.otp.length == 5, onClick = onNext)
    }
}