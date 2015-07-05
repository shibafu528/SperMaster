package info.shibafu528.spermaster.util

import android.app.Activity
import android.widget.Toast

/**
 * Created by shibafu on 15/07/05.
 */

public fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(getApplicationContext(), message, duration).show()
}