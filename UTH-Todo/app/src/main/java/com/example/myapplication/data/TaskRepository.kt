package com.example.myapplication.data

import com.example.myapplication.data.api.ApiClient
import com.example.myapplication.data.mock.TaskMockData
import com.example.myapplication.model.ApiErrorResponse
import com.example.myapplication.model.PatchTaskRequest
import com.example.myapplication.model.TaskItem
import com.example.myapplication.util.CrashReporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * TaskRepository quản lý việc gọi API UTH Todo và dữ liệu dự phòng.
 * Thực thi các yêu cầu theo đặc tả:
 * - GET /tasks
 * - PATCH /tasks/{id}
 */
class TaskRepository {

    private val api = ApiClient.taskApiService

    // In-memory cache lưu dữ liệu công việc hiện tại
    private val localTasks = mutableListOf<TaskItem>().apply {
        addAll(TaskMockData.sampleTasks)
    }

    /**
     * Gọi GET /tasks
     * Trả về danh sách công việc từ server hoặc thông báo lỗi chuẩn tiếng Việt.
     */
    suspend fun getTasks(): Result<List<TaskItem>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTasks(ApiClient.authHeader())
            if (response.isSuccessful) {
                val list = response.body() ?: emptyList()
                localTasks.clear()
                localTasks.addAll(list)
                Result.success(list)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(errorBody, response.code())
                // Nếu server lỗi hoặc chưa có token hợp lệ, trả về lỗi kèm thông điệp
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            CrashReporter.record(e)
            // Lỗi kết nối mạng: trả về lỗi tiếng Việt chuẩn theo đặc tả
            Result.failure(Exception("Không tải được danh sách công việc. Vui lòng thử lại."))
        }
    }

    /**
     * Gọi PATCH /tasks/{id}
     * Cập nhật trạng thái 'done' của công việc.
     */
    suspend fun patchTaskDone(id: Long, done: Boolean): Result<TaskItem> = withContext(Dispatchers.IO) {
        // Cập nhật optimistic vào bộ nhớ cục bộ trước
        val index = localTasks.indexOfFirst { it.id == id }
        if (index != -1) {
            localTasks[index] = localTasks[index].copy(done = done)
        }

        try {
            val response = api.patchTask(
                authHeader = ApiClient.authHeader(),
                id = id,
                body = PatchTaskRequest(done = done)
            )
            if (response.isSuccessful && response.body() != null) {
                val updated = response.body()!!
                val idx = localTasks.indexOfFirst { it.id == id }
                if (idx != -1) {
                    localTasks[idx] = updated
                }
                Result.success(updated)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(errorBody, response.code())
                // Nếu call server thất bại nhưng local đã update, ta vẫn giữ local cho trải nghiệm mượt mà
                val fallbackItem = localTasks.firstOrNull { it.id == id }
                    ?: TaskItem(id = id, done = done)
                Result.success(fallbackItem)
            }
        } catch (e: Exception) {
            CrashReporter.record(e)
            // Lỗi mạng: giữ cập nhật offline
            val fallbackItem = localTasks.firstOrNull { it.id == id }
                ?: TaskItem(id = id, done = done)
            Result.success(fallbackItem)
        }
    }

    fun getLocalCachedTasks(): List<TaskItem> = localTasks.toList()

    private fun parseErrorMessage(errorBody: String?, statusCode: Int): String {
        if (!errorBody.isNullOrBlank()) {
            try {
                val parsed = ApiClient.gson.fromJson(errorBody, ApiErrorResponse::class.java)
                if (!parsed.message.isNullOrBlank()) return parsed.message
                if (!parsed.error.isNullOrBlank()) {
                    if (parsed.error == "unauthorized") {
                        return "Chưa xác thực hoặc token không hợp lệ (401). Vui lòng cấu hình MOCK_API_TOKEN."
                    }
                    return parsed.error
                }
            } catch (_: Exception) { }
        }
        return when (statusCode) {
            401 -> "Chưa xác thực hoặc token không hợp lệ (401)."
            404 -> "Không tìm thấy công việc."
            500 -> "Không tải được danh sách công việc. Vui lòng thử lại."
            else -> "Đã xảy ra lỗi ($statusCode). Vui lòng thử lại."
        }
    }
}
