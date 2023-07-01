package com.example.todoapp.ui.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.App
import com.example.todoapp.R
import com.example.todoapp.data.databases.TaskRoomDatabase
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.repositories.ToDoItemRepository
import com.example.todoapp.ui.adaters.TaskAdapter
import com.example.todoapp.ui.viewmodels.TasksViewModel
import com.example.todoapp.ui.viewmodels.ToDoViewModel
import com.example.todoapp.ui.viewmodels.ToDoViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class MainFragment() : Fragment() {

    private lateinit var taskViewModel: TasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_tasks)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = TaskAdapter()
        recyclerView.adapter = adapter

        taskViewModel.allTasks.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.setTasks(tasks)
            adapter.notifyDataSetChanged()
        })

        val addButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        addButton.setOnClickListener {
            findNavController(view).navigate(R.id.action_mainFragment_to_addTaskFragment)
        }

        val showDoneButton = view.findViewById<ImageButton>(R.id.imageButtonVisibility)
        showDoneButton.setOnClickListener {
            val currentValue = taskViewModel.getShowDone() // Получаем текущее состояниеглазика
            lateinit var tasksToShow: List<ToDoItem> // здесь будет итоговый список тасков,который необходимо отобразить
            if (currentValue) { // если глазик "включен", значит, необходимо его переключить на "выкл" и отобразить только невыполненные
                tasksToShow = taskViewModel.getUncompletedTasks()
                showDoneButton.setImageResource(R.drawable.visibility_off)
                taskViewModel.updateShowDone(!currentValue) // обновляем состояние глазика в репозитории
            } else { // если глазик "выключен", значит, необходимо его переключить на "вкл" и отобразить все таски
                tasksToShow = taskViewModel.allTasks.value!!
                showDoneButton.setImageResource(R.drawable.visibility)
                taskViewModel.updateShowDone(!currentValue) // обновляем состояние глазика в репозитории
            }
            adapter.setData(tasksToShow) // устанавливаем в адаптер итоговый список тасков
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val currentValue = taskViewModel.getShowDone() // Получаем состояние глазика
                if (currentValue) {
                    showDoneButton.setImageResource(R.drawable.visibility)
                } else {
                    showDoneButton.setImageResource(R.drawable.visibility_off)
                }
                val tasksToShow = if (currentValue) {
                    taskViewModel.allTasks.value // Показать все задачи
                } else {
                    taskViewModel.getUncompletedTasks() // Показать только невыполненные задачи
                }
                if (tasksToShow != null) {
                    adapter.setData(tasksToShow)
                }
            }
        }

        taskViewModel.allTasks.observe(viewLifecycleOwner) {
            val currentValue = taskViewModel.getShowDone() // Получаем состояние глазика
            if (currentValue) {
                showDoneButton.setImageResource(R.drawable.visibility)
            } else {
                showDoneButton.setImageResource(R.drawable.visibility_off)
            }
            val tasksToShow = if (currentValue) {
                taskViewModel.allTasks.value // Показать все задачи
            } else {
                taskViewModel.getUncompletedTasks() // Показать только невыполненные задачи
            }
            adapter.setData(tasksToShow!!)
        }
    }
}





