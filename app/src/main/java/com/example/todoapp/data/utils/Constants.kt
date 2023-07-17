package com.example.todoapp.data.utils

object Constants {
    const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"
    const val SHARED_PREFERENCES_THEME_OPTION = "theme_option"
    const val SHARED_PREFERENCES_NOTIFICATION_STATUS = "notification_status"
    const val SHARED_PREFERENCES_NOTIFICATIONS_IDS = "notifications_id"
    const val NOTIFICATION_CHANNEL_ID = "notification"
    const val NOTIFICATION_CHANNEL_NAME = "notification_name"
    const val TAG_NOTIFICATION_TASK = "task"
    const val REPEAT_INTERVAL: Long = 8
    const val RETROFIT_TIMEOUT : Long = 10

    const val ONE_HOUR_IN_MILLIS = 3600000
    const val ONE_DAY_IN_MILLIS = 86400000

    const val TIMER_START : Long = 5000
    const val TIMER_END : Long = 1000
    const val TIMER_ONE_SECOND : Long = 1000
}