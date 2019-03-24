package io.github.achmadhafid.ten_minutes_steadfast.ui.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import io.github.achmadhafid.ten_minutes_steadfast.ext.setTextOrGone
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hannesdorfmann.fragmentargs.FragmentArgs
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import io.github.achmadhafid.ten_minutes_steadfast.R
import kotlinx.android.synthetic.main.fragemnt_confirm_dialog.*

@FragmentWithArgs
class ConfirmDialogFragment : BottomSheetDialogFragment() {

    //region Properties

    var listener: Listener? = null

    @Arg
    var id: String? = null
    @Arg(required = false)
    var title: CharSequence? = null
    @Arg(required = false)
    var description: CharSequence? = null
    @Arg(required = false)
    var positiveButton: CharSequence? = null
    @Arg(required = false)
    var negativeButton: CharSequence? = null

    @Arg(required = false) @StringRes
    var titleResource: Int? = null
    @Arg(required = false) @StringRes
    var descriptionResource: Int? = null
    @Arg(required = false) @StringRes
    var positiveButtonResource: Int? = null
    @Arg(required = false) @StringRes
    var negativeButtonResource: Int? = null

    //endregion

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FragmentArgs.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragemnt_confirm_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textView_title.setTextOrGone(title, titleResource)
        textView_desc.setTextOrGone(description, descriptionResource)
        button_positive.setTextOrGone(positiveButton, positiveButtonResource)
        button_negative.setTextOrGone(negativeButton, negativeButtonResource)

        button_positive.setOnClickListener {
            dismiss()
            listener?.onPositive(id!!) }
        button_negative.setOnClickListener {
            dismiss()
            listener?.onNegative(id!!)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.onCancel(id!!, dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismiss(id!!, dialog)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface Listener {
        fun onPositive(id: String)
        fun onNegative(id: String)
        fun onCancel(id: String, dialog: DialogInterface) {}
        fun onDismiss(id: String, dialog: DialogInterface) {}
    }

}
