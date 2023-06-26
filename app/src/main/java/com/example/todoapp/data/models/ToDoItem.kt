package com.example.todoapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks_table")
data class ToDoItem (

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "Task")
    var textOfTask: String,

    @ColumnInfo(name = "Is_completed")
    val done: Boolean,

    @ColumnInfo(name = "Deadline")
    var deadline: String,

    @ColumnInfo(name = "Importance")
    var importance: String
)
