package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Màn 4: Confirm – hiển thị lại email, otp, pass rồi Submit. */
@Composable
fun ConfirmScreen(state: ResetPasswordState, onBack: () -> Unit, onSubmit: () -> Unit) {
    AuthLayout(
        title = "Confirm",
        subtitle = "We are here to help you!",
        onBack = onBack
    ) {
        AuthTextField(state.email, {}, "Email", Icons.Default.Person, readOnly = true)
        Spacer(Modifier.height(12.dp))
        AuthTextField(state.otp, {}, "OTP", Icons.Default.Email, readOnly = true)
        Spacer(Modifier.height(12.dp))
        AuthTextField(state.password, {}, "Password", Icons.Default.Lock, isPassword = true, readOnly = true)
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Submit", onClick = onSubmit)
    }
}