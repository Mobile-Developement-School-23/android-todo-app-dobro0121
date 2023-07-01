package com.example.todoapp.data.databases

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todoapp.data.models.ToDoItem

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks_table ORDER BY id ASC")
    fun getAllTasks(): LiveData<List<ToDoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ToDoItem)

    @Update
    suspend fun updateTask(task: ToDoItem)

    @Delete
    suspend fun deleteTask(task: ToDoItem)

}