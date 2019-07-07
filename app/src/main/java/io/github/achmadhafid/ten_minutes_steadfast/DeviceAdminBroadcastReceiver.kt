package io.github.achmadhafid.ten_minutes_steadfast

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import io.github.achmadhafid.zpack.ktx.devicePolicyManager

class DeviceAdminBroadcastReceiver : DeviceAdminReceiver()

inline val Context.isAdminActive: Boolean
    get() = devicePolicyManager.isAdminActive(
        ComponentName(this, DeviceAdminBroadcastReceiver::class.java)
    )
