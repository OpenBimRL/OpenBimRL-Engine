package de.rub.bi.inf.extensions

import io.github.offlinebrain.khexagon.math.Point
import java.awt.geom.Point2D
import javax.vecmath.Point3d

fun Point.toPoint2D() = Point2D.Float(this.x, this.y)
fun Point.toPoint3d(elevation: Double = .0) = Point3d(this.x.toDouble(), elevation, this.y.toDouble())