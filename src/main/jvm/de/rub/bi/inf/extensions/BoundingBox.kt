package de.rub.bi.inf.extensions

import java.awt.geom.Path2D
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import javax.vecmath.Tuple2d

fun BoundingBox.lower() = Point3d().apply { this@lower.getLower(this) }
fun BoundingBox.upper() = Point3d().apply { this@upper.getUpper(this) }


fun BoundingBox.toRect(): Path2D {
    val rect = Path2D.Double()
    val upper = upper()
    val lower = lower()
    rect.moveTo(lower.x, lower.z)
    rect.lineTo(lower.x, upper.z)
    rect.lineTo(upper.x, upper.z)
    rect.lineTo(upper.x, lower.z)
    //rect.closePath()
    return rect
}

fun BoundingBox.center(): Point3d = Point3d().apply { this.interpolate(this@center.lower(), this@center.upper(), .5) }