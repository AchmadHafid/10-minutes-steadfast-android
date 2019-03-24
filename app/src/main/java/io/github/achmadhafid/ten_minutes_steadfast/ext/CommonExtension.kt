package io.github.achmadhafid.ten_minutes_steadfast.ext

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes

//region Logging

fun Any.log(message: String) {
    Log.d(this.javaClass.name, message)
}

//endregion
//region Text View

fun TextView.setTextOrGone(textString: CharSequence?, @StringRes textResource: Int?) {
    text = textString ?: context.getString(textResource ?: run {
        visibility = View.GONE
        return
    })
}

//endregion
