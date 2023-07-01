package com.example.todoapp.ui.viewmodels


import android.text.format.DateFormat
import androidx.lifecycle.*
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.repositories.ToDoItemRepository
import com.example.todoapp.locateLazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ToDoViewModel(private val taskRepository: ToDoItemRepository) : ViewModel(){

    //private val taskRepository: ToDoItemRepository by locateLazy()
    //private val _taskList: MutableLiveData<List<ToDoItem>> = MutableLiveData()
    //val taskList = taskRepository.getAll().asLiveDataFlow()

    fun getAllTasks(): LiveData<List<ToDoItem>> = taskRepository.getAll()

    fun add(task: String)
    {
        viewModelScope.launch {
            taskRepository.addTask_(createTask(task))
        }
    }

    fun delete(task: ToDoItem)
    {
        viewModelScope.launch {
            taskRepository.deleteTask_(task)
        }
    }

    // нужно видоизменить переданные параметры в иде важности и тд
    private fun createTask(textTask: String) = ToDoItem(
        dateOfCreate = createCaption(),
        dateOfChange = "",
        textOfTask = textTask,
        done = false,
        deadline = "",
        importance = ""
    )

    private fun createCaption(): String = DateFormat.format("dd, MMM, yyyy", Date()).toString()

    fun <T> Flow<T>.asLiveDataFlow() = shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    // Генерация уникального идентификатора
    private fun generateTaskId(): String {
        return UUID.randomUUID().toString()
    }

    fun addTask(newTask: ToDoItem) {
        viewModelScope.launch {
            taskRepository.addTask_(newTask)
        }
    }

    fun getShowDone(): Boolean {
        return taskRepository.getShowDone()
    }

    fun updateShowDone(newVaue: Boolean) {
        taskRepository.updateShowDone(newVaue)
    }

    /*fun getUncompletedTasks(): List<ToDoItem> {
        val allTasks = taskList.value
        return allTasks?.filter { !it.done } ?: emptyList()
    }*/

    fun updateTask(task: ToDoItem) {
        viewModelScope.launch {
            taskRepository.updateTask_(task)
        }
    }




    /*fun updateDate(taskId: String, newDate: String)
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
    }*/

}