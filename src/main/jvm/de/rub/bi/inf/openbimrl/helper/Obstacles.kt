package de.rub.bi.inf.openbimrl.helper

import java.awt.geom.Path2D
import java.awt.geom.Point2D
import javax.vecmath.Vector2d
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

fun addPaddingToObstacles(obstacles: List<Path2D.Double>, padding: Double): List<Path2D.Double> {
    if (padding == 0.0) return obstacles
    return obstacles.map {
        val newShape = Path2D.Double()
        val center = it.bounds.let { b -> Point2D.Double(b.centerX, b.centerY) }
        val up = Vector2d(0.0, 1.0)
        val pi = it.getPathIterator(null)
        val coordinates = DoubleArray(6)
        var first = true
        while (!pi.isDone) {
            val p = pi.let { pathIterator ->
                pathIterator.currentSegment(coordinates)
                Point2D.Double(coordinates[0], coordinates[1])
            }
            val cpVector = Vector2d(p.x - center.x, p.y - center.y)
            val angle = up.angle(cpVector)
            val signVal = sign(cpVector.x)
            val xShift = sin(angle) * padding * signVal
            val yShift = cos(angle) * padding
            if (first) {
                first = false
                newShape.moveTo(p.x + xShift, p.y + yShift)
            } else {
                newShape.lineTo(p.x + xShift, p.y + yShift)
            }
            pi.next()
        }
        return@map newShape
    }
}
