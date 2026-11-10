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
     * Gọi GET /tasks/{id}
     */
    suspend fun getTaskById(id: Long): Result<TaskItem> = withContext(Dispatchers.IO) {
        // Kiểm tra local trước
        val local = localTasks.firstOrNull { it.id == id }
        try {
            val response = api.getTask(ApiClient.authHeader(), id)
            if (response.isSuccessful && response.body() != null) {
                val item = response.body()!!
                val idx = localTasks.indexOfFirst { it.id == id }
                if (idx != -1) localTasks[idx] = item else localTasks.add(item)
                Result.success(item)
            } else if (response.code() == 404) {
                Result.failure(Exception("Không tìm thấy công việc."))
            } else if (local != null) {
                Result.success(local)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string(), response.code())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            CrashReporter.record(e)
            if (local != null) Result.success(local)
            else Result.failure(Exception("Không tải được thông tin công việc."))
        }
    }

    /**
     * Gọi PATCH /tasks/{id} với các field cần đổi (done, subtasks, title,...)
     */
    suspend fun patchTask(id: Long, request: PatchTaskRequest): Result<TaskItem> = withContext(Dispatchers.IO) {
        val idx = localTasks.indexOfFirst { it.id == id }
        if (idx != -1) {
            val cur = localTasks[idx]
            localTasks[idx] = cur.copy(
                done = request.done ?: cur.done,
                subtasks = request.subtasks ?: cur.subtasks,
                title = request.title ?: cur.title,
                note = request.note ?: cur.note,
                category = request.category ?: cur.category,
                date = request.date ?: cur.date,
                time = request.time ?: cur.time,
                priority = request.priority ?: cur.priority,
                remind = request.remind ?: cur.remind
            )
        }

        try {
            val response = api.patchTask(ApiClient.authHeader(), id, request)
            if (response.isSuccessful && response.body() != null) {
                val updated = response.body()!!
                val i = localTasks.indexOfFirst { it.id == id }
                if (i != -1) localTasks[i] = updated
                Result.success(updated)
            } else {
                val fallback = localTasks.firstOrNull { it.id == id }
                    ?: TaskItem(id = id)
                Result.success(fallback)
            }
        } catch (e: Exception) {
            CrashReporter.record(e)
            val fallback = localTasks.firstOrNull { it.id == id }
                ?: TaskItem(id = id)
            Result.success(fallback)
        }
    }

    /**
     * Gọi DELETE /tasks/{id}
     */
    suspend fun deleteTask(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        localTasks.removeAll { it.id == id }
        try {
            val response = api.deleteTask(ApiClient.authHeader(), id)
            if (response.isSuccessful || response.code() == 204) {
                Result.success(Unit)
            } else if (response.code() == 404) {
                Result.failure(Exception("Không tìm thấy công việc."))
            } else {
                Result.success(Unit) // Vẫn coi như xoá thành công phía client
            }
        } catch (e: Exception) {
            CrashReporter.record(e)
            Result.success(Unit)
        }
    }

    /**
     * Gọi PATCH /tasks/{id}
     * Cập nhật trạng thái 'done' của công việc.
     */
    suspend fun patchTaskDone(id: Long, done: Boolean): Result<TaskItem> =
        patchTask(id, PatchTaskRequest(done = done))

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
