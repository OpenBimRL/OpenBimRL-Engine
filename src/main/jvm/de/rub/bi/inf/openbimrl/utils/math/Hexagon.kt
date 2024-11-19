package de.rub.bi.inf.openbimrl.utils.math

import io.github.offlinebrain.khexagon.coordinates.HexCoordinates

// Hexagons are the Bestagons

fun neighbors(point: HexCoordinates): List<HexCoordinates> {
    return listOf(
        HexCoordinates(point.q - 1, point.r),
        HexCoordinates(point.q + 1, point.r),
        HexCoordinates(point.q, point.r - 1),
        HexCoordinates(point.q, point.r + 1),
        HexCoordinates(point.q - 1, point.r + 1),
        HexCoordinates(point.q + 1, point.r - 1),
    )
}