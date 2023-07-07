package com.example.todoapp.data.retrofit

import retrofit2.Response
import retrofit2.http.*

interface ToDoItemApi {

    @GET("auth/list/{id}")
    @Headers("Authorization: Bearer tonguester")
    suspend fun getTaskById(@Path("id") id: String): Response<ToDoApiResponseElement>

    @GET("list")
    @Headers("Authorization: Bearer tonguester")
    suspend fun getAllTasks(): Response<ToDoApiResponseList>

    @PATCH("auth/list")
    @Headers("Authorization: Bearer tonguester")
    suspend fun downloadTasksToServer(@Header("X-Last-Known-Revision:") revision: Int, @Body body: ToDoApiRequestList)
    : Response<ToDoApiResponseList>

    @POST("auth/list")
    @Headers("Authorization: Bearer tonguester")
    suspend fun addTaskToServer(@Header("X-Last-Known-Revision:") revision: Int, @Body newItem: ToDoApiRequestElement)
    : Response<ToDoApiResponseElement>

    @PUT("auth/list/{id}")
    @Headers("Authorization: Bearer tonguester")
    suspend fun changeTaskOnServer(@Header("X-Last-Known-Revision:") revision: Int,
                                   @Path("id") id: String,
                                   @Body body: ToDoApiRequestElement)
    : Response<ToDoApiResponseElement>

    @DELETE("auth/list/{id}")
    @Headers("Authorization: Bearer tonguester")
    suspend fun deleteTaskOnServer(@Header("X-Last-Known-Revision:") revision: Int,
                                   @Path("id") id: String): Response<ToDoApiResponseElement>

}