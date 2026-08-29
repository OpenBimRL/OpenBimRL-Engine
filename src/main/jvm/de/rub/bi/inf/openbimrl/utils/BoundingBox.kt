package de.rub.bi.inf.openbimrl.utils

import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

fun boundingBoxFromDoubles(coords: DoubleArray): Pair<Point3d, BoundingBox> {
    require(coords.size >= 6) { "Bounding box requires 6 coordinates" }
    val lower = Point3d(coords[0], coords[1], coords[2])
    val upper = Point3d(coords[3], coords[4], coords[5])
    val center = Point3d(lower)
    center.interpolate(upper, .5)
    return Pair(center, BoundingBox(lower, upper))
}
