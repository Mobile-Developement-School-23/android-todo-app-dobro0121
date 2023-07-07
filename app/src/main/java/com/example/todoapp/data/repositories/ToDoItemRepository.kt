package com.example.todoapp.data.repositories

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.databases.SharedPreferencesAppSettings
import com.example.todoapp.data.databases.TaskDao
import com.example.todoapp.data.databases.TaskRoomDatabase
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.retrofit.*
import com.example.todoapp.data.retrofit.ToDoItemModel.Companion.fromToDoTask
import com.example.todoapp.data.utils.Constants.BASE_URL
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


class ToDoItemRepository(private val db: TaskRoomDatabase, private val sharedPreferences: SharedPreferencesAppSettings) {

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

    fun getToDoItemById(id: String): ToDoItem {
        return taskDao.getToDoItemById(id = id)
    }

    fun getAllData(): Flow<List<ToDoItem>> = taskDao.getAllTasks()

    suspend fun getAllTasksFromServer(): LoadingState<Any> {
        try {
            val networkListResponse = toDoItemApi.getAllTasks()

            if (networkListResponse.isSuccessful) {
                val body = networkListResponse.body()
                if (body != null) {
                    val revision = body.revision
                    val networkList = body.list
                    val currentList = taskDao.getToDoItemsNoFlow().map {
                        ToDoItemModel.fromToDoTask(
                            it,
                            sharedPreferences.getDeviceId()
                        )
                    }
                    val mergedList = HashMap<String, ToDoItemModel>()

                    for (item in currentList) {
                        mergedList[item.id] = item
                    }
                    for (item in networkList) {
                        if (mergedList.containsKey(item.id)) {
                            val item1 = mergedList[item.id]
                            if (item.changed_at > item1!!.changed_at) {
                                mergedList[item.id] = item
                            } else {
                                mergedList[item.id] = item1
                            }
                        } else if (revision != sharedPreferences.getRevisionId()) {
                            mergedList[item.id] = item
                        }
                    }

                    return updateTasksOnServer(mergedList.values.toList())
                }
            } else {
                networkListResponse.errorBody()?.close()
            }

        } catch (e: Exception) {
            return LoadingState.Error("Merge failed, continue offline.")
        }
        return LoadingState.Error("Merge failed, continue offline.")
    }

    private suspend fun updateTasksOnServer(mergedList: List<ToDoItemModel>): LoadingState<Any> {
        try {
            val response = toDoItemApi.downloadTasksToServer(
                revision = sharedPreferences.getRevisionId(),
                body = ToDoApiRequestList(status = "ok", mergedList)
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    sharedPreferences.putRevisionId(responseBody.revision)
                    taskDao.mergeToDoItems(responseBody.list.map { fromToDoTask(it.toToDoItem(), sharedPreferences.getDeviceId()) })
                    return LoadingState.Success(responseBody.list)
                }
            } else {
                response.errorBody()?.close()
            }
        } catch (e: Exception) {
            return LoadingState.Error("Merge failed, continue offline.")
        }

        return LoadingState.Error("Merge failed, continue offline.")
    }

    suspend fun getTaskByIdFromServer(id: String) {
        toDoItemApi.getTaskById(id)
    }

    fun getCountCompleted(): LiveData<Int> { return taskDao.getCountCompleted() }

    suspend fun addTask(task: ToDoItem) = taskDao.insertTask(task)

    suspend fun addTaskToServer(newTask: ToDoItem) {
        try {
            val response = toDoItemApi.addTaskToServer(
                revision = sharedPreferences.getRevisionId(),
                newItem = ToDoApiRequestElement(
                    ToDoItemModel.fromToDoTask(
                        newTask,
                        sharedPreferences.getDeviceId()
                    )
                )
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    sharedPreferences.putRevisionId(responseBody.revision)
                }
            } else {
                response.errorBody()?.close()
            }
        } catch (e: Exception) {
            Log.e("Добавление данных на сервере не выполнено!", e.toString())
        }
    }

    suspend fun deleteTask(task: ToDoItem) = taskDao.deleteTask(task)

    suspend fun deleteTaskFromServer(id: String) {
        try {
            val response = toDoItemApi.deleteTaskOnServer(
                id = id,
                revision = sharedPreferences.getRevisionId()
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    sharedPreferences.putRevisionId(responseBody.revision)
                }
            } else {
                response.errorBody()?.close()
            }
        } catch (e: Exception) {
            Log.e("Удаление данных на сервере не выполнено!", e.toString())
        }
    }

    suspend fun updateTask(task: ToDoItem) = taskDao.updateTask(task)

    suspend fun updateTaskOnServer(toDoItem: ToDoItem) {
        try {
            val response = toDoItemApi.changeTaskOnServer(
                revision = sharedPreferences.getRevisionId(),
                id = toDoItem.id,
                body = ToDoApiRequestElement(
                    ToDoItemModel.fromToDoTask(
                        toDoItem,
                        sharedPreferences.getDeviceId()
                    )
                )
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    sharedPreferences.putRevisionId(responseBody.revision)
                }
            } else {
                response.errorBody()?.close()
            }
        } catch (e: Exception) {
            Log.e("Изменение данных на сервере не выполнено!", e.message.toString())
        }
    }

    suspend fun downloadTasksToSever(mergedList: List<ToDoItemModel>): LoadingState<Any> {
        try {
            val response = toDoItemApi.downloadTasksToServer(
                revision = sharedPreferences.getRevisionId(),
                body = ToDoApiRequestList(status = "ok", mergedList)
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    sharedPreferences.putRevisionId(responseBody.revision)
                    taskDao.mergeToDoItems(responseBody.list.map { fromToDoTask(it.toToDoItem(), sharedPreferences.getDeviceId()) })
                    return LoadingState.Success(responseBody.list)
                }
            } else {
                response.errorBody()?.close()
            }
        } catch (e: Exception) {
            return LoadingState.Error("Слияние не вышло, далее оффлайн")
        }

        return LoadingState.Error("Слияние не вышло, далее оффлайн")
    }

    fun getShowDone(): Boolean {
        return showDone
    }

    fun updateShowDone(newValue: Boolean) {
        showDone = newValue
    }

}
