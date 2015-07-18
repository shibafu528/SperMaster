package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import android.support.v4.util.SparseArrayCompat
import com.activeandroid.Model
import com.activeandroid.query.Select
import info.shibafu528.spermaster.util.hourOfDay
import info.shibafu528.spermaster.util.minute
import info.shibafu528.spermaster.util.toCalendar
import kotlin.properties.Delegates
import com.activeandroid.annotation.Column as column
import com.activeandroid.annotation.Table as table

/**
 * 実績の解禁状況を記録します。
 *
 * Created by shibafu on 15/07/18.
 */
public table(name = "Achievements", id = BaseColumns._ID) class Achievement() : Model() {
    /** 実績定義を参照するためのキー。 */
    column(name = "Key", notNull = true, unique = true)
    public var key: Int = -1

    /** 実績の名称。 */
    public val name: String
        get() = achievements.get(key).name

    /** 実績の説明。 */
    public val description: String
        get() = achievements.get(key).description

    /** 実績解除の条件。 */
    public val unlockCondition: (Ejaculation) -> Boolean
        get() = achievements.get(key).unlockCondition

    companion object {
        private val achievements = SparseArrayCompat<AchievementDefinition>()

        init {
            defineAchievements()
        }

        /**
         * 実績定義を作成します。
         * @param key データベース上に記録され、記録と定義を紐つける一意の値を指定します。
         * @param name 実績の名称。画面上に表示されます。
         * @param description 実績の説明。画面上に表示されます。
         * @param unlockCondition 実績解除の条件。記録の更新が発生した時、まだ獲得していない実績である場合に評価されます。
         */
        internal fun create(key: Int, name: String, description: String,
                            unlockCondition: (Ejaculation) -> Boolean) {
            achievements.put(key, AchievementDefinition(name, description, unlockCondition))
        }
    }

    /**
     * 実績定義の保持用データクラスです。
     */
    private data class AchievementDefinition(val name: String,
                                             val description: String,
                                             val unlockCondition: (Ejaculation) -> Boolean)
}

/**
 * 実績の定義と解禁条件の設定を行います。
 */
private fun defineAchievements() {
    Achievement.create(0, "シコのケービィ", "伝説。") {
        Select().from(javaClass<Ejaculation>())
                .where("EjaculatedDate >= ?", System.currentTimeMillis() - 60480000)
                .count() >= 40
    }

    Achievement.create(1, "1日の区切りに性欲をシメる", "その日の欲はその日のうちに片付けるのが粋というもの。") {
        val calendar = it.ejaculatedDate.toCalendar()
        calendar.hourOfDay == 23 && calendar.minute >= 50
    }
}