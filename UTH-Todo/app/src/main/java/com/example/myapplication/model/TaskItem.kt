package com.example.myapplication.model

import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.annotations.SerializedName

enum class TaskCategory(
    val key: String,
    val label: String,
    val emoji: String,
    val bg: Color,
    val fg: Color
) {
    STUDY("study", "Học tập", "🎓", Color(0xFFEFF4FE), Color(0xFF2B5CE6)),
    LIFE("life", "Cuộc sống", "🏠", Color(0xFFFDF0E7), Color(0xFFC2631F));

    companion object {
        fun fromKey(key: String?): TaskCategory = when (key?.lowercase()) {
            "study" -> STUDY
            "life" -> LIFE
            else -> LIFE
        }

        fun fromName(name: String?): TaskCategory = when (name?.uppercase()) {
            "STUDY" -> STUDY
            "LIFE" -> LIFE
            else -> fromKey(name)
        }
    }
}

data class SubtaskItem(
    @SerializedName("title") val title: String = "",
    @SerializedName("done") val done: Boolean = false
)

/**
 * Task model theo đặc tả API của UTH Todo.
 */
data class TaskItem(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("title") val title: String = "",
    @SerializedName("note") val note: String = "",
    @SerializedName("category") val category: String = "life",
    @SerializedName("date") val date: String = "",
    @SerializedName("time") val time: String = "",
    @SerializedName("priority") val priority: String = "medium",
    @SerializedName("remind") val remind: Boolean = false,
    @SerializedName("done") val done: Boolean = false,
    @SerializedName("subtasks") val subtasks: List<SubtaskItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val categoryEnum: TaskCategory
        get() = TaskCategory.fromKey(category)

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "note" to note,
        "category" to category,
        "date" to date,
        "time" to time,
        "priority" to priority,
        "remind" to remind,
        "done" to done,
        "createdAt" to createdAt
    )

    companion object {
        fun from(doc: DocumentSnapshot): TaskItem? {
            val title = doc.getString("title") ?: return null
            return TaskItem(
                id = doc.getLong("id") ?: (doc.id.hashCode().toLong()),
                title = title,
                note = doc.getString("note") ?: "",
                category = doc.getString("category") ?: "life",
                date = doc.getString("date") ?: "",
                time = doc.getString("time") ?: "",
                priority = doc.getString("priority") ?: "medium",
                remind = doc.getBoolean("remind") ?: false,
                done = doc.getBoolean("done") ?: false,
                createdAt = doc.getLong("createdAt") ?: 0L
            )
        }
    }
}

data class PatchTaskRequest(
    @SerializedName("done") val done: Boolean? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("remind") val remind: Boolean? = null,
    @SerializedName("subtasks") val subtasks: List<SubtaskItem>? = null
)

data class ApiErrorResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null
)
