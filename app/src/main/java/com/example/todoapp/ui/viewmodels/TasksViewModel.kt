package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.*
import com.example.todoapp.data.databases.TaskDao
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.repositories.ToDoItemRepository
import com.example.todoapp.data.retrofit.LoadingState
import com.example.todoapp.data.retrofit.Tasks
import com.example.todoapp.data.retrofit.ToDoItemApi
import com.example.todoapp.data.retrofit.ToDoItemModel
import com.example.todoapp.data.utils.*
import com.example.todoapp.locateLazy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class TasksViewModel(private val taskRepository: ToDoItemRepository,
                     private val connection: NetworkConnectivityObserver): ViewModel() {
    var job : Job? = null

    private val _tasks = MutableSharedFlow<List<ToDoItem>>()
    val allTasks: SharedFlow<List<ToDoItem>> = _tasks.asSharedFlow()

    private val _status = MutableStateFlow(ConnectivityObserver.Status.Unavailable)
    val status = _status.asStateFlow()

    private val _loadingState =
        MutableStateFlow<LoadingState<Any>>(LoadingState.Success("Loaded from rood complete!"))
    val loadingState: StateFlow<LoadingState<Any>> = _loadingState.asStateFlow()

    val countCompletedTask: Flow<Int> = _tasks.map { it -> it.count { it.done } }

    private var _currentItem = MutableStateFlow(ToDoItem())
    var currentItem = _currentItem.asStateFlow()

    var showAll = true

    init {
        observeNetwork()
        loadTasks()
    }

    fun changeMode() {
        job?.cancel()
        showAll = !showAll
        loadTasks()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            connection.observe().collectLatest {
                _status.emit(it)
            }
        }
    }

    fun loadTasks() {
        job = viewModelScope.launch(Dispatchers.IO) {
            _tasks.emitAll(taskRepository.getAllData())
        }
    }

    fun getToDoItemById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentItem.emit(taskRepository.getToDoItemById(id))
        }
    }

    fun getAllTasksFromServer() {
        if (status.value == ConnectivityObserver.Status.Available) {
            _loadingState.value = LoadingState.Loading(true)
            viewModelScope.launch(Dispatchers.IO) {
                _loadingState.emit(taskRepository.getAllTasksFromServer())
            }
        }
    }

    fun changeVisibilityButton(flag: Boolean): Boolean {
        return !flag
    }

    fun addTask(task: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.addTask(task) }
    }

    fun addTaskToServer(todoItem: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.addTaskToServer(todoItem)
        }
    }

    fun deleteTask(task: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.deleteTask(task) }
    }

    fun deleteTaskFromServer(id: String) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.deleteTaskFromServer(id) }
    }

    fun updateTask(task: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.updateTask(task) }
    }

    fun updateTaskOnServer(toDoItem: ToDoItem) {
        val item = toDoItem.copy(done = !toDoItem.done)
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTaskOnServer(item)
        }
    }

    fun getCountCompleted(): LiveData<Int> { return taskRepository.getCountCompleted() }

    private fun <T> Flow<T>.asLiveDataFlow() = shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    fun getShowDone(): Boolean {
        return taskRepository.getShowDone()
    }

    fun updateShowDone(newVaue: Boolean) {
        taskRepository.updateShowDone(newVaue)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}