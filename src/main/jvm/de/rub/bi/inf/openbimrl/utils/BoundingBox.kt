package de.rub.bi.inf.openbimrl.utils

import de.rub.bi.inf.openbimrl.functions.NativeFunction
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

fun boundingBoxFromMemory(memoryStructure: NativeFunction.MemoryStructure): Pair<Point3d, BoundingBox> {
    val coords = memoryStructure.memory.getDoubleArray(0, 6)
    val lower = Point3d(coords.sliceArray(0..2))
    val upper = Point3d(coords.sliceArray(3..5))

    val center = Point3d(lower)
    center.interpolate(upper, .5) // attention! Modifies final var center
    val bbox = BoundingBox(lower, upper)
    return Pair(center, bbox)
}