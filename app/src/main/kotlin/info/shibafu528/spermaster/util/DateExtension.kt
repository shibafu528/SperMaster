package info.shibafu528.spermaster.util

import java.util.*

/**
 * Created by shibafu on 15/07/12.
 */

public fun Date.toCalendar() : Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

fun Long.toDateString(): String {
    val day = this / 86400000;
    val time = this % 86400000;
    val hour = time / 3600000;
    val minute = time % 3600000 / 60000;
    return "${day}日 ${hour}時間 ${minute}分"
}