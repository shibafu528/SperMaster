package info.shibafu528.spermaster.util

import android.app.Activity
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Toast

/**
 * Created by shibafu on 15/07/05.
 */

public fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(getApplicationContext(), message, duration).show()
}

public fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(getActivity().getApplicationContext(), message, duration).show()
}

public fun Activity.putDebugLog(message: Any) {
    Log.d("Log", message.toString())
}

public fun Fragment.putDebugLog(message: Any) {
    Log.d("Log", message.toString())
}