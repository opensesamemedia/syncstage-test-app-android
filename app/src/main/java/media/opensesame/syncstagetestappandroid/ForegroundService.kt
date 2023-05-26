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
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

const val NOTIFICATION_CHANNEL_ID_PREFIX = "SyncStage_channel"
const val NOTIFICATION_CHANNEL_NAME = "SyncStageMicrophone"

val TAG = "ForegroundService"

class ForegroundService : LifecycleService() {


    companion object {
        var wakeLock: PowerManager.WakeLock? = null
        var notificationId: Int = 1
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    startForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "Stopped service")
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }

                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun releaseWakeLock() {
        wakeLock?.release()
        wakeLock = null

    }

    private fun getNotificationChannelId(): String {
        val notificationChannelId = "${NOTIFICATION_CHANNEL_ID_PREFIX}_$notificationId"
        Log.d(TAG, "Notification channel id: $notificationChannelId")
        return notificationChannelId
    }

    private fun startForegroundService() {
        Log.d(TAG, "Starting FG service")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, getNotificationChannelId())
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.sync_stage_logo)
            .setContentTitle("SyncStage")
            .setContentText("We are streaming data from your microphone.")

        startForeground(notificationId, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_IMMUTABLE
    )

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            getNotificationChannelId(),
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroying FG service")
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        notificationId += 1
    }
}

fun sendCommandToService(action: String, ctx: Context) =
    Intent(ctx, ForegroundService::class.java).also {
        it.action = action
        ctx.startService(it)
    }










