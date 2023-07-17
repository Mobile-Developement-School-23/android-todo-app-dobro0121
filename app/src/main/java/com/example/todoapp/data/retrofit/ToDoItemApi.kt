package com.example.todoapp.data.retrofit

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ToDoItemApi {

    @GET("auth/list/{id}")
    @Headers("Authorization: Bearer tonguester")
    suspend fun getTaskById(@Path("id") id: String): ToDoItemModel

    @GET("list")
    @Headers("Authorization: Bearer tonguester")
    suspend fun getAllTasks(): Tasks

    @PATCH("auth/list")
    @Headers("Authorization: Bearer tonguester")
    suspend fun downloadTasksToServer(@Header("X-Last-Known-Revision:") revision: String)

    @POST("auth/list")
    @Headers("Authorization: Bearer tonguester")
    suspend fun addTaskToServer(@Header("X-Last-Known-Revision:") revision: String)

    @PUT("auth/list/{id}")
    @Headers("Authorization: Bearer tonguester")
    suspend fun changeTaskOnServer(@Path("id") id: String)

    @DELETE("auth/list/{id}")
    @Headers("Authorization: Bearer tonguester")
    suspend fun deleteTaskOnServer(@Path("id") id: String)

}