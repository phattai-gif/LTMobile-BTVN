package com.example.myapplication.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.component.AppLogoIcon
import com.example.myapplication.ui.component.AppLogoText
import com.example.myapplication.ui.theme.AppColors

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis = 2000, easing = LinearEasing))
        onFinished()
    }

    Box(
        Modifier.fillMaxSize().background(AppColors.Primary).systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppLogoIcon(size = 96.dp, background = AppColors.White)
            Spacer(Modifier.height(20.dp))
            AppLogoText(fontSize = 34.sp, uthColor = AppColors.White, todoColor = AppColors.White.copy(alpha = 0.85f))
            Spacer(Modifier.height(6.dp))
            Text(
                "Việc học, việc nhà — gọn gàng mỗi ngày",
                color = AppColors.White.copy(alpha = 0.9f),
                fontSize = 13.sp, textAlign = TextAlign.Center
            )
        }

        Column(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.width(120.dp).height(3.dp).clip(CircleShape).background(AppColors.White.copy(alpha = 0.3f))) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress.value).background(AppColors.White))
            }
            Spacer(Modifier.height(10.dp))
            Text("Phiên bản 2.1.0", color = AppColors.White.copy(alpha = 0.85f), fontSize = 11.sp)
        }
    }
}