package com.example.todoapp.data.repositories

import android.util.Log
import com.example.todoapp.data.models.ToDoItem
import java.util.*

object ToDoItemRepository {

    private val todoItems: MutableList<ToDoItem> = mutableListOf(
        ToDoItem(UUID.randomUUID().toString(),"Купить молоко", false, "", "Нет"),
        ToDoItem(UUID.randomUUID().toString(),"Погулять с собакой", false, "", "Нет"),
        ToDoItem(UUID.randomUUID().toString(),"Сходить в кино", false, "", "Нет")
    )

    fun addTask(taskText: String, taskId: String) {
        val todoItem = ToDoItem(taskId, taskText, false, "", "Нет")
        todoItems.add(todoItem)
    }

    fun getTasks(): List<ToDoItem> {
        return todoItems
    }

    fun getTaskById(taskId: String): ToDoItem? {
        return todoItems.find { it.id == taskId }
    }

    fun updateTask(updatedTask: ToDoItem, text: String) {
        val index = todoItems.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            todoItems[index].textOfTask = text
        }
    }

    fun updateDate(updatedDate: ToDoItem, newDate: String) {
        val index = todoItems.indexOfFirst { it.id == updatedDate.id }
        if (index != -1) {
            todoItems[index].deadline = newDate
        }
    }

    fun updateImportance(updatedImportance: ToDoItem, newImportance: String) {
        val index = todoItems.indexOfFirst { it.id == updatedImportance.id }
        if (index != -1) {
            todoItems[index].importance = newImportance
        }
    }

    fun deleteTask(taskId: String) {
        todoItems.removeAll { it.id == taskId }
    }

    fun howManyDone(): Int {
        var countDone: Int = 0
        for(i in 0 until todoItems.size)
        {
            if(todoItems[i].done)
            {
                countDone++
            }
        }
        Log.i("Logcat", countDone.toString())
        return countDone
    }

    fun getFlag(todoItem: ToDoItem): Boolean {
        return todoItem.done
    }
}
