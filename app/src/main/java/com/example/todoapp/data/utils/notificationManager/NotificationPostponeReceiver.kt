package com.example.todoapp.data.utils.notificationManager

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.util.Log
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.repositories.ToDoItemRepository
import com.example.todoapp.data.utils.Constants.ONE_DAY_IN_MILLIS
import com.example.todoapp.data.utils.Constants.TAG_NOTIFICATION_TASK
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NotificationPostponeReceiver : BroadcastReceiver() {

    lateinit var coroutineScope: CoroutineScope

    lateinit var repository: ToDoItemRepository
    override fun onReceive(context: Context?, intent: Intent?) {
        val gson = Gson()
        val item = gson.fromJson(intent!!.getStringExtra(TAG_NOTIFICATION_TASK), ToDoItem::class.java)
        val manager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(item.id.hashCode())
        try {
            coroutineScope.launch(Dispatchers.IO) {
                val dateString = item.deadline
                val format = SimpleDateFormat("d MMMM yyyy")
                val date: Date = format.parse(dateString)
                val millis: Long = date.time
                if (item.deadline != null) {
                    repository.updateTask(item.copy(deadline = DateFormat.format("dd, MMM, yyyy",Date(millis + ONE_DAY_IN_MILLIS)).toString()))
                }
            }
        } catch (err: Exception) {
            Log.e(NotificationPostponeReceiver::class.java.simpleName, err.message.toString())
        }
    }
}