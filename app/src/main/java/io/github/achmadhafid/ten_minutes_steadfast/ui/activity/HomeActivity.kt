package io.github.achmadhafid.ten_minutes_steadfast.ui.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.achmadhafid.ten_minutes_steadfast.R
import io.github.achmadhafid.ten_minutes_steadfast.di.inject
import io.github.achmadhafid.ten_minutes_steadfast.receiver.DeviceAdminBroadcastReceiver
import io.github.achmadhafid.ten_minutes_steadfast.service.LockerService
import io.github.achmadhafid.ten_minutes_steadfast.ui.fragment.ConfirmDialogFragment
import io.github.achmadhafid.ten_minutes_steadfast.ui.fragment.ConfirmDialogFragmentBuilder
import io.github.achmadhafid.ten_minutes_steadfast.util.ContextUtil
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

private const val REQUEST_PERMISSION_DIALOG  = "request_permission_dialog"
private const val PERMISSION_COMPLETE_DIALOG = "permission_complete_dialog"
private const val RUN_LATER_DIALOG           = "run_later_dialog"

class HomeActivity : AppCompatActivity(), ConfirmDialogFragment.Listener {

    //region Properties

    @Inject
    lateinit var contextUtil: ContextUtil

    private var isOpeningAdminSetting = false

    private val requestPermissionDialog by lazy {
        ConfirmDialogFragmentBuilder(REQUEST_PERMISSION_DIALOG)
                .titleResource(R.string.require_admin_permission_dialog_title)
                .descriptionResource(R.string.require_admin_permission_dialog_content)
                .positiveButtonResource(android.R.string.ok)
                .build()
    }
    private val permissionCompleteDialog: ConfirmDialogFragment by lazy {
        ConfirmDialogFragmentBuilder(PERMISSION_COMPLETE_DIALOG)
                .titleResource(R.string.require_admin_permission_complete_dialog_title)
                .descriptionResource(R.string.require_admin_permission_complete_dialog_content)
                .positiveButtonResource(android.R.string.ok)
                .negativeButtonResource(R.string.later)
                .build()
    }
    private val runLaterDialog: ConfirmDialogFragment by lazy {
        ConfirmDialogFragmentBuilder(RUN_LATER_DIALOG)
                .titleResource(R.string.run_later_dialog_title)
                .descriptionResource(R.string.run_later_dialog_content)
                .positiveButtonResource(android.R.string.ok)
                .build()
    }

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        inject()

        homeView.apply {
            visibility = if (isAdminActive()) View.GONE else View.VISIBLE
            setOnButtonClickListener(View.OnClickListener {
                requestPermissionDialog.show(supportFragmentManager, null)
            })
        }
    }

    override fun onStart() {
        super.onStart()
        if (isAdminActive()) {
            if (isOpeningAdminSetting) {
                permissionCompleteDialog.show(supportFragmentManager, null)
            } else {
                startLockerService()
            }
        }
    }

    //endregion
    //region Private Helper

    private fun startLockerService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            requestPermissions(arrayOf(Manifest.permission.FOREGROUND_SERVICE), 1234)
        }
        LockerService.run(
                context = this,
                startTime = System.currentTimeMillis(),
                scanInterval = resources.getInteger(R.integer.scan_interval).toLong(),
                lockDuration = resources.getInteger(R.integer.lock_duration).toLong()
        )
        finish()
    }

    private fun isAdminActive() = contextUtil.isAdminActive(
        DeviceAdminBroadcastReceiver::class.java
    )

    //endregion
    //region Fragment Callback

    override fun onPositive(id: String) {
        when (id) {
            REQUEST_PERMISSION_DIALOG -> {
                startActivity(contextUtil.getAdminSettingIntent())
                isOpeningAdminSetting = true
            }
            PERMISSION_COMPLETE_DIALOG -> {
                startLockerService()
                finish()
            }
            RUN_LATER_DIALOG -> finish()
        }
    }

    override fun onNegative(id: String) {
        if (id == PERMISSION_COMPLETE_DIALOG) {
            runLaterDialog.show(supportFragmentManager, null)
        }
    }

    //endregion

}
