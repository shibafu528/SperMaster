package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import com.activeandroid.Model
import java.util.*
import com.activeandroid.annotation.Table as table
import com.activeandroid.annotation.Column as column

/**
 * Created by shibafu on 15/07/04.
 */
public table(name = "Ejaculations", id = BaseColumns._ID) class Ejaculation() : Model() {
    column(name = "EjaculatedDate") var ejaculatedDate: Date = Date()
    //TODO: 1:nタグ式に変えるのでcolumnとしては不要になる
    column(name = "Means") var means: Means = Means.UNSET
    column(name = "Note") var note: String = ""

    constructor(ejaculatedDate: Date, means: Means = Means.UNSET, note: String = "") : this() {
        this.ejaculatedDate = ejaculatedDate
        this.means = means
        this.note = note
    }
}

public enum class Means(val label: String) {
    UNSET("Unset"),
    MASTURBATION("Solo"),
    SEX("Multi")
}