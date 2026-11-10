package com.example.myapplication.data.api

import com.example.myapplication.model.PatchTaskRequest
import com.example.myapplication.model.TaskItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface TaskApiService {

    @GET("tasks")
    suspend fun getTasks(
        @Header("Authorization") authHeader: String
    ): Response<List<TaskItem>>

    @GET("tasks/{id}")
    suspend fun getTask(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long
    ): Response<TaskItem>

    @PATCH("tasks/{id}")
    suspend fun patchTask(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long,
        @Body body: PatchTaskRequest
    ): Response<TaskItem>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Long
    ): Response<Unit>
}
