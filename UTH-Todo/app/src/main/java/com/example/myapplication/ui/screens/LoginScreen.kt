package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.AuthUiState
import com.example.myapplication.ui.component.AppLogoIcon
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.uiState
    val loading = state is AuthUiState.Loading

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (state) {
            is AuthUiState.Success -> {
                Toast.makeText(context, "Thao tác thành công", Toast.LENGTH_SHORT).show()
                viewModel.consumeState()
                onSuccess()
            }
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.consumeState()
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            AppLogoIcon(size = 64.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                "Chào mừng đến với\nUTH Todo",
                fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink, lineHeight = 32.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
                fontSize = 14.sp, color = AppColors.Muted, lineHeight = 20.sp
            )
            Spacer(Modifier.height(16.dp))

            // Ô nhập Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AppColors.Muted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !loading
            )

            Spacer(Modifier.height(10.dp))

            // Ô nhập Mật khẩu
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AppColors.Muted) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = AppColors.Muted
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !loading
            )

            Spacer(Modifier.height(16.dp))

            // Hàng nút Đăng nhập & Đăng ký bằng Email
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.signInWithEmail(email.trim(), password)
                        } else {
                            Toast.makeText(context, "Vui lòng nhập đủ email và mật khẩu", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Đăng nhập", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.signUpWithEmail(email.trim(), password)
                        } else {
                            Toast.makeText(context, "Vui lòng nhập đủ email và mật khẩu", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Đăng ký", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(1.dp).background(AppColors.Bar))
                Text("  hoặc  ", fontSize = 13.sp, color = AppColors.Muted)
                Box(Modifier.weight(1f).height(1.dp).background(AppColors.Bar))
            }

            Spacer(Modifier.height(16.dp))

            // Nút Google
            OutlinedButton(
                onClick = { viewModel.signInWithGoogle(context) },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AppColors.Dot)
            ) {
                if (loading) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = AppColors.Primary)
                } else {
                    Box(
                        Modifier.size(24.dp).clip(CircleShape).background(AppColors.Bar),
                        contentAlignment = Alignment.Center
                    ) { Text("G", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.Muted) }
                    Spacer(Modifier.width(12.dp))
                    Text("Tiếp tục với Google", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Ink)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Nút dùng thử Ẩn danh
            TextButton(
                onClick = { viewModel.signInAnonymously() },
                enabled = !loading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Dùng thử không cần tài khoản", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Primary)
            }

            Spacer(Modifier.height(12.dp))
            Text(
                buildAnnotatedString {
                    append("Khi tiếp tục, bạn đồng ý với ")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("Điều khoản sử dụng") }
                    append(" và ")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("Chính sách quyền riêng tư") }
                    append(" của UTH Todo.")
                },
                fontSize = 11.sp, color = AppColors.Muted, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}