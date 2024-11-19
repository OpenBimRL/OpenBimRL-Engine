package de.rub.bi.inf.openbimrl.utils.math

fun lerp(valueIn: Double, baseMin: Double, baseMax: Double, limitMin: Double, limitMax: Double) =
    ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin