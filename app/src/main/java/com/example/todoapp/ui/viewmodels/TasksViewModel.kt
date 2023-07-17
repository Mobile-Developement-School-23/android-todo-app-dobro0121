package com.example.todoapp.ui.viewmodels

import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.example.todoapp.R
import com.example.todoapp.data.SharedPreferences
import com.example.todoapp.data.databases.TaskDao
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.repositories.ToDoItemRepository
import com.example.todoapp.data.retrofit.Tasks
import com.example.todoapp.data.retrofit.ToDoItemApi
import com.example.todoapp.data.retrofit.ToDoItemModel
import com.example.todoapp.data.utils.CoroutinesDispatcherProvider
import com.example.todoapp.data.utils.ResultHandler
import com.example.todoapp.data.utils.ViewState
import com.example.todoapp.data.utils.runIO
import com.example.todoapp.locateLazy
import com.example.todoapp.ui.fragments.MainFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class TasksViewModel(): ViewModel() {

    private val taskRepository: ToDoItemRepository by locateLazy()
    //private val sharedPreferences: SharedPreferences

    var job : Job? = null

    private val _tasks = MutableSharedFlow<List<ToDoItem>>()
    val allTasks: SharedFlow<List<ToDoItem>> = _tasks.asSharedFlow()

    val countCompletedTask: Flow<Int> = _tasks.map { it -> it.count { it.done } }

    var showAll = true

    init {
        loadTasks()
    }

    fun changeMode() {
        job?.cancel()
        showAll = !showAll
        loadTasks()
    }

    private fun loadTasks() {
        job = viewModelScope.launch(Dispatchers.IO) {
            _tasks.emitAll(taskRepository.getAllData())
        }
    }

    fun getAllTasksFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.getAllTasksFromServer()
        }
    }

    fun changeVisibilityButton(flag: Boolean): Boolean {
        return !flag
    }

    fun addTask(task: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.addTask(task) }
    }

    fun addTaskToServer(revision: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.addTaskToServer(revision)
        }
    }

    fun deleteTask(task: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.deleteTask(task) }
    }

    fun deleteTaskFromServer(id: String) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.deleteTaskFromServer(id) }
    }

    fun updateTask(task: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.updateTask(task) }
    }

    fun updateTaskOnServer(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTaskOnServer(id)
        }
    }

    fun downloadTasksToServer(revision: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.downloadTasksToSever(revision)
        }
    }

    fun getCountCompleted(): LiveData<Int> { return taskRepository.getCountCompleted() }

    private fun <T> Flow<T>.asLiveDataFlow() = shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    fun getShowDone(): Boolean {
        return taskRepository.getShowDone()
    }

    fun updateShowDone(newVaue: Boolean) {
        taskRepository.updateShowDone(newVaue)
    }

    //fun getStatusNotifications() = sharedPreferences.getNotificationStatus()

    //fun putStatusNotification(status : Boolean) = sharedPreferences.putNotificationStatus(status)
}