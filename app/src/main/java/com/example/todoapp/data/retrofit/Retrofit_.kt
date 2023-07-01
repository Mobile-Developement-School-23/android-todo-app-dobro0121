package com.example.todoapp.data.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.connection.ConnectInterceptor.intercept
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class Retrofit_ {

    val interceptor = HttpLoggingInterceptor()

    val client = OkHttpClient.Builder().addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)).build()

    val retrofit = Retrofit.Builder().baseUrl("https://beta.mrdekk.ru/todobackend")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val toDoItemApi = retrofit.create(ToDoItemApi::class.java)

    var user: User? = null
    //user = toDoItemApi.au

    //val taskItem: ToDoItemModel = toDoItemApi.getTaskById("2")

    //val listObject = toDoItemApi.getAllTasks()

}