package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import com.activeandroid.Model
import kotlin.reflect.KClass
import com.activeandroid.annotation.Column as column
import com.activeandroid.annotation.Table as table

/**
 * [Ejaculation]に0..n個紐付けることの出来るタグのデータモデル。
 *
 * Created by shibafu on 15/07/05.
 */
public table(name = "Tags", id = BaseColumns._ID) class Tag() : Model() {
    /** 表示名。しかし、ユーザ入力とのマッチングにもそのまま使われます。 */
    column(name = "Name") var name: String = ""

    /**
     * 新規のタグを作成します。
     * @param name タグの表示名
     */
    constructor(name: String) : this() {
        this.name = name
    }

    companion object {
        /**
         * ユーザ入力のタグをパースし、DBに問い合わせて既存データであればそれを取得、存在しない場合は新規作成します。
         * 保存と更新は行いません。
         * @param inputTags ユーザによって入力されたタグ。`"tag1, tag2, tag3"`のような記法を期待し処理します。
         * @return タグデータ
         */
        public fun parseInputTags(inputTags: String) : List<Tag> {
            throw UnsupportedOperationException()
        }
    }
}

public table(name = "TagMap", id = BaseColumns._ID) class TagMap() : Model() {
    column(name = "EjaculationId", notNull = true, index = true) var ejaculation: Ejaculation? = null
    column(name = "TagId", notNull = true, index = true) var tag: Tag? = null
}
