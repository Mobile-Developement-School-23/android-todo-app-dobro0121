package com.example.todoapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.repositories.ToDoItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ToDoViewModel : ViewModel(){
    private val taskRepository: ToDoItemRepository = ToDoItemRepository
    private val _taskList: MutableLiveData<List<ToDoItem>> = MutableLiveData()
    val taskList: LiveData<List<ToDoItem>> = _taskList

    init {
        // Получение начальных данных из TodoItemRepository
        val initialTasks = taskRepository.getTasks()
        _taskList.value = initialTasks

    }

    // Генерация уникального идентификатора
    private fun generateTaskId(): String {
        return UUID.randomUUID().toString()
    }

    fun addTask(taskText: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val taskId = generateTaskId()
                Log.i("Logcat", "new taskId: " + taskId)
                taskRepository.addTask(taskText, taskId)
                val updatedTasks = taskRepository.getTasks()
                _taskList.postValue(updatedTasks)
            }
        }
    }

    fun updateTask(taskId: String, newTaskText: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskRepository.getTaskById(taskId)?.let { taskRepository.updateTask(it, newTaskText) }
                val updatedTasks = taskRepository.getTasks()
                _taskList.postValue(updatedTasks)
            }
        }
    }

    fun howManyDone(): Int {
        return taskRepository.howManyDone()
    }

    fun updateDate(taskId: String, newDate: String)
    {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskRepository.getTaskById(taskId)?.let { taskRepository.updateDate(it, newDate) }
                val updatedTasks = taskRepository.getTasks()
                _taskList.postValue(updatedTasks)
            }
        }
    }

    fun updateImportance(taskId: String, importance: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskRepository.getTaskById(taskId)?.let { taskRepository.updateImportance(it, importance) }
                val updatedTasks = taskRepository.getTasks()
                _taskList.postValue(updatedTasks)
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskRepository.deleteTask(taskId)
                val updatedTasks = taskRepository.getTasks()
                _taskList.postValue(updatedTasks)
            }
        }
    }

    fun getTaskById(taskId: String): ToDoItem? {
        return taskRepository.getTaskById(taskId)
    }

    fun getTasks(): List<ToDoItem> {
        return taskRepository.getTasks()
    }

}