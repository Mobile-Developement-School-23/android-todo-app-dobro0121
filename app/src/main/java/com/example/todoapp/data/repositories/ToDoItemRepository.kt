package com.example.todoapp.data.repositories

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.databases.TaskDao
import com.example.todoapp.data.databases.TaskRoomDatabase
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.retrofit.Retrofit_
import com.example.todoapp.data.retrofit.Tasks
import com.example.todoapp.data.retrofit.ToDoItemApi
import com.example.todoapp.data.retrofit.ToDoItemModel
import com.example.todoapp.data.utils.Constants.BASE_URL
import com.example.todoapp.data.utils.notificationManager.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class ToDoItemRepository(private val db: TaskRoomDatabase) {

    private val taskDao get() = db.taskDao
    private var showDone: Boolean = true


    val interceptor = HttpLoggingInterceptor()

    val client = OkHttpClient.Builder().addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)).build()

    val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create()).build()

    val toDoItemApi = retrofit.create(ToDoItemApi::class.java)

        /*private fun addAuthorizationHeader(chain: Interceptor.Chain): Response {
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer tonguester")
            .build()
        return chain.proceed(newRequest)
    }*/

    fun getAllData(): Flow<List<ToDoItem>> = taskDao.getAllTasks()

    suspend fun getAllTasksFromServer() {
       val tasks = toDoItemApi.getAllTasks().Tasks
        //Log.d("Logcat", tasks.toString())
    }

    suspend fun getTaskByIdFromServer(id: String) {
        toDoItemApi.getTaskById(id)
    }

    fun getCountCompleted(): LiveData<Int> { return taskDao.getCountCompleted() }

    suspend fun addTask(task: ToDoItem) = taskDao.insertTask(task)

    suspend fun addTaskToServer(revision: String) {
        toDoItemApi.addTaskToServer(revision)
    }

    suspend fun deleteTask(task: ToDoItem) = taskDao.deleteTask(task)

    suspend fun deleteTaskFromServer(id: String) {
        toDoItemApi.deleteTaskOnServer(id)
    }

    suspend fun updateTask(task: ToDoItem) = taskDao.updateTask(task)

    suspend fun updateTaskOnServer(id: String) {
        toDoItemApi.changeTaskOnServer(id)
    }

    suspend fun downloadTasksToSever(revision: String) {
        toDoItemApi.downloadTasksToServer(revision)
    }

    fun getShowDone(): Boolean {
        return showDone
    }

    fun updateShowDone(newValue: Boolean) {
        showDone = newValue
    }
}
