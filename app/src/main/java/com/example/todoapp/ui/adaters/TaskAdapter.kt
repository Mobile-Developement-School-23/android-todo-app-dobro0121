package com.example.todoapp.ui.adaters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.TaskDiffCallback
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.repositories.ToDoItemRepository
import com.example.todoapp.ui.fragments.MainFragment
import com.example.todoapp.ui.fragments.MainFragmentDirections

class TaskAdapter: RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private var tasksArray = emptyList<ToDoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = tasksArray.size

    // Два одинаковых метода с DiffUtil и без
    fun setTasks(newTasks: List<ToDoItem>) {
        val diffResult = DiffUtil.calculateDiff(TaskDiffCallback(tasksArray, newTasks))
        tasksArray = newTasks
        diffResult.dispatchUpdatesTo(this)
    }
    // Два одинаковых метода с DiffUtil и без
    fun setData(tasks: List<ToDoItem>) {
        this.tasksArray = tasks
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val toDoItem = tasksArray[position]

        holder.itemInfo.setOnClickListener() { v ->
            val action = MainFragmentDirections.actionMainFragmentToAddTaskFragment(toDoItem)
            holder.itemView.findNavController().navigate(action)
        }

        holder.onBind(toDoItem)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        lateinit var itemInfo: ImageButton
        lateinit var itemDate: TextView
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            itemInfo = itemView.findViewById(R.id.imageButtonInfo)
            itemDate = itemView.findViewById(R.id.dateOfTaskTextView)
        }

        fun onBind(todoItem: ToDoItem) {
            checkBox.text = todoItem.textOfTask
            checkBox.isChecked = todoItem.done
            itemDate.text = todoItem.deadline

            if (todoItem.done) {
                checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                checkBox.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.greenFormsColor
                    )
                )
            } else {
                checkBox.paintFlags = checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                checkBox.setTextColor(ContextCompat.getColor(itemView.context, R.color.textColor))
                if (todoItem.importance == "!! Высокий") {
                    checkBox.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.redFormsColor
                        )
                    )
                }

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        checkBox.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.greenFormsColor
                            )
                        )
                        todoItem.done = true
                        checkBox.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.greenFormsColor
                            )
                        )
                    } else {
                        checkBox.paintFlags =
                            checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        checkBox.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.textColor
                            )
                        )
                        todoItem.done = false
                        if (todoItem.importance == "!! Высокий") {
                            checkBox.buttonTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.redFormsColor
                                )
                            )
                        }
                    }
                }
            }

        }
    }
}


