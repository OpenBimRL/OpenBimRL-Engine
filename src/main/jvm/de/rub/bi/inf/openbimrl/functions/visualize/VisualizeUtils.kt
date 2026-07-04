package de.rub.bi.inf.openbimrl.functions.visualize

import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.SRGB

internal fun parseOptionalDouble(value: String?, default: Double): Double =
    value?.trim()?.toDoubleOrNull()?.takeIf { it > 0.0 } ?: default

internal fun parseColor(value: String?, fallback: RGB): RGB {
    if (value.isNullOrBlank()) return fallback
    val trimmed = value.trim()
    return try {
        if (trimmed.startsWith("#")) {
            RGB(trimmed)
        } else {
            RGB("#$trimmed")
        }
    } catch (_: Exception) {
        try {
            val intVal = trimmed.toInt()
            val r = (intVal shr 16) and 0xFF
            val g = (intVal shr 8) and 0xFF
            val b = intVal and 0xFF
            SRGB.from255(r, g, b, 255)
        } catch (_: Exception) {
            fallback
        }
    }
}
