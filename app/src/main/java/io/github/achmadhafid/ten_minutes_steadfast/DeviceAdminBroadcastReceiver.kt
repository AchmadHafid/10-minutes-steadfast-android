package io.github.achmadhafid.ten_minutes_steadfast

import android.app.admin.DeviceAdminReceiver

class DeviceAdminBroadcastReceiver : DeviceAdminReceiver()

val HomeActivity.isAdminActive
    get() = isAdminActive(
        DeviceAdminBroadcastReceiver::class.java
    )

val LockerService.isAdminActive
    get() = isAdminActive(
        DeviceAdminBroadcastReceiver::class.java
    )
