package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.model.TaskItem
import com.example.myapplication.model.UserProfile
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.util.Analytics

private val ScreenBg = Color(0xFFF4F5F9)
private val Danger = Color(0xFFD9362B)

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    tasks: List<TaskItem>,
    lastSync: Long? = null,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onLinkGoogle: (() -> Unit)? = null
) {
    LaunchedEffect(Unit) { Analytics.screen("profile") }

    val done = tasks.count { it.done }
    val pending = tasks.size - done
    val percent = if (tasks.isEmpty()) 0 else done * 100 / tasks.size

    val isGoogleLinked = profile?.isGoogleLinked == true
    val name = profile?.name ?: "Khách"
    val emailText = if (profile == null || profile.isAnonymous || profile.email.isBlank()) {
        "Chưa liên kết tài khoản"
    } else {
        profile.email
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header: < Cài đặt        Hồ sơ
        Box(Modifier.fillMaxWidth().height(40.dp)) {
            Row(
                Modifier
                    .align(Alignment.CenterStart)
                    .clickable(onClick = onBack),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = AppColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Cài đặt", color = AppColors.Primary, fontSize = 15.sp)
            }
            Text(
                "Hồ sơ",
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = AppColors.Ink,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Avatar tròn 88dp
        val photo = profile?.photoUrl
        Box(
            Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3E8FB)),
            contentAlignment = Alignment.Center
        ) {
            if (photo != null) {
                AsyncImage(
                    model = photo,
                    contentDescription = "Ảnh đại diện",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = profile?.initials ?: "K",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink)
        Spacer(Modifier.height(4.dp))
        Text(emailText, fontSize = 14.sp, color = AppColors.Muted)
        Spacer(Modifier.height(12.dp))

        // Trạng thái tài khoản: Đã liên kết Google hoặc Nút liên kết Google
        if (isGoogleLinked) {
            Chip("✓  Đã liên kết tài khoản Google", Color(0xFFD9F3E4), Color(0xFF1E8A4F))
        } else {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E4EA), RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onLinkGoogle?.invoke() }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .border(1.2.dp, Color(0xFF757575), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "G",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF757575)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Liên kết Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Ink
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        // Thống kê 3 ô: Việc đã xong | Đang chờ | Hoàn thành
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("$done", "Việc đã xong", Modifier.weight(1f))
            StatCard("$pending", "Đang chờ", Modifier.weight(1f))
            StatCard("$percent%", "Hoàn thành", Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // Biểu đồ Tuần này
        WeekCard()

        Spacer(Modifier.height(24.dp))

        // Nút Đăng xuất
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .clickable(onClick = onLogout)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = Danger,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Đăng xuất",
                color = Danger,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Chip(text: String, bg: Color, fg: Color) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = fg,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    )
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink)
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 12.sp, color = AppColors.Muted)
    }
}

@Composable
private fun WeekCard() {
    val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    val heights = listOf(48, 32, 8, 8, 8, 8, 8)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tuần này", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppColors.Ink)
            Text("Việc xong mỗi ngày", fontSize = 13.sp, color = AppColors.Muted)
        }
        Spacer(Modifier.height(20.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { i, d ->
                val barColor = when (i) {
                    0 -> Color(0xFF9EBAF8)
                    1 -> AppColors.Primary
                    else -> Color(0xFFE5E7EC)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        Modifier
                            .width(28.dp)
                            .height(heights[i].dp)
                            .clip(RoundedCornerShape(if (heights[i] > 10) 6.dp else 4.dp))
                            .background(barColor)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        d,
                        fontSize = 12.sp,
                        color = if (i == 1) AppColors.Ink else AppColors.Muted,
                        fontWeight = if (i == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
