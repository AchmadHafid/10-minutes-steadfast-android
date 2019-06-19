package io.github.achmadhafid.ten_minutes_steadfast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jetradar.desertplaceholder.desertPlaceHolderAction
import io.github.achmadhafid.lottie_dialog.*

class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    //region Resource Binding

    val shortDelay by lazy { resources.getInteger(R.integer.short_delay).toLong() }

    ///endregion
    //region Properties

    var isOpeningAdminSetting = false

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        desertPlaceHolderAction(R.id.homeView) {
            if (isAdminActive) {
                startLockerService()
            } else {
                showRequestPermissionDialog()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isAdminActive) {
            if (isOpeningAdminSetting) {
                showPermissionCompleteDialog()
            } else {
                startLockerService()
            }
        }
    }

    //endregion

}

//region Dialog Helper

fun HomeActivity.showRequestPermissionDialog() {
    lottieDialog {
        type = LottieDialog.Type.BottomSheet
        animation {
            lottieFileRes = R.raw.dialog_illustration
        }
        title {
            textRes = R.string.require_admin_permission_dialog_title
        }
        content {
            textRes = R.string.require_admin_permission_dialog_content
        }
        positiveButton {
            textRes = android.R.string.ok
            iconRes = R.drawable.ic_check_black_18dp
            onClick {
                isOpeningAdminSetting = true
                runDelayed(shortDelay) { startActivity(adminSettingIntent) }
            }
        }
        negativeButton {
            textRes = android.R.string.cancel
            iconRes = R.drawable.ic_close_black_18dp
        }
        cancel {
            onBackPressed  = true
            onTouchOutside = false
        }
    }
}

fun HomeActivity.showPermissionCompleteDialog() {
    lottieDialog {
        type = LottieDialog.Type.BottomSheet
        title {
            textRes = R.string.require_admin_permission_complete_dialog_title
        }
        content {
            textRes = R.string.require_admin_permission_complete_dialog_content
        }
        positiveButton {
            textRes = R.string.yes
            iconRes = R.drawable.ic_check_black_18dp
            onClick {
                runDelayed(shortDelay) {
                    startLockerService()
                    finish()
                }
            }
        }
        negativeButton {
            textRes = R.string.no
            iconRes = R.drawable.ic_close_black_18dp
            onClick { runDelayed(shortDelay) { showRunLaterDialog() } }
        }
        cancel {
            onBackPressed  = true
            onTouchOutside = false
        }
    }
}

fun HomeActivity.showRunLaterDialog() {
    lottieDialog {
        type = LottieDialog.Type.BottomSheet
        title {
            textRes = R.string.run_later_dialog_title
        }
        content {
            textRes = R.string.run_later_dialog_content
        }
        positiveButton {
            textRes = android.R.string.ok
            iconRes = R.drawable.ic_check_black_18dp
        }
        cancel {
            onBackPressed  = true
            onTouchOutside = false
        }
        listener {
            onDismiss { finish() }
        }
    }
}

//endregion
