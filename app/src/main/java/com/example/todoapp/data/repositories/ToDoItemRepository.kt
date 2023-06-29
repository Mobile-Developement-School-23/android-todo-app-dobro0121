package com.example.todoapp.data.repositories

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.databases.TaskDao
import com.example.todoapp.data.databases.TaskRoomDatabase
import com.example.todoapp.data.models.ToDoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ToDoItemRepository(private val taskDao: TaskDao) {

    //private val database by lazy { TaskRoomDatabase.getDatabase(requireContext()) }
    private var showDone: Boolean = true
    val allTasks: LiveData<List<ToDoItem>> = taskDao.getAllTasks()

    fun getAll():LiveData<List<ToDoItem>> = taskDao.getAllTasks()

    suspend fun addTask_(task: ToDoItem){
        taskDao.insertTask(task)
    }

    suspend fun updateTask_(task: ToDoItem){
        taskDao.updateTask(task)
    }

    suspend fun deleteTask_(task: ToDoItem){
        taskDao.deleteTask(task)
    }

    suspend fun <T> Flow<List<T>>.flattenToList() =
        flatMapConcat { it.asFlow() }.toList()

    /*private val todoItems: MutableList<ToDoItem> = mutableListOf(
        //ToDoItem(UUID.randomUUID().toString(), "Купить молоко", false, "", "Нет"),
        //ToDoItem(UUID.randomUUID().toString(), "Погулять с собакой", false, "", "Нет"),
        //ToDoItem(UUID.randomUUID().toString(), "Сходить в кино", false, "", "Нет")
    )*/

    /*fun addTask(taskText: String, taskId: String) {
        val todoItem = ToDoItem(taskId, taskText, false, "", "Нет")
        allTasks.add(todoItem)
    }*/

    fun getShowDone(): Boolean {
        return showDone
    }

    /*suspend fun getTasks(): List<ToDoItem> {
        return allTasks.flattenToList()
    }*/

    /*suspend fun getTaskById(taskId: Int): ToDoItem? {
        return allTasks { it.id == taskId }
    }*/

    fun updateShowDone(newValue: Boolean) {
        showDone = newValue
    }

    /*suspend fun updateTask(updatedTask: ToDoItem, text: String) {
        val index = allTasks.flattenToList().indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            allTasks.flattenToList()[index].textOfTask = text
        }
    }*/

    /*suspend fun updateDate(updatedDate: ToDoItem, newDate: String) {
        val index = allTasks.indexOfFirst { it.id == updatedDate.id }
        if (index != -1) {
            allTasks[index].deadline = newDate
        }
    }

    suspend fun updateImportance(updatedImportance: ToDoItem, newImportance: String) {
        val index = allTasks.flattenToList().indexOfFirst { it.id == updatedImportance.id }
        if (index != -1) {
            allTasks.flattenToList()[index].importance = newImportance
        }
    }*/

    /*fun deleteTask(taskId: String) {
        allTasks.removeAll { it.id == taskId }
    }*/
}
