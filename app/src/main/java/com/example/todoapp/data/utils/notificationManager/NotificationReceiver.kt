package com.example.todoapp.data.utils.notificationManager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NotificationReceiver : BroadcastReceiver() {
    lateinit var scheduler: NotificationSchedulerImplement
    lateinit var coroutineScope: CoroutineScope
    @SuppressLint("StringFormatInvalid")
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val gson = Gson()
            val task = gson.fromJson(
                intent!!.getStringExtra(Constants.TAG_NOTIFICATION_TASK),
                ToDoItem::class.java
            )

            coroutineScope.launch(Dispatchers.IO) {

                val manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                manager.createNotificationChannel(
                    NotificationChannel(
                        Constants.NOTIFICATION_CHANNEL_ID,
                        Constants.NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                )

                val notification =
                    NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.info_outline)
                        .setContentTitle(context.getString(R.string.hour_before_task))
                        .setContentText(
                            context.getString(
                                R.string.notification_text,
                                task.importance.toString().lowercase(Locale.ROOT),
                                task.textOfTask
                            )
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(
                            NotificationCompat.Action(
                                R.drawable.baseline_delay,
                                context.getString(R.string.postpone_for_a_day),
                                postponeIntent(context, task)
                            )
                        )
                        .setContentIntent(deepLinkIntent(context, task.id))
                        .build()

                scheduler.cancel(task)
                manager.notify(task.id.hashCode(), notification)
            }
        } catch (err: Exception) {
            Log.e(NotificationReceiver::class.java.simpleName, err.stackTraceToString())
        }
    }

    private fun deepLinkIntent(context: Context, newTaskArg: Int): PendingIntent =
        NavDeepLinkBuilder(context)
            .setGraph(R.navigation.fragment_navigation)
            .setDestination(R.id.addTaskFragment, bundleOf("newTaskArg" to newTaskArg))
            .createPendingIntent()

    private fun postponeIntent(context: Context, item: ToDoItem): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            item.id.hashCode(),
            Intent(
                context,
                NotificationPostponeReceiver::class.java
            ).apply {
                putExtra(Constants.TAG_NOTIFICATION_TASK, item.toString())
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

}