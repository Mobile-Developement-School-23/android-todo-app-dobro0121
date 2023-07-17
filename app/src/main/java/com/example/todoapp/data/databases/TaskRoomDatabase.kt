package com.example.todoapp.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.models.ToDoItem

@Database(entities = [ToDoItem::class], version = 1, exportSchema = false)
abstract class TaskRoomDatabase: RoomDatabase() {

    abstract val taskDao: TaskDao

    companion object {
        fun getDatabase(context: Context) = Room.databaseBuilder(
            context,
            TaskRoomDatabase::class.java,
            "tasks_database"
        ).build()
    }
}