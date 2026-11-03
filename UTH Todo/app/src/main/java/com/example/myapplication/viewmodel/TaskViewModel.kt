package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.RemoteConfigRepository
import com.example.myapplication.data.SyncStatusRepository
import com.example.myapplication.data.TaskRepository
import com.example.myapplication.model.TaskCategory
import com.example.myapplication.model.TaskItem
import com.example.myapplication.util.Analytics
import com.example.myapplication.util.CrashReporter
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ListenerRegistration

/** Gom Firestore (công việc), Realtime Database (đồng bộ), Remote Config (nội dung) cho màn hình chính. */
class TaskViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val syncRepository = SyncStatusRepository()
    private val configRepository = RemoteConfigRepository()

    var tasks by mutableStateOf<List<TaskItem>>(emptyList())
        private set
    var loading by mutableStateOf(true)
        private set
    var lastSync by mutableStateOf<Long?>(null)
        private set
    var welcomeMessage by mutableStateOf(configRepository.welcomeMessage())
        private set
    var showProgressCard by mutableStateOf(configRepository.showProgressCard())
        private set

    private var currentUid: String? = null
    private var taskListener: ListenerRegistration? = null
    private var syncListener: ValueEventListener? = null

    init {
        configRepository.fetch {
            welcomeMessage = configRepository.welcomeMessage()
            showProgressCard = configRepository.showProgressCard()
        }
    }

    /** Bắt đầu lắng nghe dữ liệu của người dùng [uid]. */
    fun start(uid: String) {
        if (currentUid == uid) return
        stop()
        currentUid = uid
        loading = true
        taskListener = taskRepository.observe(
            uid,
            onChange = { tasks = it; loading = false },
            onError = { loading = false; CrashReporter.record(it) }
        )
        syncListener = syncRepository.observe(uid) { lastSync = it }
    }

    fun stop() {
        taskListener?.remove()
        val uid = currentUid
        val listener = syncListener
        if (uid != null && listener != null) syncRepository.stop(uid, listener)
        taskListener = null
        syncListener = null
        currentUid = null
        tasks = emptyList()
        lastSync = null
    }

    fun addTask(title: String, category: TaskCategory, time: String) {
        val uid = currentUid ?: return
        taskRepository.add(uid, TaskItem(title = title, category = category, time = time))
        Analytics.log("task_created", "category" to category.name)
        syncRepository.markSynced(uid)
    }

    fun toggle(task: TaskItem) {
        val uid = currentUid ?: return
        taskRepository.setDone(uid, task.id, !task.done)
        if (!task.done) Analytics.log("task_completed", "category" to task.category.name)
        syncRepository.markSynced(uid)
    }

    fun delete(task: TaskItem) {
        val uid = currentUid ?: return
        taskRepository.delete(uid, task.id)
        Analytics.log("task_deleted")
        syncRepository.markSynced(uid)
    }

    override fun onCleared() {
        stop()
    }
}