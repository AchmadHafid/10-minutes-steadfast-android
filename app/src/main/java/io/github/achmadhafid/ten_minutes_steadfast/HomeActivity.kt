package io.github.achmadhafid.ten_minutes_steadfast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jetradar.desertplaceholder.desertPlaceHolderAction
import io.github.achmadhafid.lottie_dialog.*
import io.github.achmadhafid.zpack.ktx.openAdminSettings

class HomeActivity : AppCompatActivity(R.layout.activity_home) {

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
        type = LottieDialog.Type.BOTTOM_SHEET
        animation(R.raw.dialog_illustration)
        title(R.string.require_admin_permission_dialog_title)
        content(R.string.require_admin_permission_dialog_content)
        positiveButton {
            textRes = android.R.string.ok
            iconRes = R.drawable.ic_check_black_18dp
            onClick {
                isOpeningAdminSetting = true
                openAdminSettings()
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
        type = LottieDialog.Type.BOTTOM_SHEET
        title(R.string.require_admin_permission_complete_dialog_title)
        content(R.string.require_admin_permission_complete_dialog_content)
        positiveButton {
            textRes = R.string.yes
            iconRes = R.drawable.ic_check_black_18dp
            onClick {
                startLockerService()
                finish()
            }
        }
        negativeButton {
            textRes = R.string.later
            iconRes = R.drawable.ic_close_black_18dp
            onClick { showRunLaterDialog() }
        }
        cancel {
            onBackPressed  = false
            onTouchOutside = false
        }
    }
}

fun HomeActivity.showRunLaterDialog() {
    lottieDialog {
        type = LottieDialog.Type.BOTTOM_SHEET
        title(R.string.run_later_dialog_title)
        content(R.string.run_later_dialog_content)
        positiveButton {
            textRes = android.R.string.ok
            iconRes = R.drawable.ic_check_black_18dp
        }
        cancel {
            onBackPressed  = true
            onTouchOutside = false
        }
        onDismiss {
            finish()
        }
    }
}

//endregion
