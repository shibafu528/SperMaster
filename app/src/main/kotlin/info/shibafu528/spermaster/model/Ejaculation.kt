package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Select
import info.shibafu528.spermaster.util.MemoizeDelayed
import java.util.*

/**
 * 射精記録1回分のデータモデル。
 *
 * Created by shibafu on 15/07/04.
 */
public @Table(name = "Ejaculations", id = BaseColumns._ID) class Ejaculation() : Model() {
    /** 射精の記録日時。 */
    @Column(name = "EjaculatedDate") public var ejaculatedDate: Date = Date()

    /** ユーザが任意に利用できるフリーメモの内容。 */
    @Column(name = "Note") public var note: String = ""

    public val timeSpan: Long by MemoizeDelayed(0) {
        ejaculatedDate.time - (before()?.ejaculatedDate?.time ?: ejaculatedDate.time)
    }

    /**
     * 新規の射精記録を作成します。
     * @param ejaculatedDate 記録日時
     * @param note メモ
     */
    constructor(ejaculatedDate: Date, note: String = "") : this() {
        this.ejaculatedDate = ejaculatedDate
        this.note = note
    }

    public fun tagMap() : List<TagMap> = id?.let { super.getMany(TagMap::class.java, "EjaculationId") } ?: emptyList()

    /**
     * この記録に紐付けられているタグのリストを取得します。
     * @return 紐付いているタグ
     */
    public fun tags() : List<Tag> = id?.let { super.getMany(TagMap::class.java, "EjaculationId").map { it.tag!! } } ?: emptyList()

    /**
     * この記録の1つ手前の記録を取得します。
     * @return 直前の記録。存在しない場合は`null`。
     */
    public fun before() : Ejaculation? =
            Select().from(Ejaculation::class.java)
                    .where("EjaculatedDate < ?", ejaculatedDate.time.toString())
                    .orderBy("EjaculatedDate desc")
                    .executeSingle()
}
