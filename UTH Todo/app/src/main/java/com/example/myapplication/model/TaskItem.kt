package com.example.myapplication.model

import androidx.compose.ui.graphics.Color

enum class TaskCategory(val label: String, val emoji: String, val bg: Color, val fg: Color) {
    STUDY("Học tập", "🎓", Color(0xFFDDE6FB), Color(0xFF2F5BEA)),
    LIFE("Cuộc sống", "🏠", Color(0xFFFDE5D4), Color(0xFFC2631F))
}

data class TaskItem(
    val title: String,
    val category: TaskCategory,
    val time: String,
    val done: Boolean = false
)

/** Dữ liệu mẫu cho trang chủ (chưa lưu database). */
object SampleTasks {
    val all = listOf(
        TaskItem("Nộp bài tập Lập trình di động", TaskCategory.STUDY, "09:00", true),
        TaskItem("Ôn chương 3 – Cấu trúc dữ liệu", TaskCategory.STUDY, "14:00"),
        TaskItem("Đi chợ mua đồ cho tuần", TaskCategory.LIFE, "17:30"),
        TaskItem("Tập thể dục 30 phút", TaskCategory.LIFE, "06:30", true),
        TaskItem("Làm slide thuyết trình", TaskCategory.STUDY, "20:00"),
        TaskItem("Giặt đồ", TaskCategory.LIFE, "19:00", true),
        TaskItem("Đọc tài liệu Firebase", TaskCategory.STUDY, "21:00")
    )
}