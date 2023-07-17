package com.example.todoapp.data.retrofit

import java.util.UUID

enum class Importance {
    low, basic, important
}

data class ToDoItemModel (
    val id: UUID,
    val text: String,
    val importance: Importance,
    val deadline: Int,
    val done: Boolean,
    val color: String,
    val created_at: Int,
    val change_at: Int,
    val last_updated_by: UUID
)