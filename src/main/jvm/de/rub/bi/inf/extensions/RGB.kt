package de.rub.bi.inf.extensions

import com.github.ajalt.colormath.model.RGB

fun RGB.Companion.RED(): String {
    return this.fromHex("F00")
}

fun RGB.Companion.GREEN(): String {
    return this.fromHex("0F0")
}

fun RGB.Companion.BLUE(): String {
    return this.fromHex("00F")
}

private fun RGB.Companion.fromHex(hex: String): String {
    return RGB(hex).toHex()
}