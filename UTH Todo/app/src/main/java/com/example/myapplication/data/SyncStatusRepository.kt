package com.example.myapplication.data

import com.example.myapplication.util.CrashReporter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

/**
 * Realtime Database: lưu thời điểm đồng bộ gần nhất tại status/{uid}/lastSync.
 * Bọc try/catch để app không crash nếu chưa tạo Realtime Database trên Console.
 */
class SyncStatusRepository {

    private val db by lazy { FirebaseDatabase.getInstance() }

    private fun ref(uid: String) = db.getReference("status").child(uid).child("lastSync")

    fun markSynced(uid: String) {
        try {
            ref(uid).setValue(ServerValue.TIMESTAMP)
        } catch (e: Exception) {
            CrashReporter.record(e)
        }
    }

    fun observe(uid: String, onChange: (Long?) -> Unit): ValueEventListener? = try {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onChange(snapshot.getValue(Long::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                CrashReporter.record(error.toException())
            }
        }
        ref(uid).addValueEventListener(listener)
        listener
    } catch (e: Exception) {
        CrashReporter.record(e)
        null
    }

    fun stop(uid: String, listener: ValueEventListener) {
        try {
            ref(uid).removeEventListener(listener)
        } catch (e: Exception) {
            CrashReporter.record(e)
        }
    }
}