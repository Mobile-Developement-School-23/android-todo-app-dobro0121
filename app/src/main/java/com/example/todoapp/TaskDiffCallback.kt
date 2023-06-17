package com.example.todoapp

import androidx.recyclerview.widget.DiffUtil
import com.example.todoapp.data.models.ToDoItem

class TaskDiffCallback(private val oldTasks: List<ToDoItem>, private val newTasks: List<ToDoItem>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldTasks.size
    }

    override fun getNewListSize(): Int {
        return newTasks.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTasks[oldItemPosition].id == newTasks[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTasks[oldItemPosition] == newTasks[newItemPosition]
    }
}