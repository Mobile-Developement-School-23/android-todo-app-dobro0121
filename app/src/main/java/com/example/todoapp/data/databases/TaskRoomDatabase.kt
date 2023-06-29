package com.example.todoapp.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.models.ToDoItem

@Database(entities = [ToDoItem::class], version = 1, exportSchema = false)
abstract class TaskRoomDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskRoomDatabase? = null
        fun getDatabase(context: Context): TaskRoomDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) { // если экземпляр БД существует, то этот и возвращаем
                return tempInstance
            }
            // иначе в синхронизированном потоке возвращаем новый созданный экзмпляр
            synchronized(this) {
                val instance = Room.databaseBuilder(context,
                    TaskRoomDatabase::class.java,
                    "task_database").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}