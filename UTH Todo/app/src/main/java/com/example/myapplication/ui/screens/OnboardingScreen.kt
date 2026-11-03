package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.OnboardingPage
import com.example.myapplication.ui.component.AppLogoIcon
import com.example.myapplication.ui.component.AppLogoText
import com.example.myapplication.ui.theme.AppColors

@Composable
fun OnboardingScreen(
    page: OnboardingPage,
    index: Int,
    total: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(AppColors.White)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Header: logo + Bỏ qua
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AppLogoIcon(size = 32.dp)
            Spacer(Modifier.width(8.dp))
            AppLogoText(fontSize = 16.sp, uthColor = AppColors.Ink, todoColor = AppColors.Primary)
            Spacer(Modifier.weight(1f))
            Text(
                "Bỏ qua", color = AppColors.Muted, fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onSkip).padding(4.dp)
            )
        }

        // Minh hoạ (đa hình theo từng trang)
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            page.Illustration()
        }

        Text(page.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink, lineHeight = 32.sp)
        Spacer(Modifier.height(10.dp))
        Text(page.description, fontSize = 15.sp, color = AppColors.Muted, lineHeight = 22.sp)

        Spacer(Modifier.height(24.dp))
        PageIndicator(current = index, total = total)
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
        ) {
            Text(page.buttonText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppColors.White)
        }
    }
}

@Composable
private fun PageIndicator(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { i ->
            val active = i == current
            Box(
                Modifier
                    .height(6.dp)
                    .width(if (active) 24.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (active) AppColors.Primary else AppColors.Dot)
            )
        }
    }
}