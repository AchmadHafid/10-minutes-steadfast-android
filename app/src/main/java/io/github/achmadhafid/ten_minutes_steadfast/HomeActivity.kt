package io.github.achmadhafid.ten_minutes_steadfast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jetradar.desertplaceholder.desertPlaceHolderAction
import io.github.achmadhafid.lottie_dialog.core.lottieConfirmationDialog
import io.github.achmadhafid.lottie_dialog.core.onDismiss
import io.github.achmadhafid.lottie_dialog.core.withAnimation
import io.github.achmadhafid.lottie_dialog.core.withCancelOption
import io.github.achmadhafid.lottie_dialog.core.withContent
import io.github.achmadhafid.lottie_dialog.core.withNegativeButton
import io.github.achmadhafid.lottie_dialog.core.withPositiveButton
import io.github.achmadhafid.lottie_dialog.core.withTitle
import io.github.achmadhafid.lottie_dialog.model.onClick
import io.github.achmadhafid.zpack.extension.intRes
import io.github.achmadhafid.zpack.extension.openAdminSettings

class HomeActivity : AppCompatActivity(R.layout.activity_home) {

    //region Resource Binding

    private val scanInterval by intRes(R.integer.scan_interval)
    private val lockDuration by intRes(R.integer.lock_duration)

    //endregion
    //region Flag

    private var isOpeningAdminSetting = false

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        desertPlaceHolderAction(R.id.homeView) {
            if (isAdminActive) lock()
            else showRequestPermissionDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        if (isAdminActive) {
            if (isOpeningAdminSetting) showPermissionCompleteDialog()
            else lock()
        }
    }

    //endregion
    //region Utility Helper

    private fun lock() {
        startLockerService(
            scanInterval = scanInterval.toLong(),
            lockDuration = lockDuration.toLong()
        )
        finish()
    }

    //endregion
    //region Dialog Helper

    private fun showRequestPermissionDialog() {
        lottieConfirmationDialog(0, baseDialog) {
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
                iconRes = R.drawable.ic_close_24dp
            }
        }
    }

    private fun showPermissionCompleteDialog() {
        lottieConfirmationDialog(0, baseDialog) {
            withTitle(R.string.require_admin_permission_complete_dialog_title)
            withContent(R.string.require_admin_permission_complete_dialog_content)
            withPositiveButton {
                textRes = R.string.yes
                onClick {
                    lock()
                }
            }
            withNegativeButton {
                textRes     = R.string.later
                iconRes     = R.drawable.ic_close_24dp
                actionDelay = 200
                onClick { showRunLaterDialog() }
            }
            withCancelOption {
                onBackPressed  = false
            }
        }
    }

    private fun showRunLaterDialog() {
        lottieConfirmationDialog(0, baseDialog) {
            withTitle(R.string.run_later_dialog_title)
            withContent(R.string.run_later_dialog_content)
            onDismiss {
                finish()
            }
        }
    }

    //endregion

}
