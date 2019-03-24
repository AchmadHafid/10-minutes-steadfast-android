package io.github.achmadhafid.ten_minutes_steadfast.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import io.github.achmadhafid.ten_minutes_steadfast.R
import io.github.achmadhafid.ten_minutes_steadfast.di.inject
import io.github.achmadhafid.ten_minutes_steadfast.ext.log
import io.github.achmadhafid.ten_minutes_steadfast.receiver.DeviceAdminBroadcastReceiver
import io.github.achmadhafid.ten_minutes_steadfast.util.ContextUtil
import javax.inject.Inject

private const val PARAM_KEY_START_TIME    = "startTime"
private const val PARAM_KEY_SCAN_INTERVAL = "scanInterval"
private const val PARAM_KEY_LOCK_DURATION = "lockDuration"

class LockerService : Service() {

    @Inject
    lateinit var contextUtil: ContextUtil

    //region Object Helper

    object PhoneListener : PhoneStateListener() {
        private var state: Int = TelephonyManager.CALL_STATE_IDLE
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            PhoneListener.state = state
        }

        fun isIdle() = state == TelephonyManager.CALL_STATE_IDLE
    }

    companion object {
        fun run(context: Context, startTime: Long, scanInterval: Long, lockDuration: Long) {
            ActivityCompat.startForegroundService(context, Intent(context, LockerService::class.java).apply {
                putExtra(PARAM_KEY_START_TIME, startTime)
                putExtra(PARAM_KEY_SCAN_INTERVAL, scanInterval)
                putExtra(PARAM_KEY_LOCK_DURATION, lockDuration)
            })
        }
    }

    //endregion
    //region Properties

    private val handler: Handler = Handler()
    private val notificationId by lazy { resources.getInteger(R.integer.notification_id) }
    private val notificationChannelId by lazy { getString(R.string.notification_channel_id) }

    //endregion
    //region Lifecycle Callback

    override fun onCreate() {
        super.onCreate()
        inject()
        contextUtil.telephonyManager
            .listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //region check required conditions

        if (contextUtil.isForegroundServiceRunning(LockerService::class.java.name)) {
            return START_STICKY
        }

        if (!contextUtil.isAdminActive(DeviceAdminBroadcastReceiver::class.java)) {
            stopSelf()
            return START_NOT_STICKY
        }

        //endregion
        //region extract params

        val extras = intent?.extras

        val startTime = extras?.getLong(PARAM_KEY_START_TIME, System.currentTimeMillis()) ?: 0L
        val interval  = extras?.getLong(PARAM_KEY_SCAN_INTERVAL, resources.getInteger(R.integer.scan_interval).toLong()) ?: 0L
        val duration  = extras?.getLong(PARAM_KEY_LOCK_DURATION, resources.getInteger(R.integer.lock_duration).toLong()) ?: 0L

        //endregion
        //region create notification channel (required for API 26+)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                    notificationChannelId,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            ).let {
                contextUtil.notificationManager
                        .createNotificationChannel(it.apply {
                            importance = NotificationManager.IMPORTANCE_HIGH
                            description = getString(R.string.notification_channel_description)
                        })
            }
        }

        //endregion
        //region register this service as foreground service

        startForeground(notificationId, NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle(getString(R.string.locker_notification_title))
                .setContentText(getString(R.string.locker_notification_content))
                .setSmallIcon(R.drawable.ic_lock_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build())

        //endregion
        //region start scanner

        scan(handler, startTime, interval, duration)

        //endregion

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        contextUtil.telephonyManager
            .listen(PhoneListener, PhoneStateListener.LISTEN_NONE)
    }

    //endregion
    //region Private Helper

    private fun scan(handler: Handler, startTime: Long, interval: Long, duration: Long) {
        if (System.currentTimeMillis() - startTime > duration) {
            log("locker service stopped")
            stopSelf()
        } else {
            if ((!contextUtil.isDeviceLocked() || contextUtil.isScreenOn()) && PhoneListener.isIdle()) {
                contextUtil.lockDevice()
            }
            log("locker service running: ${(startTime + duration - System.currentTimeMillis()) / 1000L} second(s) until stopped")
            handler.postDelayed({ scan(handler, startTime, interval, duration) }, interval)
        }
    }

    //endregion

}
