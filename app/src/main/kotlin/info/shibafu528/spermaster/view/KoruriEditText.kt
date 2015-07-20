package info.shibafu528.spermaster.view

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import info.shibafu528.spermaster.util.TypefaceManager

/**
 * Koruriフォントをデフォルトで使用するEditText
 *
 * Created by shibafu on 15/07/05.
 */
public class KoruriEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        val fontFamily: String? = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "fontFamily")
        if (fontFamily != null && !fontFamily.contains("light")) {
            setTypeface(TypefaceManager.getTypeface(getContext(), TypefaceManager.AssetTypeface.KORURI))
        } else {
            setTypeface(TypefaceManager.getTypeface(getContext(), TypefaceManager.AssetTypeface.KORURI_LIGHT))
        }
    }

}
