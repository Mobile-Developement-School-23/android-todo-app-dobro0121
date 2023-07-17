package com.example.todoapp.data.utils.notificationManager

import com.example.todoapp.data.models.ToDoItem

interface NotificationScheduler {
    fun schedule(item: ToDoItem)
    fun cancel(item: ToDoItem)
    fun cancelAll()
}