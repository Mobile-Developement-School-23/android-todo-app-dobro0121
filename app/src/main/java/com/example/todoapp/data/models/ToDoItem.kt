package com.example.todoapp.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "tasks_table")
data class ToDoItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "Date_of_create")
    var dateOfCreate: String,

    @ColumnInfo(name = "Date_of_change")
    var dateOfChange: String,

    @ColumnInfo(name = "Task")
    var textOfTask: String,

    @ColumnInfo(name = "Is_completed")
    var done: Boolean,

    @ColumnInfo(name = "Deadline")
    var deadline: String,

    @ColumnInfo(name = "Importance")
    var importance: String

): Parcelable

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