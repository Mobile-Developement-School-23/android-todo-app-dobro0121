package com.example.todoapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.retrofit.StatusOfInternet
import com.example.todoapp.data.utils.ViewState
import com.example.todoapp.databinding.FragmentMainBinding
import com.example.todoapp.ui.adaters.TaskAdapter
import com.example.todoapp.ui.viewmodels.TasksViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MainFragment() : Fragment(), TaskAdapter.TaskAdapterListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val taskViewModel: TasksViewModel by viewModels()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskAdapter
    var flagOfVisibilityButton: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding.imageButtonVisibility.callOnClick()
        if(checkForInternet(requireContext())) {
            taskViewModel.getAllTasksFromServer()
        }
        return FragmentMainBinding.inflate(inflater).also { _binding = it }.root
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // WI-FI
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Cellular
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //taskViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)

        views {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_tasks)
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            adapter = TaskAdapter(this@MainFragment)
            recyclerView.adapter = adapter
        }

        taskViewModel.allTasks.onEach(::renderTasks).launchIn(lifecycleScope)

        // Отслеживание кол-ва выполненных тасков
        taskViewModel.getCountCompleted().observe(viewLifecycleOwner) { count ->
            binding.textViewDone.text = getString(R.string.done, count)
        }

        taskViewModel.changeMode()

        val imageButtonVisibility = view.findViewById<ImageButton>(R.id.imageButtonVisibility)
        imageButtonVisibility.setOnClickListener {
            flagOfVisibilityButton = taskViewModel.changeVisibilityButton(flagOfVisibilityButton)
            if(flagOfVisibilityButton){
                imageButtonVisibility.setImageResource(R.drawable.visibility)
            }
            else {
                imageButtonVisibility.setImageResource(R.drawable.visibility_off)
            }
            taskViewModel.changeMode()
        }

        val addButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        addButton.setOnClickListener {
            findNavController(view).navigate(R.id.action_mainFragment_to_addTaskFragment)
        }

        //taskViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)
        //val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_tasks)
        //val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //recyclerView.layoutManager = layoutManager
        //val adapter = TaskAdapter()
        //recyclerView.adapter = adapter

        /*taskViewModel.allTasks.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.setTasks(tasks)
            adapter.notifyDataSetChanged()
        })*/

        /*val showDoneButton = view.findViewById<ImageButton>(R.id.imageButtonVisibility)
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
        }*/
    }

    private fun renderTasks(tasks: List<ToDoItem>) {
        if (taskViewModel.showAll) {
            adapter.submitList(tasks)
        } else {
            adapter.submitList(tasks.filter { !it.done })
        }
    }

    override fun onTaskCheckboxClicked(task: ToDoItem, isChecked: Boolean) {
        // Обработка изменения состояния чекбокса таска
        // Можно здесь обновить состояние таска в базе данных или выполнить другие необходимые действия
        val taskUpdate = ToDoItem(task.id, task.dateOfCreate, task.dateOfChange,
            task.textOfTask, isChecked, task.deadline, task.importance)
        taskViewModel.updateTask(taskUpdate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun <T> views(block: FragmentMainBinding.() -> T): T? = _binding?.block()
}





