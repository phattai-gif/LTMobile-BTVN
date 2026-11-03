package com.example.myapplication.data

import com.example.myapplication.model.TaskItem
import com.example.myapplication.util.CrashReporter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

/** Cloud Firestore: CRUD công việc theo từng người dùng. */
class TaskRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private fun col(uid: String) = db.collection("users").document(uid).collection("tasks")

    /** Lắng nghe realtime: có thay đổi trên Firestore là UI tự cập nhật. */
    fun observe(
        uid: String,
        onChange: (List<TaskItem>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration =
        col(uid).orderBy("createdAt", Query.Direction.ASCENDING).addSnapshotListener { snap, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }
            onChange(snap?.documents?.mapNotNull { TaskItem.from(it) } ?: emptyList())
        }

    fun add(uid: String, task: TaskItem) {
        col(uid).add(task.toMap()).addOnFailureListener { CrashReporter.record(it) }
    }

    fun setDone(uid: String, id: String, done: Boolean) {
        col(uid).document(id).update("done", done).addOnFailureListener { CrashReporter.record(it) }
    }

    fun delete(uid: String, id: String) {
        col(uid).document(id).delete().addOnFailureListener { CrashReporter.record(it) }
    }
}