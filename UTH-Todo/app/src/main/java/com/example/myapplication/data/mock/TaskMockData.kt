package com.example.myapplication.data.mock

import com.example.myapplication.model.SubtaskItem
import com.example.myapplication.model.TaskItem

object TaskMockData {
    val sampleTasks: List<TaskItem> = listOf(
        TaskItem(
            id = 4L,
            title = "Tập thể dục 30 phút",
            note = "",
            category = "life",
            date = "2026-10-20",
            time = "06:30",
            priority = "medium",
            remind = true,
            done = true,
            subtasks = emptyList()
        ),
        TaskItem(
            id = 1L,
            title = "Nộp bài tập Lập trình di động",
            note = "Nộp file .zip lên hệ thống của trường. Nhớ kèm ảnh chụp màn hình app.",
            category = "study",
            date = "2026-10-20",
            time = "09:00",
            priority = "high",
            remind = true,
            done = true,
            subtasks = listOf(
                SubtaskItem(title = "Hoàn thiện màn hình đăng nhập", done = true),
                SubtaskItem(title = "Chụp ảnh màn hình app", done = true),
                SubtaskItem(title = "Nén file và nộp bài", done = true)
            )
        ),
        TaskItem(
            id = 2L,
            title = "Ôn chương 3 – Cấu trúc dữ liệu",
            note = "Tập trung phần cây nhị phân và bảng băm.",
            category = "study",
            date = "2026-10-20",
            time = "14:00",
            priority = "medium",
            remind = true,
            done = false,
            subtasks = listOf(
                SubtaskItem(title = "Đọc lại slide bài giảng", done = true),
                SubtaskItem(title = "Làm 10 bài tập cuối chương", done = false)
            )
        ),
        TaskItem(
            id = 3L,
            title = "Đi chợ mua đồ cho tuần",
            note = "",
            category = "life",
            date = "2026-10-20",
            time = "17:30",
            priority = "low",
            remind = false,
            done = false,
            subtasks = emptyList()
        ),
        TaskItem(
            id = 6L,
            title = "Gọi điện về nhà",
            note = "",
            category = "life",
            date = "2026-10-21",
            time = "21:00",
            priority = "low",
            remind = true,
            done = false,
            subtasks = emptyList()
        ),
        TaskItem(
            id = 5L,
            title = "Họp nhóm đồ án",
            note = "Chuẩn bị phần demo giao diện.",
            category = "study",
            date = "2026-10-25",
            time = "20:00",
            priority = "high",
            remind = true,
            done = false,
            subtasks = emptyList()
        )
    )
}
