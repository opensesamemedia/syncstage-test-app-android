package media.opensesame.syncstagetestappandroid.repo

import android.content.Context
import javax.inject.Inject

class PreferencesRepo @Inject constructor(
    private val context: Context
) ***REMOVED***
    private val sharedPreferencesKey = "TestApp"
    private val userNameKey = "userName"
    private val userIdKey = "userId"

    fun updateUserName(userName: String) ***REMOVED***
        print(userName)
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPref.edit()) ***REMOVED***
            putString(userNameKey, userName)
            apply()
        ***REMOVED***
    ***REMOVED***

    fun getUserName(): String ***REMOVED***
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val userName = sharedPref.getString(userNameKey, "")
        return userName ?: ""
    ***REMOVED***

    fun getUserId(): String ***REMOVED***
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val userName = sharedPref.getString(userIdKey, "")
        return userName ?: ""
    ***REMOVED***

    fun updateUserId(userId: String) ***REMOVED***
        print(userId)
        val sharedPref = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPref.edit()) ***REMOVED***
            putString(userIdKey, userId)
            apply()
        ***REMOVED***
    ***REMOVED***
***REMOVED***