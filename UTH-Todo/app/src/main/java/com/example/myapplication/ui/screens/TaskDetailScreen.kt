package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.SubtaskItem
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
private val DividerGrey = Color(0xFFF1F5F9)

@Composable
fun TaskDetailScreen(
    taskId: Long,
    taskViewModel: TaskViewModel,
    onBack: () -> Unit,
    onEditTask: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val task = taskViewModel.getTask(taskId)

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var editingSubtaskIndex by remember { mutableStateOf<Int?>(null) }
    var newSubtaskText by remember { mutableStateOf("") }

    // Nếu không tìm thấy công việc
    if (task == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Không tìm thấy công việc.",
                    fontSize = 16.sp,
                    color = TextMuted
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Quay lại")
                }
            }
        }
        return
    }

    val isStudy = task.category.equals("study", ignoreCase = true)
    val catLabel = if (isStudy) "Học tập" else "Cuộc sống"
    val catBg = if (isStudy) Color(0xFFEFF4FE) else Color(0xFFFDF0E7)
    val catFg = if (isStudy) PrimaryBlue else Color(0xFFC2631F)

    val priorityLabel = when (task.priority.lowercase()) {
        "high" -> "Ưu tiên cao"
        "medium" -> "Ưu tiên vừa"
        else -> "Ưu tiên thấp"
    }
    val isHighPriority = task.priority.equals("high", ignoreCase = true)
    val priorityBg = if (isHighPriority) DangerRedBg else Color(0xFFF1F5F9)
    val priorityFg = if (isHighPriority) DangerRed else Color(0xFF475569)

    val dueDateString = formatTaskDueDate(task.date, task.time, taskViewModel.selectedDate)
    val remindString = if (task.remind) "Trước 15 phút" else "Tắt"

    val subtasks = task.subtasks
    val totalSubtasks = subtasks.size
    val doneSubtasks = subtasks.count { it.done }
    val subtaskProgress = if (totalSubtasks == 0) 0f else doneSubtasks.toFloat() / totalSubtasks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // 1. Thanh tiêu đề trên cùng: nút quay lại và nút Sửa
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBack)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Quay lại",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Danh sách",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
            }

            Text(
                text = "Sửa",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onEditTask(task.id) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // 2. Các thẻ Chip: Nhóm, Mức ưu tiên, Đã xong
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chip Nhóm
            Text(
                text = catLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = catFg,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(catBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )

            // Chip Mức ưu tiên (Cao thì tô đỏ)
            Text(
                text = priorityLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = priorityFg,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(priorityBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )

            // Chip Đã xong nếu done = true
            if (task.done) {
                Text(
                    text = "Đã xong",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF16A34A),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 3. Tiêu đề công việc
        Text(
            text = task.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = if (task.done) TextLightGrey else TextDark,
            textDecoration = if (task.done) TextDecoration.LineThrough else null,
            lineHeight = 34.sp
        )

        Spacer(Modifier.height(20.dp))

        // 4. Khối Thông tin: Hạn chót & Nhắc nhở
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                // Hạn chót
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Hạn chót",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Hạn chót", fontSize = 14.sp, color = TextMuted)
                    }
                    Text(
                        text = dueDateString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                }

                HorizontalDivider(color = DividerGrey, thickness = 1.dp)

                // Nhắc nhở
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Nhắc nhở",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Nhắc nhở", fontSize = 14.sp, color = TextMuted)
                    }
                    Text(
                        text = remindString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                }
            }
        }

        // 5. Khối Ghi chú (Ẩn cả khối khi note là chuỗi rỗng)
        if (task.note.isNotBlank()) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Ghi chú",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Spacer(Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.note,
                    fontSize = 14.sp,
                    color = TextDark,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(18.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // 6. Khối Việc nhỏ (Đếm, thanh tiến độ, danh sách, thêm việc nhỏ)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Việc nhỏ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "$doneSubtasks/$totalSubtasks",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
        }

        Spacer(Modifier.height(8.dp))

        // Thanh tiến độ việc nhỏ
        LinearProgressIndicator(
            progress = { subtaskProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryBlue,
            trackColor = Color(0xFFE2E8F0)
        )

        Spacer(Modifier.height(12.dp))

        // Danh sách các việc nhỏ
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                if (subtasks.isEmpty()) {
                    Text(
                        text = "Chưa có việc nhỏ nào. Chia nhỏ để dễ bắt đầu hơn.",
                        fontSize = 14.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                    )
                    HorizontalDivider(color = DividerGrey, thickness = 1.dp)
                } else {
                    subtasks.forEachIndexed { index, subtask ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ô tick vuông bo góc
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (subtask.done) PrimaryBlue else Color.White)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (subtask.done) PrimaryBlue else Color(0xFFCBD5E1),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        val updatedList = subtasks.toMutableList().apply {
                                            this[index] = subtask.copy(done = !subtask.done)
                                        }
                                        taskViewModel.updateSubtasks(task.id, updatedList)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (subtask.done) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Xong",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            // Tên việc nhỏ
                            Text(
                                text = subtask.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = if (subtask.done) TextLightGrey else TextDark,
                                textDecoration = if (subtask.done) TextDecoration.LineThrough else null,
                                modifier = Modifier.weight(1f)
                            )

                            // Nút sửa tên việc nhỏ
                            IconButton(
                                onClick = { editingSubtaskIndex = index },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Sửa",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Nút xóa việc nhỏ
                            IconButton(
                                onClick = {
                                    val updatedList = subtasks.toMutableList().apply { removeAt(index) }
                                    taskViewModel.updateSubtasks(task.id, updatedList)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Xoá",
                                    tint = DangerRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = DividerGrey, thickness = 1.dp)
                    }
                }

                // Hàng thêm việc nhỏ mới
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (newSubtaskText.isEmpty()) {
                            Text(
                                text = "Thêm việc nhỏ",
                                fontSize = 14.sp,
                                color = TextLightGrey
                            )
                        }
                        BasicTextField(
                            value = newSubtaskText,
                            onValueChange = { newSubtaskText = it },
                            textStyle = TextStyle(fontSize = 14.sp, color = TextDark),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newSubtaskText.isNotBlank()) {
                                    val updated = subtasks + SubtaskItem(newSubtaskText.trim(), false)
                                    taskViewModel.updateSubtasks(task.id, updated)
                                    newSubtaskText = ""
                                }
                            }),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (newSubtaskText.isNotBlank()) {
                        Text(
                            text = "Thêm",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            modifier = Modifier
                                .clickable {
                                    val updated = subtasks + SubtaskItem(newSubtaskText.trim(), false)
                                    taskViewModel.updateSubtasks(task.id, updated)
                                    newSubtaskText = ""
                                }
                                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // 7. Nút Đánh dấu hoàn thành / chưa xong
        Button(
            onClick = {
                taskViewModel.toggleTask(task) {
                    Toast.makeText(context, "Tuyệt, đã xong một việc!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (task.done) "Đánh dấu chưa xong" else "Đánh dấu hoàn thành",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(14.dp))

        // 8. Nút Xóa công việc
        TextButton(
            onClick = { showDeleteConfirmDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Xóa công việc",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DangerRed
            )
        }

        Spacer(Modifier.height(24.dp))
    }

    // Dialog xác nhận xóa công việc (Quy tắc: Hỏi "Xóa việc này? Không thể hoàn tác.")
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Xác nhận xóa", fontWeight = FontWeight.Bold) },
            text = { Text("Xóa việc này? Không thể hoàn tác.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        taskViewModel.deleteTask(
                            taskId = task.id,
                            onSuccess = {
                                Toast.makeText(context, "Đã xóa công việc", Toast.LENGTH_SHORT).show()
                                onBack()
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Xóa", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Hủy", color = TextMuted)
                }
            }
        )
    }

    // Dialog sửa tên việc nhỏ
    editingSubtaskIndex?.let { idx ->
        if (idx in subtasks.indices) {
            var editingTitle by remember { mutableStateOf(subtasks[idx].title) }
            AlertDialog(
                onDismissRequest = { editingSubtaskIndex = null },
                title = { Text("Sửa việc nhỏ", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = editingTitle,
                        onValueChange = { editingTitle = it },
                        label = { Text("Tên việc nhỏ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editingTitle.isNotBlank()) {
                                val updated = subtasks.toMutableList().apply {
                                    this[idx] = this[idx].copy(title = editingTitle.trim())
                                }
                                taskViewModel.updateSubtasks(task.id, updated)
                            }
                            editingSubtaskIndex = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Lưu")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingSubtaskIndex = null }) {
                        Text("Hủy", color = TextMuted)
                    }
                }
            )
        }
    }
}

/**
 * Định dạng ngày giờ hiển thị
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
                if (taskCal.timeInMillis <= sundayMillis) "Tuần này"
                else SimpleDateFormat("dd/MM", Locale.getDefault()).format(taskDate)
            }
            else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(taskDate)
        }
    }

    return if (timeStr.isNotBlank()) "$dateLabel, $timeStr" else dateLabel
}
