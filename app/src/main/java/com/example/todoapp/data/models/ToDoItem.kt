package com.example.todoapp.data.models

import java.util.Calendar

data class ToDoItem(val id: String, var textOfTask: String, var done: Boolean, var deadline: String, var importance: String) {

}