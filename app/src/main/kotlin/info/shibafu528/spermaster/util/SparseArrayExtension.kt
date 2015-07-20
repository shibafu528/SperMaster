package info.shibafu528.spermaster.util

import android.support.v4.util.SparseArrayCompat

/**
 * Created by shibafu on 15/07/19.
 */
public fun <T> SparseArrayCompat<T>.forEach(operation: (Int, T) -> Unit) {
    for (i in 0..size()-1) {
        operation(keyAt(i), valueAt(i))
    }
}

public fun <T, R> SparseArrayCompat<T>.map(operation: (Int, T) -> R): List<R> {
    var mappedValues = emptyList<R>()
    for (i in 0..size()-1) {
        mappedValues += operation(keyAt(i), valueAt(i))
    }
    return mappedValues
}

public fun <T> SparseArrayCompat<T>.set(key: Int, value: T) {
    this.put(key, value)
}