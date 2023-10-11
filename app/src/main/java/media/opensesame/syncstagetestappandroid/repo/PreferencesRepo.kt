package media.opensesame.syncstagetestappandroid.repo

import android.content.Context
import javax.inject.Inject

class PreferencesRepo @Inject constructor(
    private val context: Context
) {
    private val sharedPreferencesKey = "TestApp"
    private val userNameKey = "userName"
    private val userIdKey = "userId"
    private val zoneIdKey = "zoneId"
    private val studioServerIdKey = "studioServerId"
    private val syncStageSecretKey = "syncStageSecret"

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

    fun updateZoneId(zoneId: String) {
        print(zoneId)
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(zoneIdKey, zoneId)
            apply()
        }
    }

    fun getZoneId(): String {
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val zoneId = sharedPref.getString(zoneIdKey, "")
        return zoneId ?: ""
    }

    fun updateStudioServerId(studioServerId: String) {
        print(studioServerId)
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(studioServerIdKey, studioServerId)
            apply()
        }
    }

    fun getStudioServerId(): String {
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val studioServerId = sharedPref.getString(studioServerIdKey, "")
        return studioServerId ?: ""
    }

    fun updateSyncStageSecret(syncStageSecret: String) {
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(syncStageSecretKey, syncStageSecret)
            apply()
        }
    }

    fun getSyncStageSecret(): String? {
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        return sharedPref.getString(syncStageSecretKey, null)
    }
}