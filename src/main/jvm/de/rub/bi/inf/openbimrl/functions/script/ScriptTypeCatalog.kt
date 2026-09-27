package de.rub.bi.inf.openbimrl.functions.script

import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.utils.math.Straight
import javax.vecmath.Point3d
import javax.vecmath.Vector3d
import kotlin.reflect.KClass
import kotlin.script.experimental.api.KotlinType

/**
 * Allowed mapper type names for Code nodes (aligned with CreatorTool / GET /script/types).
 */
object ScriptTypeCatalog {
    data class Entry(
        val name: String,
        val description: String,
        val kotlinClass: KClass<*>,
    )

    val entries: List<Entry> = listOf(
        Entry("Any", "Untyped / opaque graph value", Any::class),
        Entry("String", "Text", String::class),
        Entry("Double", "Floating-point number", Double::class),
        Entry("Boolean", "Boolean flag", Boolean::class),
        Entry("Point3d", "javax.vecmath.Point3d", Point3d::class),
        Entry("Vector3d", "javax.vecmath.Vector3d", Vector3d::class),
        Entry("Straight", "Infinite line (point + direction)", Straight::class),
        Entry("Plane", "Infinite plane (point + two axes)", Plane::class),
        Entry("Collection", "Untyped collection", Collection::class),
    )

    fun resolveClass(typeHint: String?): KClass<*> {
        if (typeHint.isNullOrBlank()) return Any::class
        return entries.firstOrNull { it.name.equals(typeHint, ignoreCase = true) }?.kotlinClass
            ?: Any::class
    }

    /**
     * Script provided-properties use erased/simple types for v1.
     * Collection ports bind as [Collection]; scripts cast element types as needed.
     */
    fun toKotlinType(port: ScriptPort): KotlinType {
        val kClass = when {
            port.isCollection -> Collection::class
            else -> resolveClass(port.typeHint)
        }
        return KotlinType(kClass)
    }
}
