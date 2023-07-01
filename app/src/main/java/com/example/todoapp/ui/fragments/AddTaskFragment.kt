package com.example.todoapp.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateFormat
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
import androidx.navigation.NavArgs
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.ui.adaters.TaskAdapter
import com.example.todoapp.ui.viewmodels.TasksViewModel
import com.example.todoapp.ui.viewmodels.ToDoViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class AddTaskFragment : Fragment() {

    private lateinit var taskViewModel: TasksViewModel

    lateinit var editViewTask: EditText
    lateinit var dateTextView: TextView
    lateinit var textImportance: TextView

    private val args by navArgs<AddTaskFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        taskViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        val switchCalendar: SwitchCompat = view.findViewById(R.id.switchDate)
        editViewTask = view.findViewById(R.id.EditTextTaskOf)
        dateTextView = view.findViewById(R.id.dateTextView)
        val importantTextView: TextView = view.findViewById(R.id.importance_button)
        val buttonClose: ImageButton = view.findViewById(R.id.imageButtonClose)
        val textViewDelete: TextView = view.findViewById(R.id.DeleteTextView)
        val textViewSafe: TextView = view.findViewById(R.id.SafeTextView)
        textImportance = view.findViewById(R.id.text_importance)

        //deleteViewChanges(view)

        if(args.currentTask != null) {
            editViewTask.setText(args.currentTask!!.textOfTask)
            dateTextView.text = args.currentTask!!.deadline
            textImportance.text = args.currentTask!!.importance
        }
        if(dateTextView != null)
        {
            switchCalendar.isChecked
        }

        /*val task: ToDoItem? = idTask?.let { taskViewModel.getTaskById(it) }
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
        }*/

        importantTextView.setOnClickListener() {
            showImportanceList(importantTextView, textImportance)
        }

        // Нажатие на кнопку "Сохранить"
        textViewSafe.setOnClickListener() {

            Log.d("Logcat","args.currentTask: " + args.currentTask)

            if(args.currentTask == null) {
                addNewTaskToDatabase()
                Log.d("Logcat","go here")

            } else {
                updateItem()
            }
            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }

        // Нажатие на кнопку "Крестик"
        buttonClose.setOnClickListener() {
            Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_mainFragment)
        }

        // Нажатие на кнопку "Удалить"
        textViewDelete.setOnClickListener() {
            deleteTask()
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

    private fun getCurrentDate(): String = DateFormat.format("dd, MMM, yyyy", Date()).toString()

    private fun addNewTaskToDatabase() {
        val textTask: String = editViewTask.text.toString()
        val importance: String = textImportance.text.toString()
        val deadline: String = dateTextView.text.toString()
        val dateOfCreate: String = getCurrentDate()

        if(inputCheck(textTask)){
            val task = ToDoItem(0, dateOfCreate, "", textTask, false, deadline, importance)

            taskViewModel.addTask(task)
            Toast.makeText(requireContext(), "Успешно добавлена задача :)", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Задача не добавлена :(", Toast.LENGTH_LONG).show()
        }
    }

    private fun inputCheck(textTask: String): Boolean {
        return !TextUtils.isEmpty(textTask)
    }

    private fun updateItem() {
        val textTask: String = editViewTask.text.toString()
        val importance: String = textImportance.text.toString()
        val deadline: String = dateTextView.text.toString()
        val dateOfChange: String = getCurrentDate()

        if(inputCheck(textTask)) {
            val updateTask = ToDoItem(args.currentTask!!.id, args.currentTask!!.dateOfCreate,
            dateOfChange, textTask, args.currentTask!!.done, deadline, importance)
            taskViewModel.updateTask(updateTask)
            Toast.makeText(requireContext(), "Успешное изменение задачи!",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Задача не изменена :(", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteTask() {
        if(args.currentTask?.id != 0) {
            args.currentTask?.let { taskViewModel.deleteTask(it) }
            Toast.makeText(requireContext(), "Успешное удаление задачи из БД!",Toast.LENGTH_LONG).show()
        }
        findNavController().navigate(R.id.action_addTaskFragment_to_mainFragment)
    }

    private fun showImportanceList(view: View, textImportance: TextView) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.important_menu, popupMenu.menu)

        var highImportance = popupMenu.menu.getItem(2)
        var spannable = SpannableString(highImportance.title.toString())
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
            } else {
                textImportance.setTextColor(ContextCompat.getColor(view.context, R.color.lightGrey))
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

