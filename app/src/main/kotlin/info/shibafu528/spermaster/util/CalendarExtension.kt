package info.shibafu528.spermaster.util

import java.util.*

public var Calendar.year: Int
    get() = this.get(Calendar.YEAR)
    set(v) = this.set(Calendar.YEAR, v)

public var Calendar.month: Int
    get() = this.get(Calendar.MONTH)
    set(v) = this.set(Calendar.MONTH, v)

public var Calendar.day: Int
    get() = this.get(Calendar.DAY_OF_MONTH)
    set(v) = this.set(Calendar.DAY_OF_MONTH, v)

public var Calendar.hourOfDay: Int
    get() = this.get(Calendar.HOUR_OF_DAY)
    set(v) = this.set(Calendar.HOUR_OF_DAY, v)

public var Calendar.minute: Int
    get() = this.get(Calendar.MINUTE)
    set(v) = this.set(Calendar.MINUTE, v)
