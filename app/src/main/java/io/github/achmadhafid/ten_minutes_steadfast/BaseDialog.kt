package io.github.achmadhafid.ten_minutes_steadfast

import io.github.achmadhafid.lottie_dialog.core.lottieConfirmationDialogBuilder
import io.github.achmadhafid.lottie_dialog.core.withCancelOption
import io.github.achmadhafid.lottie_dialog.core.withPositiveButton
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType

inline val baseDialog
    get() = lottieConfirmationDialogBuilder {
        type = LottieDialogType.BOTTOM_SHEET
        withPositiveButton {
            textRes     = android.R.string.ok
            iconRes     = R.drawable.ic_done_24dp
            actionDelay = 200L
        }
        withCancelOption {
            onBackPressed  = true
            onTouchOutside = false
        }
    }
