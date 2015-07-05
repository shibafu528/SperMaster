package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import com.activeandroid.Model
import com.activeandroid.query.Select
import info.shibafu528.spermaster.util.hasMany
import java.util.Date
import com.activeandroid.annotation.Column as column
import com.activeandroid.annotation.Table as table

/**
 * Created by shibafu on 15/07/04.
 */
public table(name = "Ejaculations", id = BaseColumns._ID) class Ejaculation() : Model() {
    column(name = "EjaculatedDate") var ejaculatedDate: Date = Date()
    column(name = "Note") var note: String = ""

    constructor(ejaculatedDate: Date, note: String = "") : this() {
        this.ejaculatedDate = ejaculatedDate
        this.note = note
    }

    fun tagMap() : List<TagMap> = getId()?.let { super.getMany(javaClass<TagMap>(), "EjaculationId") } ?: emptyList()

    fun tags() : List<Tag> = getId()?.let { super.getMany(javaClass<TagMap>(), "EjaculationId").map { it.tag!! } } ?: emptyList()
}

public enum class Means(val label: String) {
    UNSET("Unset"),
    MASTURBATION("Solo"),
    SEX("Multi")
}