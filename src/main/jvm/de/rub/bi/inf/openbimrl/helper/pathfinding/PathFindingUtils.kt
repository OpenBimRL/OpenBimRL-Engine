package de.rub.bi.inf.openbimrl.helper.pathfinding

import de.rub.bi.inf.extensions.intersects
import de.rub.bi.inf.extensions.toPoint2D
import de.rub.bi.inf.nativelib.IfcPointer
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.hexToPixel
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import java.awt.geom.Point2D

fun filterObstacles(collection: MutableCollection<*>): List<Path2D.Double> = collection
        .filterIsInstance<IfcPointer>()
        .map { it.polygon.value }
        .filter { it.isPresent }
        .map { it.get() }


// check if point is out of bounds or inside obstacle
fun isWalkable(
    layout: Layout,
    bounds: Path2D,
    obstacles: List<Path2D.Double>
): (HexCoordinates) -> Boolean {
    return { point ->
        val coordinates = hexToPixel(layout, point).let {
            Point2D.Float(it.x, it.y)
        }

        bounds.contains(coordinates) // point is out of bounds
                // point is in wall
                && !obstacles.parallelStream().map { it.contains(coordinates) }.reduce(false, Boolean::or)
    }
}

fun movementCost(
    layout: Layout,
    obstacles: List<Path2D.Double>
): (HexCoordinates, HexCoordinates) -> Double {
    return lambda@{ a, b ->
        if (a == b) return@lambda .0
        val realCoordinatesA = hexToPixel(layout, a).toPoint2D()
        val realCoordinatesB = hexToPixel(layout, b).toPoint2D()
        val line = Line2D.Float(realCoordinatesA, realCoordinatesB)

        // This is probably a horrible idea, but has that ever stopped me?
        // this checks if the path would pass through a wall
        if (obstacles.parallelStream().map { it.intersects(line) }.reduce(false, Boolean::or))
            return@lambda Double.POSITIVE_INFINITY
        else
            return@lambda realCoordinatesA.distance(realCoordinatesB)
    }
}