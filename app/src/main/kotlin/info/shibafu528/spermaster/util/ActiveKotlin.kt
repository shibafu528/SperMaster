package info.shibafu528.spermaster.util

import com.activeandroid.Model
import kotlin.properties.ReadOnlyProperty

public fun <T : Model> Model.hasMany(foreignKey: String) : ReadOnlyProperty<Model, List<T>> = HasMany(foreignKey)

private class HasMany<T : Model>(val foreignKey: String) : ReadOnlyProperty<Model, List<T>>, Model() {
    var modelType: Class<T>? = null

    override fun get(thisRef: Model, desc: PropertyMetadata): List<T> {
        @suppress("UNCHECKED_CAST")
        if (modelType == null) {
            modelType = thisRef.javaClass.getDeclaredField(desc.name).getType() as Class<T>
        }

        return thisRef.getMany(modelType, foreignKey)
    }
}