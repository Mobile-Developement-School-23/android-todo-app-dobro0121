package com.example.todoapp.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.data.retrofit.Importance
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "tasks_table")
data class ToDoItem(

    @PrimaryKey(autoGenerate = true)
    val id: String,

    @ColumnInfo(name = "Date_of_create")
    var dateOfCreate: Date,

    @ColumnInfo(name = "Date_of_change")
    var dateOfChange: Date?,

    @ColumnInfo(name = "Task")
    var textOfTask: String,

    @ColumnInfo(name = "Is_completed")
    var done: Boolean,

    @ColumnInfo(name = "Deadline")
    var deadline: Date?,

    @ColumnInfo(name = "Importance")
    var importance: Importance

): Parcelable
{
    constructor() : this("-1", Date(), Date(),"", false, null, Importance.basic)
}

