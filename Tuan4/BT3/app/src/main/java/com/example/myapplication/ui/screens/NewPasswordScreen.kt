package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Màn 3: Create new password */
@Composable
fun NewPasswordScreen(state: ResetPasswordState, onBack: () -> Unit, onNext: () -> Unit) {
    var password by remember { mutableStateOf(state.password) }
    var confirm by remember { mutableStateOf("") }

    val error = when {
        password.isNotEmpty() && password.length < 6 -> "Mật khẩu tối thiểu 6 ký tự"
        confirm.isNotEmpty() && confirm != password -> "Mật khẩu xác nhận không khớp"
        else -> null
    }
    val valid = password.length >= 6 && password == confirm

    AuthLayout(
        title = "Create new password",
        subtitle = "Your new password must be different form\npreviously used password",
        onBack = onBack
    ) {
        AuthTextField(password, { password = it }, "Password", Icons.Default.Lock, isPassword = true)
        Spacer(Modifier.height(14.dp))
        AuthTextField(confirm, { confirm = it }, "Confirm Password", Icons.Default.Lock, isPassword = true)
        Spacer(Modifier.height(8.dp))
        error?.let { ErrorText(it) }
        Spacer(Modifier.height(8.dp))
        PrimaryButton("Next", enabled = valid) {
            state.password = password
            onNext()
        }
    }
}