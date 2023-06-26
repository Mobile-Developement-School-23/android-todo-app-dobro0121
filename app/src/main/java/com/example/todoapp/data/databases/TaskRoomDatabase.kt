package com.example.todoapp.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.models.ToDoItem

@Database(entities = [ToDoItem::class], version = 1)
abstract class TaskRoomDatabase: RoomDatabase() {
    abstract val taskDao: TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskRoomDatabase?= null

        fun getDatabase(context: Context): TaskRoomDatabase
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskRoomDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}