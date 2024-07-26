package de.rub.bi.inf.openbimrl.helper.pathfinding

import de.rub.bi.inf.extensions.intersects
import de.rub.bi.inf.extensions.toPoint2D
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.nativelib.ThreeDTester
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.hexToPixel
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import java.util.stream.Stream

fun geometryFromPointers(collection: Collection<*>): List<Path2D.Double> = collection
    .filterIsInstance<IfcPointer>()
    .map { it.polygon.value }
    .filter { it.isPresent }
    .map { it.get() }

private fun Stream<Boolean>.any(): Boolean = this.reduce(false, Boolean::or)


// check if point is out of bounds or inside obstacle
fun isWalkable(
    layout: Layout,
    bounds: Path2D,
    obstacles: List<Path2D.Double>,
    passages: List<Path2D.Double>
): (HexCoordinates) -> Boolean {
    return { point ->
        val coordinates = hexToPixel(layout, point).let {
            Point2D.Float(it.x, it.y)
        }

        bounds.contains(coordinates) && // is in bounds
                (passages.parallelStream().map { it.contains(coordinates) }.any() || // is in door
                        !obstacles.parallelStream().map { it.contains(coordinates) }.any()) // is not in wall
        // note to self: Order here may matter
    }
}

fun movementCost(
    layout: Layout,
    obstacles: List<Path2D.Double>,
    passages: List<Path2D.Double>
): (HexCoordinates, HexCoordinates) -> Double {
    return lambda@{ a, b ->
        if (a == b) return@lambda .0
        val realCoordinatesA = hexToPixel(layout, a).toPoint2D()
        val realCoordinatesB = hexToPixel(layout, b).toPoint2D()
        val line = Line2D.Float(realCoordinatesA, realCoordinatesB)

//        obstacles.forEach {
//            ThreeDTester.addShape(it)
//        }
//
//        ThreeDTester.addShape(line)

        // This is probably a horrible idea, but has that ever stopped me?
        // this checks if the path would pass through a wall
        val returnValue = if (passages.parallelStream().map { it.intersects(line) }.any())
            realCoordinatesA.distance(realCoordinatesB)
        else if (obstacles.parallelStream().map { it.intersects(line) }.any())
            Double.POSITIVE_INFINITY
        else
            realCoordinatesA.distance(realCoordinatesB)

//        obstacles.forEach {
//            ThreeDTester.removeShape(it)
//        }
//
//        ThreeDTester.removeShape(line)

        return@lambda returnValue
    }
}