package de.rub.bi.inf.extensions

import java.awt.geom.Line2D
import java.awt.geom.Path2D
import java.awt.geom.PathIterator
import java.util.*

fun Path2D.Double.intersects(line: Line2D): Boolean {
    var x1 = Optional.empty<Double>()
    var y1 = Optional.empty<Double>()
    var x2 = Optional.empty<Double>()
    var y2 = Optional.empty<Double>()
    val pi = this.getPathIterator(null)
    while (!pi.isDone) {
        val coordinates = DoubleArray(6)
        when (pi.currentSegment(coordinates)) {
            PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO -> {
                if (x1.isEmpty && y1.isEmpty) {
                    x1 = Optional.of(coordinates[0])
                    y1 = Optional.of(coordinates[1])
                } else if (x2.isEmpty && y2.isEmpty) {
                    x2 = Optional.of(coordinates[0])
                    y2 = Optional.of(coordinates[1])
                }
            }
        }
        if (x1.isPresent && y1.isPresent && x2.isPresent && y2.isPresent) {
            val segment: Line2D = Line2D.Double(x1.get(), y1.get(), x2.get(), y2.get())
            if (segment.intersectsLine(line)) {
                return true
            }
            x1 = Optional.empty()
            y1 = Optional.empty()
            x2 = Optional.empty()
            y2 = Optional.empty()
        }
        pi.next()
    }
    return false
}