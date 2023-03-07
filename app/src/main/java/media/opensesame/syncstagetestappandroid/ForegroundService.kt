package media.opensesame.syncstagetestappandroid

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

const val NOTIFICATION_CHANNEL_ID_PREFIX = "SyncStage_channel"
const val NOTIFICATION_CHANNEL_NAME = "SyncStageMicrophone"

val TAG = "ForegroundService"

class ForegroundService : LifecycleService() ***REMOVED***


    companion object ***REMOVED***
        var wakeLock: PowerManager.WakeLock? = null
        var notificationId: Int = 1
    ***REMOVED***

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int ***REMOVED***
        intent?.let ***REMOVED***
            when (it.action) ***REMOVED***
                ACTION_START_SERVICE -> ***REMOVED***
                    startForegroundService()
                ***REMOVED***
                ACTION_STOP_SERVICE -> ***REMOVED***
                    Log.d(TAG, "Stopped service")
                    stopForeground(STOP_FOREGROUND_REMOVE)
                ***REMOVED***

                else -> ***REMOVED******REMOVED***
            ***REMOVED***
        ***REMOVED***
        return super.onStartCommand(intent, flags, startId)
    ***REMOVED***

    private fun releaseWakeLock() ***REMOVED***
        wakeLock?.release()
        wakeLock = null

    ***REMOVED***

    override fun onCreate() ***REMOVED***
        super.onCreate()
    ***REMOVED***

    private fun getNotificationChannelId(): String ***REMOVED***
        val notificationChannelId = "$***REMOVED***NOTIFICATION_CHANNEL_ID_PREFIX***REMOVED***_$notificationId"
        Log.d(TAG, "Notification channel id: $notificationChannelId")
        return notificationChannelId
    ***REMOVED***

    private fun startForegroundService() ***REMOVED***
        Log.d(TAG, "Starting FG service")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ***REMOVED***
            createNotificationChannel(notificationManager)
        ***REMOVED***

        val notificationBuilder = NotificationCompat.Builder(this, getNotificationChannelId())
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.sync_stage_logo)
            .setContentTitle("SyncStage")
            .setContentText("We are streaming data from your microphone.")

        startForeground(notificationId, notificationBuilder.build())
    ***REMOVED***

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also ***REMOVED***
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
      ***REMOVED***
        FLAG_IMMUTABLE
    )

    private fun createNotificationChannel(notificationManager: NotificationManager) ***REMOVED***
        val channel = NotificationChannel(
            getNotificationChannelId(),
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    ***REMOVED***

    override fun onDestroy() ***REMOVED***
        super.onDestroy()
        Log.d(TAG, "Destroying FG service")
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        notificationId += 1
    ***REMOVED***
***REMOVED***

fun sendCommandToService(action: String, ctx: Context) =
    Intent(ctx, ForegroundService::class.java).also ***REMOVED***
        it.action = action
        ctx.startService(it)
***REMOVED***










