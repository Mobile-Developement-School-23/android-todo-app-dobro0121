package com.example.todoapp

import android.content.Context

class ToDoItemsRepository {
    fun getTasks(context: Context?): List<ToDoItem> {
        return buildList {
            val taskText = "Как же много дел № "
            val numberOfTasks = 15
            for(i in 0 until numberOfTasks){
                val task: String = taskText + i
                add(ToDoItem(task))
            }
        }
    }
}