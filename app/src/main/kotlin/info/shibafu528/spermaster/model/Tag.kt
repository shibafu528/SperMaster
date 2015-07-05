package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import com.activeandroid.Model
import kotlin.reflect.KClass
import com.activeandroid.annotation.Column as column
import com.activeandroid.annotation.Table as table

/**
 * Created by shibafu on 15/07/05.
 */
public table(name = "Tags", id = BaseColumns._ID) class Tag() : Model() {
    column(name = "Name") var name: String = ""

    constructor(name: String) : this() {
        this.name = name
    }
}

public table(name = "TagMap", id = BaseColumns._ID) class TagMap() : Model() {
    column(name = "EjaculationId", notNull = true, index = true) var ejaculation: Ejaculation? = null
    column(name = "TagId", notNull = true, index = true) var tag: Tag? = null
}
