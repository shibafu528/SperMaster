package info.shibafu528.spermaster.model

import android.provider.BaseColumns
import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.activeandroid.annotation.Table
import com.activeandroid.query.Select

/**
 * [Ejaculation]に0..n個紐付けることの出来るタグのデータモデル。
 *
 * Created by shibafu on 15/07/05.
 */
public @Table(name = "Tags", id = BaseColumns._ID) class Tag() : Model() {
    /** 表示名。しかし、ユーザ入力とのマッチングにもそのまま使われます。 */
    @Column(name = "Name") public var name: String = ""

    /**
     * 新規のタグを作成します。
     * @param name タグの表示名
     */
    constructor(name: String) : this() {
        this.name = name
    }

    override fun toString(): String {
        return name
    }

    companion object {
        /**
         * ユーザ入力のタグをパースし、DBに問い合わせて既存データであればそれを取得、存在しない場合は新規作成します。
         * 保存と更新は行いません。
         * @param inputTags ユーザによって入力されたタグ。`"tag1, tag2, tag3"`のような記法を期待し処理します。
         * @return タグデータ
         */
        public fun parseInputTags(inputTags: String) : List<Tag> =
            inputTags.split(',' , ';', '、')
                     .map { it.trim() }
                     .filterNot { "".equals(it) }
                     .map { Select().from(Tag::class.java)
                                    .where("Name = ?", it)
                                    .executeSingle()         ?: Tag(it) }
    }
}

public @Table(name = "TagMap", id = BaseColumns._ID) class TagMap() : Model() {
    @Column(name = "EjaculationId", notNull = true, index = true)
    public var ejaculation: Ejaculation? = null

    @Column(name = "TagId", notNull = true, index = true)
    public var tag: Tag? = null

    constructor(ejaculation: Ejaculation, tag: Tag) : this() {
        this.ejaculation = ejaculation
        this.tag = tag
    }
}
