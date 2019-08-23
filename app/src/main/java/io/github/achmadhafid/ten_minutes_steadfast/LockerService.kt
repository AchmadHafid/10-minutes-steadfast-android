package io.github.achmadhafid.ten_minutes_steadfast

import android.Manifest
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import io.github.achmadhafid.zpack.ktx.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PARAM_KEY_START_TIME    = "startTime"
private const val PARAM_KEY_SCAN_INTERVAL = "scanInterval"
private const val PARAM_KEY_LOCK_DURATION = "lockDuration"

class LockerService : LifecycleService() {

    //region Resource Binding

    private val notificationId by lazy { resources.getInteger(R.integer.notification_id) }
    private val notificationChannelId by lazy { getString(R.string.notification_channel_id) }
    private val notificationChannelName by lazy { getString(R.string.notification_channel_name) }
    private val notificationChannelDesc by lazy { getString(R.string.notification_channel_description) }
    private val notificationTitle by lazy { getString(R.string.locker_notification_title) }
    private val notificationContent by lazy { getString(R.string.locker_notification_content) }
    private val scanInterval by lazy { resources.getInteger(R.integer.scan_interval).toLong() }
    private val lockDuration by lazy { resources.getInteger(R.integer.lock_duration).toLong() }

    //endregion

    //region Lifecycle Callback

    override fun onCreate() {
        super.onCreate()
        telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        //region check required conditions

        if (isForeground) {
            return START_STICKY
        } else {
            isForeground = true
        }

        if (!isAdminActive) {
            stopSelf()
            return START_NOT_STICKY
        }

        //endregion
        //region extract params

        val startTime = intent?.extras?.getLong(PARAM_KEY_START_TIME, System.currentTimeMillis()) ?: 0L
        val interval  = intent?.extras?.getLong(PARAM_KEY_SCAN_INTERVAL, scanInterval) ?: 0L
        val duration  = intent?.extras?.getLong(PARAM_KEY_LOCK_DURATION, lockDuration) ?: 0L

        //endregion
        //region create notification channel (required for API 26+)

        @TargetApi(Build.VERSION_CODES.O)
        if (atLeastOreo()) {
            NotificationChannel(
                notificationChannelId,
                notificationChannelName,
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                notificationManager
                    .createNotificationChannel(it.apply {
                        importance = NotificationManager.IMPORTANCE_HIGH
                        description = notificationChannelDesc
                    })
            }
        }

        //endregion
        //region register this service as foreground service

        startForeground(notificationId, NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setSmallIcon(R.drawable.ic_lock_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build())

        //endregion
        //region start scanner

        lifecycleScope.launch {
            while(true) {
                if (System.currentTimeMillis() - startTime > duration) {
                    stopSelf()
                } else {
                    if ((!isDeviceLocked || isScreenOn) && PhoneListener.isIdle()) {
                        devicePolicyManager.lockNow()
                    }
                }
                delay(interval)
            }
        }

        //endregion

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isForeground = false
        telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_NONE)
    }

    //endregion
    //region Object Helper

    object PhoneListener : PhoneStateListener() {
        private var state: Int = TelephonyManager.CALL_STATE_IDLE
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            PhoneListener.state = state
        }
        fun isIdle() = state == TelephonyManager.CALL_STATE_IDLE
    }

    companion object {
        var isForeground = false
            private set

        fun run(context: Context, startTime: Long, scanInterval: Long, lockDuration: Long) {
            ActivityCompat.startForegroundService(context, Intent(context, LockerService::class.java).apply {
                putExtra(PARAM_KEY_START_TIME, startTime)
                putExtra(PARAM_KEY_SCAN_INTERVAL, scanInterval)
                putExtra(PARAM_KEY_LOCK_DURATION, lockDuration)
            })
        }
    }

    //endregion

}

//region Extension Helper

fun HomeActivity.startLockerService() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        requestPermissions(arrayOf(Manifest.permission.FOREGROUND_SERVICE), 1234)
    }
    LockerService.run(
        context = this,
        startTime = System.currentTimeMillis(),
        scanInterval = resources.getInteger(R.integer.scan_interval).toLong(),
        lockDuration = resources.getInteger(R.integer.lock_duration).toLong()
    )
    finish()
}

//endregion
