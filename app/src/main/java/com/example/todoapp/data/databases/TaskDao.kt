package com.example.todoapp.data.databases

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.retrofit.ToDoItemModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks_table ORDER BY id ASC")
    fun getAllTasks(): Flow<List<ToDoItem>>

    @Query("SELECT * FROM task_table ORDER BY deadline")
    fun getToDoItemsNoFlow(): List<ToDoItem>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getToDoItemById(id: String): ToDoItem

    @Query("SELECT COUNT(*) FROM tasks_table WHERE Is_completed = 1")
    fun getCountCompleted(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ToDoItem)

    @Update
    suspend fun updateTask(task: ToDoItem)

    @Delete
    suspend fun deleteTask(task: ToDoItem)

    @Query("DELETE FROM tasks_table")
    suspend fun deleteAllTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun mergeToDoItems(todoItems: List<ToDoItemModel>)

}