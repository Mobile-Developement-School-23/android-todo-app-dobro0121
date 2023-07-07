package com.example.todoapp.data.retrofit

import com.example.todoapp.data.models.ToDoItem
import com.google.gson.annotations.SerializedName
import java.util.*

data class ToDoApiResponseList(
    @SerializedName("status")
    val status: String,
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("list")
    val list: List<ToDoItemModel>
)

data class ToDoApiRequestList(
    @SerializedName("status")
    val status: String,
    @SerializedName("list")
    val list: List<ToDoItemModel>
)

data class ToDoApiResponseElement(
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("element")
    val element: ToDoItemModel
)

data class ToDoApiRequestElement(
    @SerializedName("element")
    val element: ToDoItemModel
)

enum class Importance {
    low, basic, important
}

data class ToDoItemModel(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("importance")
    val importance: Importance,

    @SerializedName("deadline")
    val deadline: Long?,

    @SerializedName("done")
    val done: Boolean,

    @SerializedName("color")
    val color: String,

    @SerializedName("created_at")
    val created_at: Long,

    @SerializedName("changed_at")
    val changed_at: Long,

    @SerializedName("last_updated_by")
    val last_updated_by: String
) {
    fun toToDoItem(): ToDoItem = ToDoItem(
        id,
        Date(created_at),
        Date(changed_at),
        text,
        done,
        deadline?.let { Date(it) },
        importance
    )

    companion object {
        fun fromToDoTask(toDoItem: ToDoItem, deviseId: String): ToDoItemModel {
            return ToDoItemModel(
                id = toDoItem.id,
                text = toDoItem.textOfTask,
                importance = toDoItem.importance,
                deadline = toDoItem.deadline?.time,
                done = toDoItem.done,
                created_at = toDoItem.dateOfCreate.time,
                changed_at = toDoItem.dateOfChange?.time ?: 0,
                last_updated_by = deviseId,
                color = ""
            )
        }
    }
}