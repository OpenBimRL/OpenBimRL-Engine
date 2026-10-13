package de.rub.bi.inf.openbimrl.functions.script

/**
 * Coerces graph edge values into script bindings so mapper type hints behave like
 * [de.rub.bi.inf.openbimrl.functions.AbstractFunction] field injection:
 * - [Double] ports: String/Number parse, collections flattened via filterIsInstance
 *   then try-parse of the first element if needed.
 */
object ScriptInputCoercion {

    fun coerce(value: Any?, port: ScriptPort): Any? {
        if (value == null) return null
        return when {
            port.isCollection -> coerceCollection(value, port)
            port.typeHint.equals("Double", ignoreCase = true) -> coerceToDouble(value)
            port.typeHint.equals("Boolean", ignoreCase = true) -> coerceToBoolean(value)
            port.typeHint.equals("String", ignoreCase = true) -> coerceToString(value)
            else -> value
        }
    }

    private fun coerceCollection(value: Any?, port: ScriptPort): Any? {
        val elements = when (value) {
            is Collection<*> -> value
            else -> listOf(value)
        }
        val elementHint = port.collectionType?.takeIf { it.isNotBlank() }
            ?: if (port.typeHint.equals("Collection", ignoreCase = true)) null else port.typeHint
        if (elementHint.equals("Double", ignoreCase = true)) {
            return elements.mapNotNull { coerceToDouble(it) }
        }
        return elements
    }

    /**
     * Scalar Double binding (mapper type "Double").
     *
     * Collection handling:
     * 1. [filterIsInstance]<[Double]> — take first if any
     * 2. else [filterIsInstance]<[Number]> — take first coerced
     * 3. else try-parse [Collection.firstOrNull]
     */
    fun coerceToDouble(value: Any?): Double? {
        if (value == null) return null
        when (value) {
            is Double -> return value
            is Number -> return value.toDouble()
            is String -> return parseDoubleString(value)
            is Collection<*> -> {
                val doubles = value.filterIsInstance<Double>()
                if (doubles.isNotEmpty()) return doubles.first()

                val numbers = value.filterIsInstance<Number>()
                if (numbers.isNotEmpty()) return numbers.first().toDouble()

                val first = value.firstOrNull() ?: return null
                return coerceToDouble(first)
            }
            else -> return parseDoubleString(value.toString())
        }
    }

    private fun coerceToBoolean(value: Any?): Boolean? {
        if (value == null) return null
        when (value) {
            is Boolean -> return value
            is Collection<*> -> return coerceToBoolean(value.firstOrNull())
            is String -> return value.equals("true", ignoreCase = true)
            is Number -> return value.toDouble() != 0.0
            else -> return null
        }
    }

    private fun coerceToString(value: Any?): String? {
        if (value == null) return null
        return when (value) {
            is String -> value
            is Collection<*> -> value.firstOrNull()?.toString()
            else -> value.toString()
        }
    }

    private fun parseDoubleString(raw: String): Double? {
        val normalized = raw.trim().replace(',', '.')
        if (normalized.isEmpty()) return null
        return normalized.toDoubleOrNull()
    }
}
