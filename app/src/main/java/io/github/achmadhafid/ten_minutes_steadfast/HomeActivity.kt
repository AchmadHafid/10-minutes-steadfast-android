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
    lottieDialog(baseDialog) {
        animation(R.raw.dialog_illustration)
        title(R.string.require_admin_permission_dialog_title)
        content(R.string.require_admin_permission_dialog_content)
        positiveButton {
            onClick {
                isOpeningAdminSetting = true
                openAdminSettings()
            }
        }
        negativeButton {
            textRes = android.R.string.cancel
            iconRes = R.drawable.ic_close_black_18dp
        }
    }
}

fun HomeActivity.showPermissionCompleteDialog() {
    lottieDialog(baseDialog) {
        title(R.string.require_admin_permission_complete_dialog_title)
        content(R.string.require_admin_permission_complete_dialog_content)
        positiveButton {
            textRes = R.string.yes
            onClick {
                startLockerService()
                finish()
            }
        }
        negativeButton {
            textRes     = R.string.later
            iconRes     = R.drawable.ic_close_black_18dp
            actionDelay = 200
            onClick { showRunLaterDialog() }
        }
        cancel {
            onBackPressed  = false
        }
    }
}

fun HomeActivity.showRunLaterDialog() {
    lottieDialog(baseDialog) {
        title(R.string.run_later_dialog_title)
        content(R.string.run_later_dialog_content)
        onDismiss {
            finish()
        }
    }
}

private val baseDialog by lazy {
    lottieDialogBuilder {
        type = LottieDialog.Type.BOTTOM_SHEET
        positiveButton {
            textRes     = android.R.string.ok
            iconRes     = R.drawable.ic_check_black_18dp
            actionDelay = 200L
        }
        cancel {
            onBackPressed  = true
            onTouchOutside = false
        }
    }
}

//endregion
