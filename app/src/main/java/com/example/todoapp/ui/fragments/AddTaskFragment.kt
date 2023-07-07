package com.example.todoapp.ui.fragments

import android.annotation.SuppressLint
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
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.retrofit.Importance
import com.example.todoapp.data.utils.ConnectivityObserver
import com.example.todoapp.databinding.FragmentAddTaskBinding
import com.example.todoapp.databinding.FragmentMainBinding
import com.example.todoapp.ui.viewmodels.TasksViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TasksViewModel
    private var task_ = ToDoItem()
    lateinit var editViewTask: EditText
    lateinit var dateTextView: TextView
    lateinit var textImportance: TextView

    private val args by navArgs<AddTaskFragmentArgs>()

    @SuppressLint("SimpleDateFormat")
    val dataFormat = SimpleDateFormat("d MMMM y")

    @SuppressLint("UseCompatTextViewDrawableApis")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        taskViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)
        val id = args.currentTask
        if (id != null && savedInstanceState == null) {
            taskViewModel.getToDoItemById(id)

            lifecycleScope.launch {
                taskViewModel.currentItem.collect {
                    task_ = it
                }
            }
        }
        return FragmentAddTaskBinding.inflate(inflater).also { _binding = it }.root
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
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

            if (task_ != null) {
                binding.EditTextTaskOf.setText(task_.textOfTask)
            }
            if (task_ != null) {
                binding.dateTextView.text = task_.deadline?.let { dataFormat.format(it).toString() }
            }

            makeImportance(task_.importance)
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
                val format = SimpleDateFormat("d MMM yyyy")
                val formatted: String = format.format(utc.time)
                dateTextView.setText(formatted)
            }
        }
    }

    fun convertStringToDateLong(dateString: String): Long {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0L
    }

    private fun getCurrentDate(): String = DateFormat.format("dd, MMM, yyyy", Date()).toString()

    private fun addNewTaskToDatabase() {
        val textTask: String = editViewTask.text.toString()
        val importance: String = textImportance.text.toString()
        val deadline: String = dateTextView.text.toString()
        val dateOfCreate: String = getCurrentDate()

        if(inputCheck(textTask)){
            val task = ToDoItem()

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
        val deadlineLong: Long = convertStringToDateLong(deadline)
        val dateOfChange: String = getCurrentDate()
        val dateOfChangeLong: Long = convertStringToDateLong(dateOfChange)

        if(inputCheck(textTask)) {
            val updateTask = ToDoItem(task_.id, task_.dateOfCreate,
            Date(dateOfChangeLong), textTask, task_.done, Date(deadlineLong), importance)
            taskViewModel.updateTask(updateTask)
            Toast.makeText(requireContext(), "Успешное изменение задачи!",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Задача не изменена :(", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteTask() {
        if(args.currentTask != null) {
            args.currentTask?.let { taskViewModel.deleteTask(task_) } // надо поменять на другой вид айдишника
            //args.currentTask?.let { taskViewModel.deleteTaskFromServer("tonguester", args.currentTask!!.id.toString()) }
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

    fun makeImportance(importance: Importance) {
        when (importance) {
            Importance.basic -> {
                binding.textImportance.text = "Нет"
                task_.importance = Importance.basic
                binding.textImportance.setTextColor(
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.lightGrey
                    )
                )
            }

            Importance.low -> {
                binding.textImportance.text = "Низкий"
                task_.importance = Importance.low
                binding.textImportance.setTextColor(
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.lightGrey
                    )
                )
            }

            Importance.important -> {
                binding.textImportance.text = "!! Высокий"
                task_.importance = Importance.important
                binding.textImportance.setTextColor(
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.redFormsColor
                    )
                )
            }
        }
    }

    private fun createListeners() {
        binding.buttonDeleteTask.setOnClickListener {
            if (taskViewModel.status.value == ConnectivityObserver.Status.Available) {
                args.currentTask?.let { taskViewModel.deleteTaskFromServer(it) }
            } else {
                Toast.makeText(
                    context,
                    R.string.unavailable_network_state_delete_later,
                    Toast.LENGTH_SHORT
                ).show()
            }

            taskViewModel.deleteTask(task_)
            taskViewModel.clearTask()

            findNavController().popBackStack()
        }

        binding.switchDataVisible.setOnCheckedChangeListener { _, switched ->
            if (switched) {
                binding.textviewDateBefore.visibility = View.VISIBLE
                binding.textviewDateBefore.text = dataFormat.format(Date())
                showDateTimePicker()
            } else {
                deleteDate()
            }
        }

        binding.buttonSaveCreate.setOnClickListener {
            if (binding.editText.text.isNullOrBlank()) {
                Toast.makeText(context, R.string.error_enter_tasks_text, Toast.LENGTH_SHORT).show()
                binding.editText.error = getString(R.string.error_enter_tasks_text)
                return@setOnClickListener
            }

            binding.editText.error = null
            currentTask.description = binding.editText.text.toString()
            currentTask.changedAt = Date()


            if (binding.buttonSaveCreate.text == getString(R.string.save_button)) {
                if (viewModel.status.value == ConnectivityObserver.Status.Available) {
                    viewModel.createRemoteTask(currentTask)
                } else {
                    Toast.makeText(
                        context,
                        R.string.unavailable_network_state_update_later,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.updateTask(currentTask)
            } else {
                if (viewModel.status.value == ConnectivityObserver.Status.Available) {
                    viewModel.updateRemoteTask(currentTask)
                } else {
                    Toast.makeText(
                        context,
                        R.string.unavailable_network_state_create_later,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                currentTask.id = UUID.randomUUID().toString()
                viewModel.createTask(currentTask)
            }

            viewModel.clearTask()
            findNavController().popBackStack()
        }

        binding.toolbar.setNavigationOnClickListener {
            viewModel.clearTask()
            findNavController().popBackStack()
        }

        binding.menuImportance.setOnClickListener {
            showImportancePopupMenu(binding.menuImportance)
        }

        datePicker.addOnPositiveButtonClickListener {
            val date: Date
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            date = calendar.time
            binding.textviewDateBefore.visibility = View.VISIBLE
            binding.textviewDateBefore.text = dataFormat.format(date)
            currentTask.deadline = date
        }

        datePicker.addOnNegativeButtonClickListener {
            if (currentTask.deadline == null) deleteDate()
        }

        datePicker.addOnCancelListener {
            if (currentTask.deadline == null) deleteDate()
        }
    }
}

