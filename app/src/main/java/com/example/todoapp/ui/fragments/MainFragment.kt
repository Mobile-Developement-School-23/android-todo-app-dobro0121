package com.example.todoapp.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.retrofit.LoadingState
import com.example.todoapp.data.utils.ConnectivityObserver
import com.example.todoapp.databinding.FragmentMainBinding
import com.example.todoapp.ui.adaters.TaskAdapter
import com.example.todoapp.ui.viewmodels.TasksViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class MainFragment : Fragment(), TaskAdapter.TaskAdapterListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val taskViewModel: TasksViewModel by viewModels()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskAdapter
    var flagOfVisibilityButton: Boolean = true
    private var internetState = ConnectivityObserver.Status.Unavailable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lifecycleScope.launch {
            taskViewModel.loadingState.collectLatest {
                updateLoadingStatus(it)
            }
        }

        lifecycleScope.launch {
            taskViewModel.status.collectLatest {
                updateNetworkState(it)
            }
        }
        return FragmentMainBinding.inflate(inflater).also { _binding = it }.root
    }

    /*private fun checkForInternet(context: Context): Boolean {
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
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_tasks)
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            adapter = TaskAdapter(object : TaskAdapter.TaskAdapterListener {
                override fun onClickCheck(item: ToDoItem) {
                    if (internetState == ConnectivityObserver.Status.Available) {
                        taskViewModel.updateTaskOnServer(item)
                    } else {
                        Toast.makeText(
                            context,
                            "Нет подключения к интернету, пока работа производится оффлайн",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onTaskCheckboxClicked(task: ToDoItem, isChecked: Boolean) {
                    TODO("Not yet implemented")
                }

                override fun onClickItem(idItem: String) {
                    val action = MainFragmentDirections.actionMainFragmentToAddTaskFragment(idItem)
                    findNavController().navigate(action)
                }
            })
            recyclerView.adapter = adapter
        }

        taskViewModel.allTasks.onEach(::renderTasks).launchIn(lifecycleScope)

        // Отслеживание кол-ва выполненных тасков
        taskViewModel.getCountCompleted().observe(viewLifecycleOwner) { count ->
            binding.textViewDone.text = getString(R.string.done, count)
        }

        lifecycleScope.launch {
            taskViewModel.allTasks.collectLatest {
                updateUI(it)
            }
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

    override fun onClickCheck(item: ToDoItem) {
        if (internetState == ConnectivityObserver.Status.Available) {
            taskViewModel.updateTaskOnServer(item)
        } else {
            Toast.makeText(
                context,
                "No internet connection, will upload with later. Continue offline.",
                Toast.LENGTH_SHORT
            ).show()
        }
        taskViewModel.changeMode()
    }

    override fun onClickItem(idItem: String) {
        val action = MainFragmentDirections.actionMainFragmentToAddTaskFragment(idItem)
        findNavController().navigate(action)
    }

    private fun <T> views(block: FragmentMainBinding.() -> T): T? = _binding?.block()

    private fun updateUI(list: List<ToDoItem>) {
        if (taskViewModel.showAll) {
            adapter.submitList(list)
            binding.recyclerViewTasks.scrollToPosition(0)
        } else {
            adapter.submitList(list.filter { !it.done })
        }
    }


    private fun updateLoadingStatus(loadingState: LoadingState<Any>) {
        when (loadingState) {
            is LoadingState.Loading -> {
                binding.recyclerViewTasks.visibility = View.GONE
                binding.recyclerViewTasks.visibility = View.VISIBLE
            }

            is LoadingState.Success -> {
                binding.recyclerViewTasks.visibility = View.VISIBLE
            }

            is LoadingState.Error -> {
                binding.recyclerViewTasks.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Загрузка провалена, показаны локальные данные",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateNetworkState(status: ConnectivityObserver.Status) {
        when (status) {
            ConnectivityObserver.Status.Available -> {
                if (internetState != status) {
                    Toast.makeText(context, "Есть подключение к интернету", Toast.LENGTH_SHORT)
                        .show()
                    taskViewModel.getAllTasksFromServer()
                }

            }

            ConnectivityObserver.Status.Unavailable -> {
                if (internetState != status) {
                    Toast.makeText(context, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT)
                        .show()
                    taskViewModel.getAllTasksFromServer()
                }
            }

            ConnectivityObserver.Status.Losing -> {
                if (internetState != status) {
                    Toast.makeText(context, "Нестабильно подключение к интернету", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            ConnectivityObserver.Status.Lost -> {
                if (internetState != status) {
                    Toast.makeText(context, "Потеряно подключение к интернету", Toast.LENGTH_SHORT).show()
                }
            }
        }
        internetState = status
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





