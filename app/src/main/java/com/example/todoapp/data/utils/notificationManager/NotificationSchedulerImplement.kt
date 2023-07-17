package com.example.todoapp.data.utils.notificationManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.todoapp.data.SharedPreferences
import com.example.todoapp.data.models.ToDoItem
import com.example.todoapp.data.utils.Constants.ONE_HOUR_IN_MILLIS
import com.example.todoapp.data.utils.Constants.TAG_NOTIFICATION_TASK
import java.text.SimpleDateFormat
import java.util.*

class NotificationSchedulerImplement(private val context: Context,
                                     private val sharedPreferences: SharedPreferences
): NotificationScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: ToDoItem) {
        val dateString = item.deadline
        val format = SimpleDateFormat("d MMMM yyyy")
        val date: Date = format.parse(dateString)
        val millis: Long = date.getTime()

        if (item.deadline != null) {
            if (millis >= System.currentTimeMillis() + ONE_HOUR_IN_MILLIS
                && !item.done
                && sharedPreferences.getNotificationStatus() == true) {

                val intent = Intent(context, NotificationReceiver::class.java).apply {
                    putExtra(TAG_NOTIFICATION_TASK, item.toString())
                }
                sharedPreferences.addNotification(item.id.hashCode().toString())
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    millis - ONE_HOUR_IN_MILLIS,
                    PendingIntent.getBroadcast(
                        context,
                        item.id.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }
    }

    override fun cancel(item: ToDoItem) {
        sharedPreferences.removeNotification(item.id.hashCode().toString())
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id.hashCode(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancelAll() {
        for (task_id in sharedPreferences.getNotificationsIds().split(" ")) {
            sharedPreferences.removeNotification(task_id)
            alarmManager.cancel(
                PendingIntent.getBroadcast(
                    context,
                    task_id.hashCode(),
                    Intent(context, NotificationReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }
}