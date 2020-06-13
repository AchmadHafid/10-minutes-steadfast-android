package io.github.achmadhafid.ten_minutes_steadfast

import android.Manifest
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import io.github.achmadhafid.zpack.extension.atLeastOreo
import io.github.achmadhafid.zpack.extension.atLeastPie
import io.github.achmadhafid.zpack.extension.devicePolicyManager
import io.github.achmadhafid.zpack.extension.intRes
import io.github.achmadhafid.zpack.extension.intent
import io.github.achmadhafid.zpack.extension.isDeviceLocked
import io.github.achmadhafid.zpack.extension.isScreenOn
import io.github.achmadhafid.zpack.extension.notificationManagerCompat
import io.github.achmadhafid.zpack.extension.stringRes
import io.github.achmadhafid.zpack.extension.telephonyManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LockerService : LifecycleService() {

    //region Resource Binding

    private val notificationId by intRes(R.integer.notification_id)
    private val notificationChannelId by stringRes(R.string.notification_channel_id)
    private val notificationChannelName by stringRes(R.string.notification_channel_name)
    private val notificationChannelDesc by stringRes(R.string.notification_channel_description)
    private val notificationTitle by stringRes(R.string.locker_notification_title)
    private val notificationContent by stringRes(R.string.locker_notification_content)
    private val scanInterval by intRes(R.integer.scan_interval)
    private val lockDuration by intRes(R.integer.lock_duration)

    //endregion
    //region Coroutine Scope

    private val scope by lazy {
        MainScope()
    }

    //endregion
    //region Flag

    private var isForeground = false

    //endregion
    //region Lifecycle Callback

    override fun onCreate() {
        super.onCreate()
        telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        //region check required conditions

        if (isForeground) return START_STICKY
        else isForeground = true

        if (!isAdminActive) {
            stopSelf()
            return START_NOT_STICKY
        }

        //endregion
        //region extract params

        val (startTime, interval, duration) = intent?.extras?.run {
            Triple(
                getLong(PARAM_KEY_START_TIME, System.currentTimeMillis()),
                getLong(PARAM_KEY_SCAN_INTERVAL, scanInterval.toLong()),
                getLong(PARAM_KEY_LOCK_DURATION, lockDuration.toLong())
            )
        } ?: Triple(0L, 0L, 0L)

        //endregion
        //region create notification channel (required for API 26+)

        @TargetApi(Build.VERSION_CODES.O)
        if (atLeastOreo()) {
            NotificationChannel(
                notificationChannelId,
                notificationChannelName,
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                notificationManagerCompat.createNotificationChannel(it.apply {
                        importance = NotificationManagerCompat.IMPORTANCE_HIGH
                        description = notificationChannelDesc
                    })
            }
        }

        //endregion
        //region register this service as foreground service

        startForeground(notificationId, NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setSmallIcon(R.drawable.ic_lock_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build())

        //endregion
        //region start scanner

        scope.launch {
            while(true) {
                if (!isActive) return@launch

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
        scope.cancel()
        isForeground = false
        telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_NONE)
    }

    //endregion

}

//region Parameter Key

private const val PARAM_KEY_START_TIME    = "startTime"
private const val PARAM_KEY_SCAN_INTERVAL = "scanInterval"
private const val PARAM_KEY_LOCK_DURATION = "lockDuration"

//endregion
//region Extension Helper

fun AppCompatActivity.startLockerService(
    scanInterval: Long,
    lockDuration: Long,
    startTime: Long = System.currentTimeMillis()
) {
    @TargetApi(Build.VERSION_CODES.P)
    if (atLeastPie()) requestPermissions(arrayOf(Manifest.permission.FOREGROUND_SERVICE), 1234)
    ActivityCompat.startForegroundService(this, intent<LockerService> {
        putExtra(PARAM_KEY_START_TIME, startTime)
        putExtra(PARAM_KEY_SCAN_INTERVAL, scanInterval)
        putExtra(PARAM_KEY_LOCK_DURATION, lockDuration)
    })
}

//endregion
