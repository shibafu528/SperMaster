package info.shibafu528.spermaster.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

/**
 * Created by shibafu on 14/02/18.
 */
public class SimpleAlertDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    public interface OnDialogChoseListener {
        public fun onDialogChose(requestCode: Int, extendCode: Long, which: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = arguments

        val builder = AlertDialog.Builder(activity)
        args.getInt(PARAM_ICON, -1).let { if (it > -1 ) builder.setIcon(it) }
        args.getString(PARAM_TITLE)?.let { builder.setTitle(it) }
        args.getString(PARAM_MESSAGE)?.let { builder.setMessage(it) }
        args.getString(PARAM_POSITIVE)?.let { builder.setPositiveButton(it, this) }
        args.getString(PARAM_NEUTRAL)?.let { builder.setNeutralButton(it, this) }
        args.getString(PARAM_NEGATIVE)?.let { builder.setNegativeButton(it, this) }

        return builder.create()
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        dismiss()

        val listener: OnDialogChoseListener? = when {
            parentFragment is OnDialogChoseListener -> parentFragment as OnDialogChoseListener
            targetFragment is OnDialogChoseListener -> targetFragment as OnDialogChoseListener
            activity is OnDialogChoseListener -> activity as OnDialogChoseListener
            else -> null
        }

        arguments?.let {
            listener?.onDialogChose(
                    it.getInt(PARAM_REQUEST_CODE),
                    it.getLong(PARAM_EXTEND_CODE),
                    i)
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
        onClick(dialog!!, DialogInterface.BUTTON_NEGATIVE)
    }

    companion object {

        private val PARAM_REQUEST_CODE: String = "requestcode"
        private val PARAM_EXTEND_CODE: String = "extendcode"
        private val PARAM_ICON: String = "icon"
        private val PARAM_TITLE: String = "title"
        private val PARAM_MESSAGE: String = "message"
        private val PARAM_POSITIVE: String = "positive"
        private val PARAM_NEUTRAL: String = "neutral"
        private val PARAM_NEGATIVE: String = "negative"

        public fun newInstance(requestCode: Int = 0,
                               extendCode: Long = 0,
                               iconId: Int? = null,
                               title: String? = null,
                               message: String? = null,
                               positive: String? = null,
                               neutral: String? = null,
                               negative: String? = null): SimpleAlertDialogFragment {
            val fragment = SimpleAlertDialogFragment()
            val args = Bundle()
            args.putInt(PARAM_REQUEST_CODE, requestCode)
            args.putLong(PARAM_EXTEND_CODE, extendCode)
            iconId?.let { args.putInt(PARAM_ICON, it) }
            title?.let { args.putString(PARAM_TITLE, it) }
            message?.let { args.putString(PARAM_MESSAGE, it) }
            positive?.let { args.putString(PARAM_POSITIVE, it) }
            neutral?.let { args.putString(PARAM_NEUTRAL, it) }
            negative?.let { args.putString(PARAM_NEGATIVE, it) }
            fragment.arguments = args
            return fragment
        }
    }
}
