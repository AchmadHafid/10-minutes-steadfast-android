package io.github.achmadhafid.ten_minutes_steadfast

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

object PhoneListener : PhoneStateListener() {
    private var state: Int = TelephonyManager.CALL_STATE_IDLE
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        PhoneListener.state = state
    }
    fun isIdle() = state == TelephonyManager.CALL_STATE_IDLE
}
