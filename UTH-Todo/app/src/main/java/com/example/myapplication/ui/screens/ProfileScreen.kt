package com.example.myapplication.ui.screens

import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.model.TaskItem
import com.example.myapplication.model.UserProfile
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.util.Analytics
import com.example.myapplication.util.CrashReporter

private val ScreenBg = Color(0xFFF4F5F9)
private val Danger = Color(0xFFD9362B)

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    tasks: List<TaskItem>,
    lastSync: Long?,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val soon = { Toast.makeText(context, "Tính năng sắp ra mắt", Toast.LENGTH_SHORT).show() }

    LaunchedEffect(Unit) { Analytics.screen("profile") }

    val done = tasks.count { it.done }
    val pending = tasks.size - done
    val percent = if (tasks.isEmpty()) 0 else done * 100 / tasks.size
    val syncText = lastSync
        ?.let { DateUtils.getRelativeTimeSpanString(it, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString() }
        ?: "Chưa đồng bộ"

    Column(
        Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth().height(40.dp)) {
            Row(Modifier.align(Alignment.CenterStart).clickable(onClick = onBack), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
                Text("Cài đặt", color = AppColors.Primary, fontSize = 15.sp)
            }
            Text("Hồ sơ", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = AppColors.Ink, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(Modifier.height(16.dp))

        // Avatar: ảnh tài khoản Google (nếu có), không thì hiện chữ cái đầu
        val photo = profile?.photoUrl
        Box(
            Modifier.size(88.dp).clip(CircleShape).background(Color(0xFFE3E8FB)),
            contentAlignment = Alignment.Center
        ) {
            if (photo != null) {
                AsyncImage(model = photo, contentDescription = "Ảnh đại diện", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Text(profile?.initials ?: "U", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(profile?.name ?: "Khách", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink)
        Text(profile?.email ?: "", fontSize = 14.sp, color = AppColors.Muted)
        Spacer(Modifier.height(8.dp))
        if (profile?.isGoogleLinked == true) Chip("✓  Đã liên kết tài khoản Google", Color(0xFFD9F3E4), Color(0xFF1E8A4F))
        else Chip("Tài khoản khách", AppColors.Bar, AppColors.Muted)

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("$done", "Việc đã xong", Modifier.weight(1f))
            StatCard("$pending", "Đang chờ", Modifier.weight(1f))
            StatCard("$percent%", "Hoàn thành", Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        WeekCard()

        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White)) {
            SettingRow(Icons.Default.Refresh, "Đồng bộ dữ liệu", syncText, onClick = soon)
            Divider()
            SettingRow(Icons.Default.Lock, "Quyền riêng tư", null, onClick = soon)
            Divider()
            // Crashlytics: gây crash có chủ đích để xem báo cáo trên Console
            SettingRow(Icons.Default.Warning, "Thử gây crash (Crashlytics)", null) {
                CrashReporter.log("Người dùng bấm nút thử crash")
                throw RuntimeException("Test crash từ UTH Todo")
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).clickable(onClick = onLogout).padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Danger, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Đăng xuất", color = Danger, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun Divider() = Box(Modifier.fillMaxWidth().height(1.dp).background(ScreenBg))

@Composable
private fun Chip(text: String, bg: Color, fg: Color) {
    Text(
        text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = fg,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(Color.White).padding(14.dp)) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink)
        Text(label, fontSize = 12.sp, color = AppColors.Muted)
    }
}

@Composable
private fun WeekCard() {
    val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    val heights = listOf(56, 76, 34, 76, 62, 20, 20)
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White).padding(16.dp)) {
        Text("Tuần này", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppColors.Ink)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            days.forEachIndexed { i, d ->
                val barColor = when (i) {
                    1 -> AppColors.Primary
                    0 -> Color(0xFFB7C6F7)
                    else -> Color(0xFFE5E7EC)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.width(30.dp).height(heights[i].dp).clip(RoundedCornerShape(8.dp)).background(barColor))
                    Spacer(Modifier.height(6.dp))
                    Text(d, fontSize = 11.sp, color = if (i == 1) AppColors.Ink else AppColors.Muted, fontWeight = if (i == 1) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, value: String?, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AppColors.Muted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(title, fontSize = 15.sp, color = AppColors.Ink, modifier = Modifier.weight(1f))
        if (value != null) Text(value, fontSize = 14.sp, color = AppColors.Muted)
        else Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AppColors.Dot)
    }
}