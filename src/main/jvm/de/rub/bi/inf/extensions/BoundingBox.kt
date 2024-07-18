package de.rub.bi.inf.extensions

import java.awt.geom.Path2D
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

fun BoundingBox.toRect(): Path2D {
    val rect = Path2D.Double()
    val lower = Point3d()
    val upper = Point3d()
    this.getLower(lower)
    this.getUpper(upper)
    rect.moveTo(lower.x, lower.z)
    rect.lineTo(lower.x, upper.z)
    rect.lineTo(upper.x, upper.z)
    rect.lineTo(upper.x, lower.z)
    rect.closePath()
    return rect
}