package com.example.myapplication.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Màn 1: Forget Password? (đồng thời hiển thị kết quả sau khi Submit ở màn 4) */
@Composable
fun EmailScreen(state: ResetPasswordState, onNext: () -> Unit) {
    var email by remember { mutableStateOf(state.email) }
    var error by remember { mutableStateOf<String?>(null) }

    AuthLayout(
        title = "Forget Password?",
        subtitle = "Enter your Email, we will send you a verification code."
    ) {
        AuthTextField(
            value = email,
            onValueChange = { email = it; error = null },
            placeholder = "Your Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(8.dp))
        error?.let { ErrorText(it) }
        Spacer(Modifier.height(8.dp))
        PrimaryButton("Next") {
            if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                state.email = email.trim()
                onNext()
            } else error = "Email không hợp lệ"
        }

        // Khung kết quả: email, otp, pass (từ màn hình 4)
        state.result?.let { r ->
            Spacer(Modifier.height(32.dp))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(SmartColors.ResultBox).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Kết quả", fontSize = 12.sp, color = SmartColors.Muted)
                Spacer(Modifier.height(8.dp))
                listOf("email: ${r.email}", "otp: ${r.otp}", "pass: ${r.password}").forEach {
                    Text(it, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = SmartColors.Red, textAlign = TextAlign.Center)
                }
            }
        }
    }
}