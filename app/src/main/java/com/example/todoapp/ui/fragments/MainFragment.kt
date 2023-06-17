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
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.ui.adaters.TaskAdapter
import com.example.todoapp.ui.viewmodels.ToDoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment() : Fragment() {

    private lateinit var taskViewModel: ToDoViewModel
    private var onItemClickListener: AdapterView.OnItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_tasks)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter: TaskAdapter = TaskAdapter()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        taskViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        taskViewModel.taskList.observe(viewLifecycleOwner) { tasks ->
            adapter.setTasks(tasks)
            adapter.notifyDataSetChanged()
        }

        val tasks = taskViewModel.getTasks()
        Log.d("TaskList", tasks.toString())

        val addButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        addButton.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("id", "addTask_")
            findNavController(view).navigate(R.id.action_mainFragment_to_addTaskFragment, bundle)
        }
    }
}





