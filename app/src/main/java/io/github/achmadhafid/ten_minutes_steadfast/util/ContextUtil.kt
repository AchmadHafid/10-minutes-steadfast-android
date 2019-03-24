package io.github.achmadhafid.ten_minutes_steadfast.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.PowerManager
import android.telephony.TelephonyManager
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("WrongConstant")
@Singleton
class ContextUtil
@Inject constructor(val context: Context) {

    val powerManager by lazy { context.getSystemService(Context.POWER_SERVICE) as PowerManager }
    val activityManager by lazy { context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }
    val devicePolicyManager by lazy { context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
    val keyGuardManager by lazy { context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager }
    val notificationManager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    val telephonyManager by lazy { context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }

    fun lockDevice() = devicePolicyManager.lockNow()

    fun isScreenOn() = powerManager.isInteractive

    fun isDeviceLocked() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
        keyGuardManager.isDeviceLocked
    } else {
        keyGuardManager.isKeyguardLocked
    }

    fun <A: DeviceAdminReceiver> isAdminActive(adminReceiver: Class<A>) =
        devicePolicyManager.isAdminActive(
            android.content.ComponentName(
                context,
                adminReceiver
            )
        )

    fun isForegroundServiceRunning(serviceClassName: String) =
        getRunningServiceInfo(serviceClassName)?.foreground ?: false

    @Suppress("DEPRECATION")
    fun getRunningServiceInfo(serviceClassName: String): ActivityManager.RunningServiceInfo? {
        for (serviceInfo in activityManager.getRunningServices(java.lang.Integer.MAX_VALUE)) {
            if (serviceClassName == serviceInfo.service.className) {
                return serviceInfo
            }
        }
        return null
    }

    fun getAdminSettingIntent() =
        android.content.Intent()
            .setComponent(android.content.ComponentName(
                "com.android.settings",
                "com.android.settings.DeviceAdminSettings"
            ))

}
