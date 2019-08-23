package io.github.achmadhafid.ten_minutes_steadfast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jetradar.desertplaceholder.desertPlaceHolderAction
import io.github.achmadhafid.lottie_dialog.*
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.model.onClick
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
    lottieConfirmationDialog(baseDialog) {
        withAnimation(R.raw.dialog_illustration)
        withTitle(R.string.require_admin_permission_dialog_title)
        withContent(R.string.require_admin_permission_dialog_content)
        withPositiveButton {
            onClick {
                isOpeningAdminSetting = true
                openAdminSettings()
            }
        }
        withNegativeButton {
            textRes = android.R.string.cancel
            iconRes = R.drawable.ic_close_black_18dp
        }
    }
}

fun HomeActivity.showPermissionCompleteDialog() {
    lottieConfirmationDialog(baseDialog) {
        withTitle(R.string.require_admin_permission_complete_dialog_title)
        withContent(R.string.require_admin_permission_complete_dialog_content)
        withPositiveButton {
            textRes = R.string.yes
            onClick {
                startLockerService()
                finish()
            }
        }
        withNegativeButton {
            textRes     = R.string.later
            iconRes     = R.drawable.ic_close_black_18dp
            actionDelay = 200
            onClick { showRunLaterDialog() }
        }
        withCancelOption {
            onBackPressed  = false
        }
    }
}

fun HomeActivity.showRunLaterDialog() {
    lottieConfirmationDialog(baseDialog) {
        withTitle(R.string.run_later_dialog_title)
        withContent(R.string.run_later_dialog_content)
        onDismiss {
            finish()
        }
    }
}

private val baseDialog by lazy {
    lottieConfirmationDialogBuilder {
        type = LottieDialogType.BOTTOM_SHEET
        withPositiveButton {
            textRes     = android.R.string.ok
            iconRes     = R.drawable.ic_check_black_18dp
            actionDelay = 200L
        }
        withCancelOption {
            onBackPressed  = true
            onTouchOutside = false
        }
    }
}

//endregion
