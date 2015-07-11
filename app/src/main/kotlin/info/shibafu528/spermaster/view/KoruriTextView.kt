package info.shibafu528.spermaster.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.EditText
import android.widget.TextView

/**
 * Koruriフォントをデフォルトで使用するTextView
 *
 * Created by shibafu on 15/07/05.
 */
public class KoruriTextView : TextView {

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
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "Koruri-Regular.ttf")
        }
        if (typefaceLight == null) {
            typefaceLight = Typeface.createFromAsset(getContext().getAssets(), "Koruri-Light.ttf")
        }
        val fontFamily: String? = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "fontFamily")
        if (fontFamily != null && !fontFamily.contains("light")) {
            setTypeface(typeface)
        } else {
            setTypeface(typefaceLight)
        }
    }

    companion object {
        private var typeface: Typeface? = null
        private var typefaceLight: Typeface? = null
    }
}
