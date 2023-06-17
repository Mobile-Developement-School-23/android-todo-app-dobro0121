package com.example.todoapp.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.ui.adaters.TaskAdapter
import com.example.todoapp.ui.viewmodels.ToDoViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class AddTaskFragment : Fragment() {

    private lateinit var taskViewModel: ToDoViewModel
    private lateinit var idTask: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel = ViewModelProvider(requireActivity()).get(ToDoViewModel::class.java)
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        val switchCalendar: SwitchCompat = view.findViewById(R.id.switchDate)
        val buttonClose: ImageButton = view.findViewById(R.id.imageButtonClose)
        val textViewDelete: TextView = view.findViewById(R.id.DeleteTextView)
        val textViewSafe: TextView = view.findViewById(R.id.SafeTextView)
        var editViewTask: EditText = view.findViewById(R.id.EditTextTaskOf)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val importantTextView: TextView = view.findViewById(R.id.importance_button)
        val textImportance: TextView = view.findViewById(R.id.text_importance)
        idTask = arguments?.getString("id").toString()

        deleteViewChanges(view)

        val task: ToDoItem? = idTask?.let { taskViewModel.getTaskById(it) }
        Log.i("Logcat", "task " + task)
        if (idTask != null) {
            if(idTask.isNotEmpty()){
                if (task != null) {
                    editViewTask.setText(task.textOfTask)
                    dateTextView.text = task.deadline
                    textImportance.text = task.importance
                    if(dateTextView != null)
                    {
                        switchCalendar.isChecked
                    }
                }
            }
        }

        importantTextView.setOnClickListener() {
            showImportanceList(importantTextView, textImportance)
        }

        textViewSafe.setOnClickListener() {
            val textTask: String = editViewTask.text.toString().trim()
            Log.d("Logcat", "text task is not empty: " + textTask.isNotEmpty().toString())
            Log.d("Logcat", "id: " + idTask.isNotEmpty())
            Log.d("Logcat", "id: " + idTask)

            if (textTask.isNotEmpty()) {
                if (idTask != "addTask_") {
                    Log.d("Logcat", " add or not add")
                    taskViewModel.updateTask(idTask, textTask)
                } else
                {
                    taskViewModel.addTask(textTask)
                }
            }

            if (idTask != null && idTask != "defaultTaskId" &&
                dateTextView.text != taskViewModel.getTaskById(idTask)?.deadline) {
                taskViewModel.updateDate(idTask, dateTextView.text.toString())
            }

            if (idTask != null && idTask != "defaultTaskId" &&
                importantTextView.text != taskViewModel.getTaskById(idTask)?.importance) {
                taskViewModel.updateImportance(idTask, textImportance.text.toString())
            }

            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }

        buttonClose.setOnClickListener() {
            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }

        textViewDelete.setOnClickListener() {
            val taskText = editViewTask.text.toString().trim()
            if((idTask?.isNotEmpty() ?: idTask) != "defaultTaskId" && taskText.isNotEmpty())
                idTask?.let { it1 -> taskViewModel.deleteTask(it1) }
            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }
        
        switchCalendar.setOnCheckedChangeListener { _, isChecked ->
            builder.setTitleText("Выберите дату")
            val picker: MaterialDatePicker<*> = builder.build()
            fragmentManager?.let { manager ->
                picker.show(manager, picker.toString())
            }

            picker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it as Long
                val format = SimpleDateFormat("d MMMM yyyy")
                val formatted: String = format.format(utc.time)
                dateTextView.setText(formatted)
            }
        }
    }

    private fun showImportanceList(view: View, textImportance: TextView) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.important_menu, popupMenu.menu)

        val todoItem: ToDoItem? = taskViewModel.getTaskById(idTask)

        var highImportance = popupMenu.menu.getItem(2)
        var spannable: SpannableString = SpannableString(highImportance.title.toString())
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.redFormsColor)), 0, spannable.length, 0)
        highImportance.title = spannable

        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem: MenuItem? ->
            textImportance.text = menuItem?.title
            if(menuItem!!.itemId == R.id.high) {
                textImportance.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.redFormsColor
                    )
                )
            }
            else if(menuItem!!.itemId == R.id.low) {
                textImportance.setTextColor(ContextCompat.getColor(view.context, R.color.lightGrey))
                todoItem?.let {
                    val importance = getString(R.string.low)
                    taskViewModel.updateImportance(it.id, importance)
                }
            } else {
                textImportance.setTextColor(ContextCompat.getColor(view.context, R.color.lightGrey))
                todoItem?.let {
                    val importance = getString(R.string.no)
                    taskViewModel.updateImportance(it.id, importance)
                }
            }
            true
        })
        popupMenu.show()
    }

    private fun deleteViewChanges(view: View) {
        var editViewTask: EditText = view.findViewById(R.id.EditTextTaskOf)
        val textViewDelete: TextView = view.findViewById(R.id.DeleteTextView)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.delete)?.mutate()

        // Проверяем, содержит ли EditText текст изначально
        if (editViewTask.text.isNotEmpty()) {
            val color = ContextCompat.getColor(requireContext(), R.color.redFormsColor)
            drawable?.setTint(color)
            textViewDelete.setTextColor(color)
        } else {
            val color = ContextCompat.getColor(requireContext(), R.color.lightGrey)
            drawable?.setTint(color)
            textViewDelete.setTextColor(color)
        }

        textViewDelete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        // Следим за изменениями текста в EditText
        editViewTask.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не нужно ничего делать перед изменением текста
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Проверяем, содержит ли EditText текст и изменяем цвет TextView и Drawable соответствующим образом
                if (s.isNullOrEmpty()) {
                    val color = ContextCompat.getColor(requireContext(), R.color.lightGrey)
                    drawable?.setTint(color)
                    textViewDelete.setTextColor(color)
                } else {
                    val color = ContextCompat.getColor(requireContext(), R.color.redFormsColor)
                    drawable?.setTint(color)
                    textViewDelete.setTextColor(color)
                }
                // Применяем измененный Drawable к TextView
                textViewDelete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }

            override fun afterTextChanged(s: Editable?) {
                // Не нужно ничего делать после изменения текста
            }
        })
    }

}

