package com.example.todoapp.data.retrofit

import com.example.todoapp.data.models.ToDoItem

data class Tasks(
    val Tasks: List<ToDoItemModel>,
    val revision: String
)
