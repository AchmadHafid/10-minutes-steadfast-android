package io.github.achmadhafid.ten_minutes_steadfast

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context

val Context.devicePolicyManager
    get() = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

fun Context.lockDevice() = devicePolicyManager.lockNow()

inline fun <reified A : DeviceAdminReceiver> Context.isAdminActive(): Boolean =
    devicePolicyManager.isAdminActive(ComponentName(this, A::class.java))
