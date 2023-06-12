package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter: RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private val repository: ToDoItemRepository = ToDoItemRepository()

    private val tasksArray: List<ToDoItem> by lazy {
        repository.getTasks()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = tasksArray.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemTextView: TextView
        val itemInfo: ImageButton
        val itemCheckBox: CheckBox

        init {
            itemTextView = itemView.findViewById(R.id.taskTextView)
            itemInfo = itemView.findViewById(R.id.imageButtonInfo)
            itemCheckBox = itemView.findViewById(R.id.checkBox)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTextView.text = tasksArray.get(position).textOfTask
    }

}