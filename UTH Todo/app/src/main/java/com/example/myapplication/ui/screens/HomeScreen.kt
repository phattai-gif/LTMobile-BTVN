package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.SampleTasks
import com.example.myapplication.model.TaskCategory
import com.example.myapplication.model.TaskItem
import com.example.myapplication.model.UserProfile
import com.example.myapplication.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ScreenBg = Color(0xFFF4F5F9)

@Composable
fun HomeScreen(profile: UserProfile?, onOpenProfile: () -> Unit) {
    val context = LocalContext.current
    val soon = { Toast.makeText(context, "Tính năng sắp ra mắt", Toast.LENGTH_SHORT).show() }

    val tasks = remember { mutableStateListOf(*SampleTasks.all.toTypedArray()) }
    val done = tasks.count { it.done }
    val total = tasks.size
    val progress = if (total == 0) 0f else done.toFloat() / total
    val today = remember {
        SimpleDateFormat("EEEE, d 'tháng' M", Locale("vi")).format(Date()).replaceFirstChar { it.uppercase() }
    }

    Scaffold(
        containerColor = ScreenBg,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                BarItem("Trang chủ", Icons.Default.Home, selected = true, onClick = {})
                BarItem("Danh sách", Icons.AutoMirrored.Filled.List, selected = false, onClick = soon)
                BarItem("Thông báo", Icons.Default.Notifications, selected = false, onClick = soon)
                // Từ trang chủ nhấn vào Cài đặt -> màn hình thông tin cá nhân
                BarItem("Cài đặt", Icons.Default.Settings, selected = false, onClick = onOpenProfile)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = soon, shape = CircleShape, containerColor = AppColors.Primary) {
                Icon(Icons.Default.Add, "Thêm việc", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: avatar (bấm vào -> hồ sơ) + lời chào + chuông
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFE3E8FB)).clickable(onClick = onOpenProfile),
                        contentAlignment = Alignment.Center
                    ) { Text(profile?.initials ?: "U", fontWeight = FontWeight.Bold, color = AppColors.Primary) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(today, fontSize = 13.sp, color = AppColors.Muted)
                        Text("Chào ${profile?.greetingName ?: "UTH"}!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink)
                    }
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(Color.White).clickable(onClick = soon),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Notifications, null, tint = AppColors.Primary) }
                }
            }

            // Thẻ tiến độ
            item {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(AppColors.Primary).padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Tiến độ hôm nay", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                        Text("$done/$total việc", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Bắt đầu từ việc nhỏ nhất nhé.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(64.dp),
                            strokeWidth = 6.dp,
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Text("${(progress * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // 2 thẻ nhóm
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TaskCategory.entries.forEach { cat ->
                        val remaining = tasks.count { it.category == cat && !it.done }
                        Column(
                            Modifier.weight(1f).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(16.dp)
                        ) {
                            Box(
                                Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(cat.bg),
                                contentAlignment = Alignment.Center
                            ) { Text(cat.emoji, fontSize = 20.sp) }
                            Spacer(Modifier.height(12.dp))
                            Text(cat.label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink)
                            Text("$remaining việc còn lại", fontSize = 13.sp, color = AppColors.Muted)
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Việc hôm nay", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink, modifier = Modifier.weight(1f))
                    Text("Xem tất cả", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.Primary, modifier = Modifier.clickable(onClick = soon))
                }
            }

            itemsIndexed(tasks) { index, task ->
                TaskRow(task, onToggle = { tasks[index] = task.copy(done = !task.done) })
            }
        }
    }
}

@Composable
private fun RowScope.BarItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, label) },
        label = { Text(label, fontSize = 11.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppColors.Primary,
            selectedTextColor = AppColors.Primary,
            indicatorColor = Color.Transparent,
            unselectedIconColor = AppColors.Muted,
            unselectedTextColor = AppColors.Muted
        )
    )
}

@Composable
private fun TaskRow(task: TaskItem, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (task.done) AppColors.Primary else Color.White)
                .border(1.5.dp, if (task.done) AppColors.Primary else AppColors.Dot, CircleShape)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center
        ) { if (task.done) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp)) }

        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                task.title,
                fontSize = 15.sp, fontWeight = FontWeight.Medium,
                color = if (task.done) AppColors.Muted else AppColors.Ink,
                textDecoration = if (task.done) TextDecoration.LineThrough else null
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    task.category.label,
                    fontSize = 11.sp, fontWeight = FontWeight.Medium, color = task.category.fg,
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(task.category.bg).padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(task.time, fontSize = 12.sp, color = AppColors.Muted)
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AppColors.Dot)
    }
}