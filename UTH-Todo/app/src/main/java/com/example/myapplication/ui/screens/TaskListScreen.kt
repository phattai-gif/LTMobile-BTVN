package com.example.myapplication.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import com.example.myapplication.model.TaskItem
import com.example.myapplication.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ScreenBg = Color(0xFFF6F8FC)
private val PrimaryBlue = Color(0xFF2B5CE6)
private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val TextLightGrey = Color(0xFF94A3B8)
private val DangerRed = Color(0xFFDC2626)
private val DangerRedBg = Color(0xFFFEE2E2)
private val SearchBg = Color(0xFFF1F3F9)

@Composable
fun TaskListScreen(
    initialCategory: String? = null,
    taskViewModel: TaskViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onOpenTaskDetail: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val soon = { msg: String -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }

    // Quản lý từ khóa tìm kiếm và tab lọc nhóm (Tất cả, Học tập, Cuộc sống)
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory ?: "all") }

    val allTasks = taskViewModel.tasks
    // Quy tắc: “N việc chưa xong”, đếm mọi việc, không theo bộ lọc
    val uncompletedCount = allTasks.count { !it.done }

    // Lọc công việc trên máy: theo category & theo tên không phân biệt hoa thường
    val filteredTasks = remember(allTasks, selectedCategory, searchQuery) {
        allTasks.filter { task ->
            val matchesCategory = when (selectedCategory.lowercase()) {
                "study" -> task.category.equals("study", ignoreCase = true)
                "life" -> task.category.equals("life", ignoreCase = true)
                else -> true
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                task.title.contains(searchQuery.trim(), ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    // Tách việc Cần làm / Đã xong (giữ thứ tự ban đầu từ server)
    val pendingTasks = filteredTasks.filter { !it.done }
    val doneTasks = filteredTasks.filter { it.done }

    Scaffold(
        containerColor = ScreenBg,
        bottomBar = {
            com.example.myapplication.ui.component.UthBottomBar(
                currentRoute = "list",
                onNavigateHome = onNavigateToHome,
                onNavigateList = { },
                onNavigateNotifications = { soon("Không có thông báo mới") },
                onNavigateProfile = onNavigateToProfile,
                onFabClick = { soon("Mở form thêm công việc mới") }
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
            // 1. Header: "Danh sách" và "N việc chưa xong"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Danh sách",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "$uncompletedCount việc chưa xong",
                        fontSize = 14.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            // 2. Ô tìm kiếm
            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Tìm công việc",
                            fontSize = 15.sp,
                            color = TextLightGrey
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Tìm kiếm",
                            tint = TextLightGrey,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Xoá tìm kiếm",
                                    tint = TextLightGrey,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SearchBg,
                        unfocusedContainerColor = SearchBg,
                        disabledContainerColor = SearchBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )
            }

            // 3. Chip bộ lọc: Tất cả / Học tập / Cuộc sống
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChipItem(
                        label = "Tất cả",
                        isSelected = selectedCategory.equals("all", true),
                        onClick = { selectedCategory = "all" }
                    )
                    FilterChipItem(
                        label = "Học tập",
                        isSelected = selectedCategory.equals("study", true),
                        onClick = { selectedCategory = "study" }
                    )
                    FilterChipItem(
                        label = "Cuộc sống",
                        isSelected = selectedCategory.equals("life", true),
                        onClick = { selectedCategory = "life" }
                    )
                }
            }

            // Lỗi khi tải dữ liệu
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
                            Text(
                                text = taskViewModel.errorMessage ?: "Lỗi tải dữ liệu",
                                fontSize = 13.sp,
                                color = DangerRed,
                                modifier = Modifier.weight(1f)
                            )
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

            // Trạng thái không khớp bộ lọc: "Không tìm thấy công việc nào."
            if (filteredTasks.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Không tìm thấy công việc nào.",
                                fontSize = 14.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // 4. Phần CẦN LÀM
                if (pendingTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "CẦN LÀM",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(pendingTasks, key = { it.id }) { task ->
                        PendingTaskRow(
                            task = task,
                            referenceDate = taskViewModel.selectedDate,
                            onToggle = {
                                taskViewModel.toggleTask(task) {
                                    Toast.makeText(context, "Tuyệt, đã xong một việc!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onClick = { onOpenTaskDetail(task.id) }
                        )
                    }
                }

                // 5. Phần ĐÃ XONG
                if (doneTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "ĐÃ XONG",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(doneTasks, key = { it.id }) { task ->
                        DoneTaskRow(
                            task = task,
                            onToggle = {
                                taskViewModel.toggleTask(task)
                            },
                            onClick = { onOpenTaskDetail(task.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Chip lọc danh mục: Tất cả / Học tập / Cuộc sống
 */
@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) TextDark else Color.White)
            .then(
                if (isSelected) Modifier
                else Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(50))
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextDark
        )
    }
}

/**
 * Dòng công việc CẦN LÀM:
 * - Ô tick
 * - Tiêu đề
 * - Chip nhóm & Thời gian (Hôm nay / Ngày mai / Tuần này, HH:mm)
 * - Chip "Gấp" khi priority = high
 */
@Composable
private fun PendingTaskRow(
    task: TaskItem,
    referenceDate: String,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val isStudy = task.category.equals("study", ignoreCase = true)
    val catLabel = if (isStudy) "Học tập" else "Cuộc sống"
    val catBg = if (isStudy) Color(0xFFEFF4FE) else Color(0xFFFDF0E7)
    val catFg = if (isStudy) PrimaryBlue else Color(0xFFC2631F)

    val dateFormatted = formatTaskDueDate(task.date, task.time, referenceDate)
    val isHighPriority = task.priority.equals("high", ignoreCase = true)

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
            // Ô tròn checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
                    .clickable(onClick = onToggle)
            )

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Text(
                        text = dateFormatted,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            // Chip "Gấp" hiện khi priority = high và chưa xong
            if (isHighPriority) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Gấp",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DangerRed,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DangerRedBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Dòng công việc ĐÃ XONG:
 * - Chỉ tên, gạch ngang.
 * - Bỏ tick thì chuyển lên Cần làm.
 */
@Composable
private fun DoneTaskRow(
    task: TaskItem,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ô tròn đã hoàn thành (màu xanh kèm checkmark)
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Bỏ hoàn thành",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            // Chỉ tên, gạch ngang
            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextLightGrey,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.weight(1f)
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
 * Định dạng ngày giờ theo quy tắc:
 * Ngày: hôm nay “Hôm nay”, +1 ngày “Ngày mai”, đến hết Chủ nhật “Tuần này”, ghép giờ: “Hôm nay, 14:00”.
 */
private fun formatTaskDueDate(dateStr: String, timeStr: String, referenceTodayStr: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val refDate = try { sdf.parse(referenceTodayStr) } catch (_: Exception) { Date() } ?: Date()
    val taskDate = try { sdf.parse(dateStr) } catch (_: Exception) { null }

    val dateLabel = if (taskDate == null) {
        dateStr
    } else {
        val refCal = Calendar.getInstance().apply {
            time = refDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val taskCal = Calendar.getInstance().apply {
            time = taskDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffDays = ((taskCal.timeInMillis - refCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        when (diffDays) {
            0 -> "Hôm nay"
            1 -> "Ngày mai"
            in 2..6 -> {
                val refDayOfWeek = refCal.get(Calendar.DAY_OF_WEEK)
                val daysUntilSunday = if (refDayOfWeek == Calendar.SUNDAY) 0 else (Calendar.SUNDAY + 7 - refDayOfWeek) % 7
                val sundayMillis = refCal.timeInMillis + daysUntilSunday * 24L * 60 * 60 * 1000
                if (taskCal.timeInMillis <= sundayMillis) {
                    "Tuần này"
                } else {
                    SimpleDateFormat("dd/MM", Locale.getDefault()).format(taskDate)
                }
            }
            else -> {
                SimpleDateFormat("dd/MM", Locale.getDefault()).format(taskDate)
            }
        }
    }

    return if (timeStr.isNotBlank()) "$dateLabel, $timeStr" else dateLabel
}
