package com.example.todoapp.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.databases.TaskRoomDatabase
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.repositories.ToDoItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksViewModel(application: Application): AndroidViewModel(application) {

    val allTasks: LiveData<List<ToDoItem>>
    private val taskRepository: ToDoItemRepository

    init {
        val taskDao = TaskRoomDatabase.getDatabase(application).taskDao()
        taskRepository = ToDoItemRepository(taskDao)
        allTasks = taskRepository.allTasks
    }

    // Добавление задачи
    fun addTask(newTask: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.addTask_(newTask)
        }
    }
    // Обновление данных задачи
    fun updateTask(updateTask: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTask_(updateTask)
        }
    }
    // Удаление задачи
    fun deleteTask(deleteTask: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.deleteTask_(deleteTask)
        }
    }

    fun getShowDone(): Boolean {
        return taskRepository.getShowDone()
    }

    fun updateShowDone(newVaue: Boolean) {
        taskRepository.updateShowDone(newVaue)
    }

    fun getUncompletedTasks(): List<ToDoItem> {
        val allTasks = allTasks.value
        return allTasks?.filter { !it.done } ?: emptyList()
    }

    /*fun howManyDone(): Int {
        var countDone: Int = 0
            for(i in 0 until allTasks.size)
            {
                if(allTasks[i].done)
                {
                    countDone++
                }
            }
            Log.i("Logcat", countDone.toString())
        return countDone
    }*/
}