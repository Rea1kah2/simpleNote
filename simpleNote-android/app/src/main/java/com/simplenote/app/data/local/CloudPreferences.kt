package com.simplenote.app.data.local

import android.content.Context
import android.content.SharedPreferences

class CloudPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("simplenote_cloud_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CLOUD_URL = "key_cloud_url"
        private const val KEY_AUTO_SYNC = "key_auto_sync"
        private const val KEY_LAST_SYNC_TIME = "key_last_sync_time"
        private const val KEY_LAST_SYNC_STATUS = "key_last_sync_status"
    }

    var cloudUrl: String
        get() = prefs.getString(KEY_CLOUD_URL, "https://simple-note-umber.vercel.app") ?: "https://simple-note-umber.vercel.app"
        set(value) = prefs.edit().putString(KEY_CLOUD_URL, value.trim()).apply()

    var isAutoSyncEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SYNC, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_SYNC, value).apply()

    var lastSyncTime: Long
        get() = prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_SYNC_TIME, value).apply()

    var lastSyncStatus: String
        get() = prefs.getString(KEY_LAST_SYNC_STATUS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_SYNC_STATUS, value).apply()
}
