package com.example.todoapp.data.databases

import androidx.room.*
import com.example.todoapp.data.models.ToDoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks_table ORDER BY id ASC")
    fun getAllTasks(): Flow<List<ToDoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ToDoItem)

    suspend fun updateTask(task: ToDoItem)

    @Delete
    suspend fun deleteTask(task: ToDoItem)
}