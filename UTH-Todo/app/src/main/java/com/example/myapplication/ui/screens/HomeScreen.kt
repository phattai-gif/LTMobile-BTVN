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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
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
import com.example.myapplication.data.api.ApiClient
import com.example.myapplication.model.TaskCategory
import com.example.myapplication.model.TaskItem
import com.example.myapplication.model.UserProfile
import com.example.myapplication.viewmodel.TaskViewModel

// Bảng màu chuẩn theo giao diện thiết kế
private val ScreenBg = Color(0xFFF6F8FC)
private val PrimaryBlue = Color(0xFF2B5CE6)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val TextLightGrey = Color(0xFF94A3B8)
private val BorderGrey = Color(0xFFE2E8F0)
private val DangerRed = Color(0xFFEF4444)

@Composable
fun HomeScreen(
    profile: UserProfile?,
    uid: String?,
    taskViewModel: TaskViewModel,
    onOpenProfile: () -> Unit,
    onNavigateToTaskList: (String?) -> Unit = {},
    onOpenTaskDetail: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val soon = { msg: String -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    var showTokenDialog by remember { mutableStateOf(false) }

    // Android 13+: xin quyền thông báo
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(uid) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val todayTasks = taskViewModel.todayTasks
    val todayDone = taskViewModel.todayDoneCount
    val todayTotal = taskViewModel.todayTotalCount
    val progress = taskViewModel.todayProgress
    val percent = taskViewModel.todayPercent

    Scaffold(
        containerColor = ScreenBg,
        bottomBar = {
            com.example.myapplication.ui.component.UthBottomBar(
                currentRoute = "home",
                onNavigateHome = { },
                onNavigateList = { onNavigateToTaskList(null) },
                onNavigateNotifications = { soon("Không có thông báo mới") },
                onNavigateProfile = onOpenProfile,
                onFabClick = { showTokenDialog = true }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header (Avatar, Ngày của máy, Lời chào, Chuông thông báo)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar chữ cái đầu: Khách là "K", người dùng là 2 chữ cái đầu 2 từ cuối ("MA")
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8EFFF))
                            .clickable(onClick = onOpenProfile),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.initials ?: "K",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    // Ngày và lời chào
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = taskViewModel.headerDateString,
                            fontSize = 13.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Chào ${profile?.greetingName ?: "bạn"}!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    // Chuông thông báo có chấm đỏ khi còn thông báo chưa đọc
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, BorderGrey, CircleShape)
                            .clickable {
                                soon("Bạn có 1 thông báo hệ thống.")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Thông báo",
                            tint = TextDark,
                            modifier = Modifier.size(22.dp)
                        )

                        // Chấm đỏ thông báo
                        if (taskViewModel.hasUnreadNotifications) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 11.dp, end = 12.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(DangerRed)
                            )
                        }
                    }
                }
            }

            // 2. Thẻ Tiến độ hôm nay (Chỉ tính việc có date = hôm nay: "2/4 việc", "50%", câu động viên)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(PrimaryBlue)
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Tiến độ hôm nay",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "$todayDone/$todayTotal việc",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = taskViewModel.motivationMessage,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.88f),
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // Vòng tròn tiến độ %
                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 7.dp,
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.28f)
                        )
                        Text(
                            text = "$percent%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // 3. Hai thẻ nhóm công việc: Học tập / Cuộc sống (Đếm số việc chưa xong, mọi ngày)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Thẻ Học tập
                    CategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Học tập",
                        remainingText = "${taskViewModel.studyRemainingCount} việc còn lại",
                        icon = Icons.Default.School,
                        iconBg = Color(0xFFEFF4FE),
                        iconTint = PrimaryBlue,
                        onClick = {
                            onNavigateToTaskList("study")
                        }
                    )

                    // Thẻ Cuộc sống
                    CategoryCard(
                        modifier = Modifier.weight(1f),
                        title = "Cuộc sống",
                        remainingText = "${taskViewModel.lifeRemainingCount} việc còn lại",
                        icon = Icons.Default.Home,
                        iconBg = Color(0xFFFDF0E7),
                        iconTint = Color(0xFFC2631F),
                        onClick = {
                            onNavigateToTaskList("life")
                        }
                    )
                }
            }

            // 4. Tiêu đề "Việc hôm nay" & "Xem tất cả"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Việc hôm nay",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Xem tất cả",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.clickable {
                            onNavigateToTaskList(null)
                        }
                    )
                }
            }

            // Thông báo lỗi nếu gọi API thất bại (Quy tắc: Hiện message, có nút Thử lại)
            if (taskViewModel.errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEB)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = taskViewModel.errorMessage ?: "Lỗi tải dữ liệu",
                                    fontSize = 13.sp,
                                    color = DangerRed
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            FilledTonalButton(
                                onClick = { taskViewModel.loadTasks() },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White,
                                    contentColor = DangerRed
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Thử lại", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Trạng thái đang tải
            if (taskViewModel.loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            }
            // Trạng thái rỗng: "Hôm nay chưa có việc nào. Bấm + để thêm nhé."
            else if (todayTasks.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Hôm nay chưa có việc nào. Bấm + để thêm nhé.",
                                fontSize = 14.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(14.dp))
                            // Nút chuyển nhanh sang ngày mock (20/10/2026) để kiểm tra giao diện mẫu
                            OutlinedButton(
                                onClick = { taskViewModel.switchToMockDate() },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                            ) {
                                Text("Xem dữ liệu mẫu 20/10/2026", color = PrimaryBlue, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
            // Danh sách các việc hôm nay
            else {
                items(todayTasks, key = { it.id }) { task ->
                    TaskCardRow(
                        task = task,
                        onToggle = {
                            taskViewModel.toggleTask(task) {
                                Toast.makeText(context, "Tuyệt, đã xong một việc!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClick = {
                            onOpenTaskDetail(task.id)
                        }
                    )
                }
            }
        }
    }

    // Dialog nhập Token hoặc đổi cấu hình API
    if (showTokenDialog) {
        ApiTokenConfigDialog(
            currentToken = ApiClient.mockApiToken,
            onDismiss = { showTokenDialog = false },
            onSave = { token ->
                taskViewModel.setApiToken(token)
                showTokenDialog = false
                Toast.makeText(context, "Đã lưu token và tải lại dữ liệu", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Thẻ hiển thị nhóm công việc (Học tập / Cuộc sống)
 */
@Composable
private fun CategoryCard(
    modifier: Modifier = Modifier,
    title: String,
    remainingText: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = remainingText,
                fontSize = 13.sp,
                color = TextLightGrey
            )
        }
    }
}

/**
 * Một dòng công việc trong danh sách hôm nay.
 * Theo quy tắc:
 * - Tên, chip nhóm, giờ.
 * - Đã xong thì gạch ngang tên.
 * - Tick gọi PATCH /tasks/{id} với done đảo lại.
 */
@Composable
private fun TaskCardRow(
    task: TaskItem,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val isStudy = task.category.equals("study", ignoreCase = true)
    val catLabel = if (isStudy) "Học tập" else "Cuộc sống"
    val catBg = if (isStudy) Color(0xFFEFF4FE) else Color(0xFFFDF0E7)
    val catFg = if (isStudy) PrimaryBlue else Color(0xFFC2631F)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút checkbox tick tròn
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (task.done) PrimaryBlue else Color.White)
                    .border(
                        width = 1.5.dp,
                        color = if (task.done) PrimaryBlue else Color(0xFFCBD5E1),
                        shape = CircleShape
                    )
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                if (task.done) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Hoàn thành",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            // Nội dung công việc: Tiêu đề, nhãn danh mục, giờ
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (task.done) TextLightGrey else TextDark,
                    textDecoration = if (task.done) TextDecoration.LineThrough else null
                )
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Chip phân loại nhóm
                    Text(
                        text = catLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = catFg,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(catBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    // Giờ hẹn
                    Text(
                        text = task.time,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            // Mũi tên mở chi tiết
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Chi tiết",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Mục thanh điều hướng dưới cùng
 */
@Composable
private fun RowScope.BarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = PrimaryBlue,
            selectedTextColor = PrimaryBlue,
            indicatorColor = Color.Transparent,
            unselectedIconColor = TextLightGrey,
            unselectedTextColor = TextLightGrey
        )
    )
}

/**
 * Dialog cấu hình MOCK_API_TOKEN để gọi API thật
 */
@Composable
private fun ApiTokenConfigDialog(
    currentToken: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var token by remember { mutableStateOf(currentToken) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cấu hình MOCK_API_TOKEN", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Nhập token được cấp để gọi API https://share.tryapps.store/mock/teach/tasks qua Bearer Authorization.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Token") },
                    placeholder = { Text("Dán MOCK_API_TOKEN tại đây") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(token) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Lưu & Tải lại")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = TextMuted)
            }
        }
    )
}
