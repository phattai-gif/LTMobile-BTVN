package com.example.myapplication.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.DocumentSnapshot

enum class TaskCategory(val label: String, val emoji: String, val bg: Color, val fg: Color) {
    STUDY("Học tập", "🎓", Color(0xFFDDE6FB), Color(0xFF2F5BEA)),
    LIFE("Cuộc sống", "🏠", Color(0xFFFDE5D4), Color(0xFFC2631F));
    companion object {
        fun fromName(name: String?) = entries.firstOrNull { it.name == name } ?: LIFE
    }
}

/** Một công việc. Được lưu ở Firestore: users/{uid}/tasks/{id}. */
data class TaskItem(
    val id: String = "",
    val title: String,
    val category: TaskCategory,
    val time: String,
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "category" to category.name,
        "time" to time,
        "done" to done,
        "createdAt" to createdAt
    )

    companion object {
        fun from(doc: DocumentSnapshot): TaskItem? {
            val title = doc.getString("title") ?: return null
            return TaskItem(
                id = doc.id,
                title = title,
                category = TaskCategory.fromName(doc.getString("category")),
                time = doc.getString("time") ?: "",
                done = doc.getBoolean("done") ?: false,
                createdAt = doc.getLong("createdAt") ?: 0L
            )
        }
    }
}