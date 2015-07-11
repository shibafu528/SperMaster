package info.shibafu528.spermaster.util

import com.activeandroid.Model
import kotlin.properties.ReadOnlyProperty

/**
 * 強制的に記憶を消去できるメモ化委譲プロパティを作るクラス。
 *
 * Created by shibafu on 15/07/11.
 */
public class MemoizeDelayed<T>(val eval : () -> T) : ReadOnlyProperty<Model, T> {
    /** 最後に評価を実行した時刻 */
    private var lastEvaluated: Long = 0
    /** 最後に評価を行った結果の値 */
    private var evaluatedValue: T = null

    override fun get(thisRef: Model, desc: PropertyMetadata): T {
        // 最終評価時刻が、最終初期化時刻以前であれば再評価
        if (lastEvaluated < lastPurged) {
            lastEvaluated = System.currentTimeMillis()
            evaluatedValue = eval()
        }
        return evaluatedValue
    }

    companion object {
        /** 最後に初期化を行った時刻 */
        private var lastPurged: Long = System.currentTimeMillis()

        /**
         * このクラスを使用している委譲プロパティ全てに対して、次回参照時に再評価を要求します。
         */
        public fun purge() {
            lastPurged = System.currentTimeMillis()
        }
    }
}