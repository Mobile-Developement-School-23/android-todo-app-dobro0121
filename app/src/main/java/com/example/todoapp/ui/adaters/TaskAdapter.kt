package com.example.todoapp.ui.adaters

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.TaskDiffCallback
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.retrofit.Importance
import com.example.todoapp.databinding.TaskItemBinding
import com.example.todoapp.ui.fragments.MainFragmentDirections


class TaskAdapter(private val listener: TaskAdapterListener) : ListAdapter<ToDoItem, ToDoViewHolder>(
    TaskDiffCallback()
) {

    interface TaskAdapterListener {
        fun onTaskCheckboxClicked(task: ToDoItem, isChecked: Boolean)
        fun onClickItem(idItem: String)
        fun onClickCheck(item: ToDoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val viewHolder = ToDoViewHolder.create(parent)
        viewHolder.setListener(listener)
        return viewHolder
    }

    /*
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
    }*/

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val toDoItem = getItem(position)
        holder.onBind(toDoItem, listener)

        /*holder.itemInfo.setOnClickListener() { v ->
            val action = MainFragmentDirections.actionMainFragmentToAddTaskFragment(toDoItem)
            holder.itemView.findNavController().navigate(action)
        }*/
    }
}

class ToDoViewHolder(private val binding: TaskItemBinding): RecyclerView.ViewHolder(binding.root){
    lateinit var itemInfo: ImageButton
    lateinit var itemDate: TextView
    //private val checkBox: CheckBox

    init {
        itemInfo = itemView.findViewById(R.id.imageButtonInfo)
        itemDate = itemView.findViewById(R.id.dateOfTaskTextView)
        //checkBox = itemView.findViewById(R.id.checkBox)
    }

    private var listener: TaskAdapter.TaskAdapterListener? = null

    var task: ToDoItem? = null
        private set

    fun setListener(listener: TaskAdapter.TaskAdapterListener) {
        this.listener = listener
    }

    fun onBind(task: ToDoItem, listener: TaskAdapter.TaskAdapterListener) {
        this.task = task
        //checkBox.text = todoItem.textOfTask
        //checkBox.isChecked = todoItem.done
        //itemDate.text = todoItem.deadline

        itemView.setOnClickListener {
            listener.onClickItem(task.id)
        }

        binding.checkBox.setOnClickListener {
            listener.onClickCheck(task)
        }

        views {
            checkBox.text = task.textOfTask
            checkBox.isChecked = task.done
            itemDate.text = task.deadline.toString()

            if (task.done) {
                checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                checkBox.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.lightGrey
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
            }
            if (task.importance == Importance.important) {
                if (task.done) {
                    checkBox.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.greenFormsColor
                        )
                    )
                } else {
                    checkBox.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.redFormsColor
                        )
                    )
                }
            } else {
                if (task.done) {
                    checkBox.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.greenFormsColor
                        )
                    )
                } else {
                    checkBox.buttonTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.lightGrey
                        )
                    )
                }
            }


            checkBox.setOnCheckedChangeListener(null) // Удаляем предыдущего слушателя
            checkBox.setOnClickListener{
                task.done = !task.done

                if (task.done) {
                    checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    checkBox.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.lightGrey
                        )
                    )
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
                    if (task.importance == Importance.important) {
                        checkBox.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.redFormsColor
                            )
                        )
                    } else {
                        checkBox.buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.lightGrey
                            )
                        )
                    }
                }

                listener?.onTaskCheckboxClicked(task, task.done)
            }

        }
    }

    private fun <T> views(block: TaskItemBinding.() -> T): T? = binding.block()
    companion object {
        fun create(parent: ViewGroup) =
            TaskItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).let(::ToDoViewHolder)
    }
}
