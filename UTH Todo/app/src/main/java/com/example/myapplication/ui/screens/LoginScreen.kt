package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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

    // Thành công -> Toast + vào trang chủ. Lỗi -> Snackbar báo lỗi.
    LaunchedEffect(state) {
        when (state) {
            is AuthUiState.Success -> {
                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
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
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Spacer(Modifier.height(48.dp))
            AppLogoIcon(size = 76.dp)
            Spacer(Modifier.height(28.dp))
            Text(
                "Chào mừng đến với\nUTH Todo",
                fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink, lineHeight = 34.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
                fontSize = 15.sp, color = AppColors.Muted, lineHeight = 22.sp
            )
            Spacer(Modifier.height(24.dp))
            FeatureRow(Icons.Default.Refresh, "Tự động sao lưu, không lo mất dữ liệu")
            Spacer(Modifier.height(10.dp))
            FeatureRow(Icons.Default.Phone, "Dùng trên mọi thiết bị của bạn")

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = { viewModel.signInWithGoogle(context) },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
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
                    Text("Tiếp tục với Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Ink)
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(1.dp).background(AppColors.Bar))
                Text("  hoặc  ", fontSize = 13.sp, color = AppColors.Muted)
                Box(Modifier.weight(1f).height(1.dp).background(AppColors.Bar))
            }
            Spacer(Modifier.height(4.dp))

            TextButton(
                onClick = { viewModel.signInAnonymously() },
                enabled = !loading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Dùng thử không cần tài khoản", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Primary)
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
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 14.sp, color = AppColors.Ink)
    }
}