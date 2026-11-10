package com.example.myapplication.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TaskRepository
import com.example.myapplication.data.api.ApiClient
import com.example.myapplication.data.mock.TaskMockData
import com.example.myapplication.model.TaskCategory
import com.example.myapplication.model.TaskItem
import com.example.myapplication.util.Analytics
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TaskViewModel điều khiển dữ liệu và logic cho màn hình Trang chủ UTH Todo:
 * - Gọi GET /tasks lấy danh sách công việc
 * - Gọi PATCH /tasks/{id} khi tick hoàn thành
 * - Tính toán tiến độ hôm nay (số việc, %, câu động viên)
 * - Đếm số việc còn lại theo nhóm (mọi ngày)
 */
class TaskViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    var tasks by mutableStateOf<List<TaskItem>>(TaskMockData.sampleTasks)
        private set

    var loading by mutableStateOf(false)
        private set

    var lastSync by mutableStateOf<Long?>(System.currentTimeMillis())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var hasUnreadNotifications by mutableStateOf(true)
        private set

    // Ngày của máy dạng yyyy-MM-dd
    val deviceToday: String by lazy {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Ngày đang được chọn để hiển thị ở "Việc hôm nay".
    // Mặc định: nếu dữ liệu có việc hôm nay theo máy thì dùng deviceToday,
    // nếu không có (ví dụ mock 2026-10-20) thì ưu tiên 2026-10-20 để hiển thị khớp hình mẫu.
    var selectedDate by mutableStateOf(
        if (tasks.any { it.date == deviceToday }) deviceToday else "2026-10-20"
    )

    // Format ngày hiển thị ở Header: "Thứ Ba, 20 tháng 10"
    val headerDateString by derivedStateOf {
        formatHeaderDate(selectedDate)
    }

    // Danh sách việc hôm nay (lọc theo ngày được chọn, sắp xếp theo giờ tăng dần)
    val todayTasks by derivedStateOf {
        tasks
            .filter { it.date == selectedDate }
            .sortedBy { it.time }
    }

    // Thống kê tiến độ hôm nay
    val todayTotalCount by derivedStateOf { todayTasks.size }
    val todayDoneCount by derivedStateOf { todayTasks.count { it.done } }
    val todayProgress by derivedStateOf {
        if (todayTotalCount == 0) 0f else todayDoneCount.toFloat() / todayTotalCount
    }
    val todayPercent by derivedStateOf {
        (todayProgress * 100).toInt()
    }

    // Câu động viên theo quy tắc:
    // 100%: “Xong hết rồi, nghỉ ngơi thôi!” · ≥ 50%: “Đã hơn nửa chặng, cố lên!” · còn lại: “Bắt đầu từ việc nhỏ nhất nhé.”
    val motivationMessage by derivedStateOf {
        when {
            todayTotalCount == 0 -> "Bắt đầu từ việc nhỏ nhất nhé."
            todayPercent == 100 -> "Xong hết rồi, nghỉ ngơi thôi!"
            todayPercent >= 50 -> "Đã hơn nửa chặng, cố lên!"
            else -> "Bắt đầu từ việc nhỏ nhất nhé."
        }
    }

    // Số việc còn lại theo nhóm (mọi ngày):
    val studyRemainingCount by derivedStateOf {
        tasks.count { it.categoryEnum == TaskCategory.STUDY && !it.done }
    }
    val lifeRemainingCount by derivedStateOf {
        tasks.count { it.categoryEnum == TaskCategory.LIFE && !it.done }
    }

    init {
        loadTasks()
    }

    fun start(uid: String) {
        // Tương thích luồng đăng nhập
        loadTasks()
    }

    fun stop() {
        // Dọn dẹp nếu cần
    }

    /**
     * Gọi API GET /tasks
     */
    fun loadTasks() {
        viewModelScope.launch {
            if (ApiClient.mockApiToken.isBlank()) {
                // Khi chưa cấu hình token thật: sử dụng dữ liệu mẫu chuẩn của mock API, không báo lỗi đỏ
                tasks = repository.getLocalCachedTasks()
                errorMessage = null
                loading = false
                return@launch
            }

            loading = true
            errorMessage = null
            val result = repository.getTasks()
            if (result.isSuccess) {
                val list = result.getOrNull() ?: emptyList()
                if (list.isNotEmpty()) {
                    tasks = list
                    // Cập nhật selectedDate nếu có việc của máy
                    if (list.any { it.date == deviceToday }) {
                        selectedDate = deviceToday
                    }
                }
            } else {
                errorMessage = result.exceptionOrNull()?.message
                    ?: "Không tải được danh sách công việc. Vui lòng thử lại."
                if (tasks.isEmpty()) {
                    tasks = repository.getLocalCachedTasks()
                }
            }
            loading = false
        }
    }

    /**
     * Gọi API PATCH /tasks/{id} khi click vào checkbox
     */
    fun toggleTask(task: TaskItem, onDoneToast: () -> Unit = {}) {
        val newDone = !task.done

        // Cập nhật optimistic ngay trên UI
        tasks = tasks.map {
            if (it.id == task.id) it.copy(done = newDone) else it
        }

        if (newDone) {
            onDoneToast()
            Analytics.log("task_completed", "category" to task.category)
        }

        viewModelScope.launch {
            val result = repository.patchTaskDone(task.id, newDone)
            if (result.isSuccess) {
                val updated = result.getOrNull()
                if (updated != null) {
                    tasks = tasks.map {
                        if (it.id == updated.id) updated else it
                    }
                }
            }
        }
    }

    fun getTask(id: Long): TaskItem? = tasks.firstOrNull { it.id == id }

    fun updateSubtasks(taskId: Long, newSubtasks: List<com.example.myapplication.model.SubtaskItem>) {
        tasks = tasks.map {
            if (it.id == taskId) it.copy(subtasks = newSubtasks) else it
        }
        viewModelScope.launch {
            val result = repository.patchTask(taskId, com.example.myapplication.model.PatchTaskRequest(subtasks = newSubtasks))
            if (result.isSuccess) {
                val updated = result.getOrNull()
                if (updated != null) {
                    tasks = tasks.map { if (it.id == updated.id) updated else it }
                }
            }
        }
    }

    fun deleteTask(taskId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        tasks = tasks.filter { it.id != taskId }
        viewModelScope.launch {
            val result = repository.deleteTask(taskId)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Không thể xóa công việc")
            }
        }
    }

    fun setApiToken(token: String) {
        ApiClient.mockApiToken = token.trim()
        loadTasks()
    }

    fun switchToToday() {
        selectedDate = deviceToday
    }

    fun switchToMockDate() {
        selectedDate = "2026-10-20"
    }

    private fun formatHeaderDate(dateStr: String): String {
        return try {
            val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr) ?: Date()
            val viLocale = Locale.forLanguageTag("vi-VN")
            val formatter = SimpleDateFormat("EEEE, d 'tháng' M", viLocale)
            val formatted = formatter.format(parsedDate)
            formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(viLocale) else it.toString() }
        } catch (_: Exception) {
            "Thứ Ba, 20 tháng 10"
        }
    }
}
