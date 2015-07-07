package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import com.activeandroid.Model
import com.activeandroid.query.Select
import java.util.Date
import kotlin.properties.Delegates
import com.activeandroid.annotation.Column as column
import com.activeandroid.annotation.Table as table

/**
 * 射精記録1回分のデータモデル。
 *
 * Created by shibafu on 15/07/04.
 */
public table(name = "Ejaculations", id = BaseColumns._ID) class Ejaculation() : Model() {
    /** 射精の記録日時。 */
    column(name = "EjaculatedDate") var ejaculatedDate: Date = Date()

    /** ユーザが任意に利用できるフリーメモの内容。 */
    column(name = "Note") var note: String = ""

    val timeSpan: Long by Delegates.lazy {
        ejaculatedDate.getTime() - (before()?.ejaculatedDate?.getTime() ?: ejaculatedDate.getTime())
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

    fun tagMap() : List<TagMap> = getId()?.let { super.getMany(javaClass<TagMap>(), "EjaculationId") } ?: emptyList()

    /**
     * この記録に紐付けられているタグのリストを取得します。
     * @return 紐付いているタグ
     */
    fun tags() : List<Tag> = getId()?.let { super.getMany(javaClass<TagMap>(), "EjaculationId").map { it.tag!! } } ?: emptyList()

    /**
     * この記録の1つ手前の記録を取得します。
     * @return 直前の記録。存在しない場合は`null`。
     */
    fun before() : Ejaculation? =
            Select().from(javaClass<Ejaculation>())
                    .where("EjaculatedDate < ?", ejaculatedDate.getTime().toString())
                    .orderBy("EjaculatedDate desc")
                    .executeSingle()
}

/**
 * これを持つ[Ejaculation]がどのような理由によるものかを表します。
 * @param label 画面上での表示
 */
deprecated("Tagに置き換えるために作業が終わり次第削除されます。")
public enum class Means(val label: String) {
    /** 未設定。 */
    UNSET("Unset"),
    /** 自慰による射精。 */
    MASTURBATION("Solo"),
    /** パートナーを伴う性交による射精。 */
    SEX("Multi")
}