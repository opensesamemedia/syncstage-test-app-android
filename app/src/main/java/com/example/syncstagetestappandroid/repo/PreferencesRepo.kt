package com.example.syncstagetestappandroid.repo

import android.content.Context
import media.opensesame.syncstagesdk.SyncStage
import javax.inject.Inject

class PreferencesRepo @Inject constructor(
    private val context: Context
) {
    private val sharedPreferencesKey = "TestApp"
    private val userNameKey = "userName"
    private val userIdKey = "userId"

    fun updateUserName(userName: String) {
        print(userName)
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(userNameKey, userName)
            apply()
        }
    }

    fun getUserName(): String {
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val userName = sharedPref.getString(userNameKey, "")
        return userName ?: ""
    }

    fun getUserId(): String {
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val userName = sharedPref.getString(userIdKey, "")
        return userName ?: ""
    }

    fun updateUserId(userId: String) {
        print(userId)
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(userIdKey, userId)
            apply()
        }
    }
}