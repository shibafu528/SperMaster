package info.shibafu528.spermaster.util

import com.activeandroid.serializer.TypeSerializer
import info.shibafu528.spermaster.model.Means
import java.util.Date

/**
 * Created by shibafu on 15/07/05.
 */
public final class DateSerializer : TypeSerializer() {
    override fun getDeserializedType(): Class<*>? = javaClass<Date>()
    override fun getSerializedType(): Class<*>? = javaClass<Long>()
    override fun serialize(data: Any?): Any? = (data as Date).getTime()
    override fun deserialize(data: Any?): Any? = Date(data as Long)
}
public final class MeansSerializer : TypeSerializer() {
    override fun getDeserializedType(): Class<*>? = javaClass<Means>()
    override fun getSerializedType(): Class<*>? = javaClass<Int>()
    override fun serialize(data: Any?): Any? = (data as Means).ordinal()
    override fun deserialize(data: Any?): Any? = Means.values().get(data as Int)
}