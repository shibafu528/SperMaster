package info.shibafu528.spermaster.util

import android.content.Context
import android.graphics.Typeface
import android.support.v4.util.SparseArrayCompat

/**
 * Created by shibafu on 15/07/20.
 */
public final class TypefaceManager {
    companion object {
        private val typefaces = SparseArrayCompat<Typeface>()

        public fun getTypeface(context: Context, asset: AssetTypeface): Typeface {
            var typeface = typefaces.get(asset.ordinal())
            if (typeface == null) {
                typeface = Typeface.createFromAsset(context.getAssets(), asset.fileName)
                typefaces[asset.ordinal()] = typeface
            }
            return typeface
        }
    }

    public enum class AssetTypeface(val fileName: String) {
        KORURI("Koruri-Regular.ttf"),
        KORURI_LIGHT("Koruri-Light.ttf")
    }
}