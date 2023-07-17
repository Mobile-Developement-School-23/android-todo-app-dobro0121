package com.example.todoapp.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.todoapp.R
import com.example.todoapp.data.utils.Constants.SHARED_PREFERENCES_NOTIFICATIONS_IDS
import com.example.todoapp.data.utils.Constants.SHARED_PREFERENCES_NOTIFICATION_STATUS
import com.example.todoapp.data.utils.Constants.SHARED_PREFERENCES_THEME_OPTION
import java.util.*

class SharedPreferences(val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    init {
        if (!sharedPreferences.contains(DEVICE_TAG)) {
            editor.putString(DEVICE_TAG, UUID.randomUUID().toString())
            editor.apply()
        }
    }

    fun setCurrentToken(token: String) {
        editor.putString(PREF_CURRENT_ACCOUNT_TOKEN, token)
        editor.apply()
    }

    fun getDeviceId() : String {
        return sharedPreferences.getString(DEVICE_TAG, null)?: "0d"
    }

    fun putRevisionId(revision: Int) {
        editor.putInt(REVISION_TAG, revision)
        editor.apply()
    }

    fun getRevisionId() : Int {
        return sharedPreferences.getInt(REVISION_TAG, 1)
    }

    companion object {
        private const val PREF_CURRENT_ACCOUNT_TOKEN = "currentToken"
        private const val REVISION_TAG = "currentRevision"
        private const val DEVICE_TAG = "currentDevice"
    }

    fun putThemeMode(themeMode: String) {
        val themeOptions = context.resources.getStringArray(R.array.theme_options)

        when (themeOptions.indexOf(themeMode)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        editor.putString(SHARED_PREFERENCES_THEME_OPTION, themeMode)
        editor.apply()
    }

    fun getThemeMode(): String {
        return sharedPreferences.getString(SHARED_PREFERENCES_THEME_OPTION, null) ?: "system"
    }

    fun putNotificationStatus(status: Boolean) {
        if (status) editor.putString(SHARED_PREFERENCES_NOTIFICATION_STATUS, "yes")
        else editor.putString(SHARED_PREFERENCES_NOTIFICATION_STATUS, "no")

        editor.apply()
    }

    fun getNotificationStatus(): Boolean? {
        return when (sharedPreferences.getString(SHARED_PREFERENCES_NOTIFICATION_STATUS, null)) {
            "yes" -> true
            "no" -> false
            else -> null
        }
    }

    fun addNotification(id: String): String {
        editor.putString(SHARED_PREFERENCES_NOTIFICATIONS_IDS, getNotificationsIds() + " $id")
        editor.apply()
        return sharedPreferences.getString(SHARED_PREFERENCES_NOTIFICATIONS_IDS, "").toString()
    }

    fun removeNotification(id: String) {
        val s = getNotificationsIds()
        val arr = ArrayList(s.trim().split(" "))
        if (arr.contains(id)) {
            arr.remove(id)
        }
        val res = arr.fold("") { previous, next -> "$previous $next" }
        editor.putString(SHARED_PREFERENCES_NOTIFICATIONS_IDS, res)
        editor.apply()
    }

    fun getNotificationsIds(): String {
        return sharedPreferences.getString(SHARED_PREFERENCES_NOTIFICATIONS_IDS, "").toString().trim()
    }
}