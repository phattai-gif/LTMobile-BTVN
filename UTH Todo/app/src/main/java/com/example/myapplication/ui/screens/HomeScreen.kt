package com.example.myapplication.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.myapplication.data.PushRepository
import com.example.myapplication.model.TaskCategory
import com.example.myapplication.model.TaskItem
import com.example.myapplication.model.UserProfile
import com.example.myapplication.ui.theme.AppColors
import com.example.myapplication.util.Analytics
import com.example.myapplication.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ScreenBg = Color(0xFFF4F5F9)

@Composable
fun HomeScreen(
    profile: UserProfile?,
    uid: String?,
    taskViewModel: TaskViewModel,
    onOpenProfile: () -> Unit
) {
    val context = LocalContext.current
    val soon = { Toast.makeText(context, "Tính năng sắp ra mắt", Toast.LENGTH_SHORT).show() }
    var showAddDialog by remember { mutableStateOf(false) }

    // Android 13+: xin quyền hiện thông báo (Cloud Messaging)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(uid) {
        Analytics.screen("home")
        if (uid != null) {
            taskViewModel.start(uid)   // Firestore + Realtime Database
            PushRepository.register(uid) // Cloud Messaging token
            if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val tasks = taskViewModel.tasks
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
                BarItem("Cài đặt", Icons.Default.Settings, selected = false, onClick = onOpenProfile)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = CircleShape,
                containerColor = AppColors.Primary
            ) { Icon(Icons.Default.Add, "Thêm việc", tint = Color.White) }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

            // Thẻ tiến độ: bật/tắt và lời nhắn lấy từ Remote Config
            if (taskViewModel.showProgressCard) {
                item {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(AppColors.Primary).padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Tiến độ hôm nay", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                            Text("$done/$total việc", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(taskViewModel.welcomeMessage, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
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
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TaskCategory.entries.forEach { cat ->
                        val remaining = tasks.count { it.category == cat && !it.done }
                        Column(Modifier.weight(1f).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(16.dp)) {
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
                Text("Việc hôm nay", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.Ink)
            }

            if (taskViewModel.loading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppColors.Primary)
                    }
                }
            } else if (tasks.isEmpty()) {
                item {
                    Text(
                        "Chưa có việc nào. Bấm nút + để thêm việc đầu tiên!",
                        color = AppColors.Muted, fontSize = 14.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            }

            items(tasks, key = { it.id }) { task ->
                TaskRow(task, onToggle = { taskViewModel.toggle(task) }, onDelete = { taskViewModel.delete(task) })
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, category, time ->
                taskViewModel.addTask(title, category, time)
                showAddDialog = false
            }
        )
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
private fun TaskRow(task: TaskItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(start = 14.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
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
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, "Xoá việc", tint = AppColors.Muted)
        }
    }
}

@Composable
private fun AddTaskDialog(onDismiss: () -> Unit, onSave: (String, TaskCategory, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00") }
    var category by remember { mutableStateOf(TaskCategory.STUDY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm việc mới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Tên việc") }, singleLine = true)
                OutlinedTextField(time, { time = it }, label = { Text("Giờ (HH:mm)") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskCategory.entries.forEach { cat ->
                        val selected = cat == category
                        Text(
                            "${cat.emoji} ${cat.label}",
                            fontSize = 13.sp,
                            color = if (selected) cat.fg else AppColors.Muted,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) cat.bg else ScreenBg)
                                .border(1.dp, if (selected) cat.fg else AppColors.Dot, RoundedCornerShape(50))
                                .clickable { category = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = title.isNotBlank(), onClick = { onSave(title.trim(), category, time.trim()) }) {
                Text("Lưu")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}