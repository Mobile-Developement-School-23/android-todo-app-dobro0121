package com.example.todoapp

import androidx.recyclerview.widget.DiffUtil
import com.example.todoapp.data.models.ToDoItem

class TaskDiffCallback: DiffUtil.ItemCallback<ToDoItem>()  {

    override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean = oldItem == newItem
}