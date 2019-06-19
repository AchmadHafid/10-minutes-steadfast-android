package io.github.achmadhafid.ten_minutes_steadfast

import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.telephony.TelephonyManager

val Context.powerManager
    get() = getSystemService(Context.POWER_SERVICE) as PowerManager
val Context.activityManager
    get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
val Context.devicePolicyManager
    get() = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
val Context.keyGuardManager
    get() = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
val Context.notificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
val Context.telephonyManager
    get() = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
val Context.isDeviceLocked
    get() = if (atLeastLollipopMR1()) keyGuardManager.isDeviceLocked
    else keyGuardManager.isKeyguardLocked
val Context.isScreenOn
    get() = powerManager.isInteractive
val Context.adminSettingIntent: Intent
    get() = android.content.Intent()
        .setComponent(
            android.content.ComponentName(
                "com.android.settings",
                "com.android.settings.DeviceAdminSettings"
            )
        )

fun Context.lockDevice() = devicePolicyManager.lockNow()

fun <A : DeviceAdminReceiver> Context.isAdminActive(adminReceiver: Class<A>): Boolean {
    return devicePolicyManager.isAdminActive(
        android.content.ComponentName(
            this,
            adminReceiver
        )
    )
}

fun Context.isForegroundServiceRunning(serviceClassName: String) =
    getRunningServiceInfo(serviceClassName)?.foreground ?: false

@Suppress("DEPRECATION")
fun Context.getRunningServiceInfo(serviceClassName: String): ActivityManager.RunningServiceInfo? {
    for (serviceInfo in activityManager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClassName == serviceInfo.service.className) {
            return serviceInfo
        }
    }
    return null
}

fun atLeastLollipopMR1() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
fun atLeastOreo()        = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun runDelayed(delay: Long, code: () -> Unit) {
    Handler(Looper.getMainLooper())
        .postDelayed(code, delay)
}
