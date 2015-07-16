package info.shibafu528.spermaster.util

import com.squareup.otto.Bus
import info.shibafu528.spermaster.model.Tag

/**
 * アプリケーション上で共用するイベントバスです。
 *
 * Javaであればシングルトンクラス内で宣言するようなものですが、面倒くさいのでこうしています。
 */
public val EventBus: Bus = Bus()

/**
 * ユーザによるタグ選択が行われた時に発生します。
 * @param tag 選択されたタグ
 */
public data class SelectedTagFilterEvent(val tag: Tag)

/**
 * ユーザによるタグ選択が解除された時に発生します。
 */
public class UnselectedTagFilterEvent()