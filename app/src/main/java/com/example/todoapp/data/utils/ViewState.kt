package com.example.todoapp.data.utils

import com.example.todoapp.data.models.ToDoItem

sealed class ViewState {
    object ShowLoading: ViewState()
    class ShowError(val error: Throwable): ViewState()
    class ShowData(val data: List<ToDoItem>): ViewState()
}