package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import android.support.v4.util.SparseArrayCompat
import com.activeandroid.ActiveAndroid
import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Select
import info.shibafu528.spermaster.util.hourOfDay
import info.shibafu528.spermaster.util.map
import info.shibafu528.spermaster.util.minute
import info.shibafu528.spermaster.util.toCalendar
import java.util.*

/**
 * 実績の解禁状況を記録します。
 *
 * Created by shibafu on 15/07/18.
 */
public @Table(name = "Achievements", id = BaseColumns._ID) class Achievement() : Model() {
    /** 実績定義を参照するためのキー。 */
    @Column(name = "Key", notNull = true, unique = true)
    public var key: Int = -1

    /** 実績の解除日時 */
    @Column(name = "UnlockedDate") public var unlockedDate: Date = Date()

    /** 実績の名称。 */
    public val name: String
        get() = achievements.get(key).name

    /** 実績の説明。 */
    public val description: String
        get() = achievements.get(key).description

    /** 実績解除の条件。 */
    public val unlockCondition: (Ejaculation) -> Boolean
        get() = achievements.get(key).unlockCondition

    public constructor(key: Int) : this() {
        this.key = key
    }

    companion object {
        private val achievements = SparseArrayCompat<AchievementDefinition>()

        init {
            defineAchievements()
        }

        /**
         * 未解除の実績に対して、受け取った記録を用いてそれらの解除条件の評価を行います。
         * @param updatedRecord 新規登録または更新した射精記録
         */
        public fun unlock(updatedRecord: Ejaculation): List<AchievementDefinition> {
            //解除済み実績キー
            val unlockedKeys = Select().from(Achievement::class.java)
                                       .execute<Achievement>()
                                       .map { it.key }

            ActiveAndroid.beginTransaction()
            try {
                //解除に成功した実績を収集
                val unlocked = achievements.map { i, def ->
                    //解除済みか解除失敗ならボッシュート
                    if (unlockedKeys.contains(i) || !def.unlockCondition(updatedRecord))
                        return@map null

                    //解除条件を達成した場合はDBへの記録もする
                    Achievement(i).save()
                    return@map def
                }.filterNotNull()

                //結果をコミットする(ペーペケッペッペペーペーペペ♪)
                ActiveAndroid.setTransactionSuccessful()
                return unlocked
            } catch (e: Exception) {
                e.printStackTrace()
                return emptyList()
            } finally {
                ActiveAndroid.endTransaction()
            }
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
    public data class AchievementDefinition(val name: String,
                                             val description: String,
                                             val unlockCondition: (Ejaculation) -> Boolean)
}

/**
 * 実績の定義と解禁条件の設定を行います。
 */
private fun defineAchievements() {
    Achievement.create(0, "シコのケービィ", "伝説。") {
        Select().from(Ejaculation::class.java)
                .where("EjaculatedDate >= ?", System.currentTimeMillis() - 60480000) // 1 week
                .count() >= 40
    }

    Achievement.create(1, "1日の区切りに性欲をシメる", "その日の欲はその日のうちに片付けるのが粋というもの。") {
        val calendar = it.ejaculatedDate.toCalendar()
        calendar.hourOfDay == 23 && calendar.minute >= 50
    }

    Achievement.create(2, "射精で始まる1日", "気持ちの良いスタート。") {
        val calendar = it.ejaculatedDate.toCalendar()
        calendar.hourOfDay == 0 && calendar.minute <= 10
    }

    Achievement.create(3, "朝一番搾り", "朝に立ち上がる現象との関連性。") {
        it.ejaculatedDate.toCalendar().hourOfDay in 5..9
    }

    Achievement.create(4, "ハローワールド", "ここから始まる。") {
        true // 未獲得の実績が、記録追加・更新のタイミングで評価されるので必ず最初に解禁される
    }

    Achievement.create(5, "クイックリロード", "間髪を入れぬ素早い射精。") {
        it.timeSpan != 0L && it.timeSpan < 1800000L // 30 min
    }
}