package info.shibafu528.spermaster.util

import java.util.*

/**
 * Created by shibafu on 15/07/12.
 */

public fun Date.toCalendar() : Calendar {
    val cal = Calendar.getInstance()
    cal.setTime(this)
    return cal
}