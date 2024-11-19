package de.rub.bi.inf.openbimrl.utils.pathfinding

import de.rub.bi.inf.extensions.intersects
import de.rub.bi.inf.extensions.toPoint2D
import de.rub.bi.inf.nativelib.IfcPointer
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.hexToPixel
import java.awt.Rectangle
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import java.util.*
import java.util.stream.Stream

fun geometryFromPointers(collection: Collection<*>): List<Path2D.Double> =
    collection.filterIsInstance<IfcPointer>().map { it.polygon.value }.filter { it.isPresent }.map { it.get() }

private fun Stream<Boolean>.any(): Boolean = this.reduce(false, Boolean::or)

private val lookUpBuffer = mutableMapOf<Path2D.Double, Rectangle>()

public fun clearGeometryBuffer() = lookUpBuffer.clear()
public fun fillGeometryBuffer(stuff: Array<Path2D.Double>) {
    stuff.forEach { lookUpBuffer[it] = it.bounds }
}

private fun checkGeometryWithCache(geometry: List<Path2D.Double>, coordinates: Point2D.Float): Boolean {

    // if point is not in the bounding box of a passage way, it is not IN the passage way
    // !! is safe here due to previous call
    if (!geometry.parallelStream().map { lookUpBuffer[it]!!.contains(coordinates) }.any()) return false

    return geometry.parallelStream().map { it.contains(coordinates) }.any() // expensive geometric check
}

// check if point is out of bounds or inside obstacle
fun isWalkable(
    layout: Layout,
    bounds: Path2D,
    obstacles: List<Path2D.Double>,
    passages: List<Path2D.Double>,
    alwaysWalkable: Array<HexCoordinates> = emptyArray()
): (HexCoordinates) -> Boolean {
    return inner@{ point ->

        if (alwaysWalkable.contains(point)) return@inner true

        val coordinates = hexToPixel(layout, point).let {
            Point2D.Float(it.x, it.y)
        }

        if (!bounds.contains(coordinates)) return@inner false // point is not in the building

        // if point is in passage way, it's walkable
        if (checkGeometryWithCache(passages, coordinates)) return@inner true

        // if point is in obstacle, it's not
        return@inner !checkGeometryWithCache(obstacles, coordinates)
    }
}

fun movementCost(
    layout: Layout, obstacles: List<Path2D.Double>, passages: List<Path2D.Double>
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
        val returnValue =
            if (passages.parallelStream().map { it.intersects(line) }.any()) realCoordinatesA.distance(realCoordinatesB)
            else if (obstacles.parallelStream().map { it.intersects(line) }.any()) Double.POSITIVE_INFINITY
            else realCoordinatesA.distance(realCoordinatesB)

//        obstacles.forEach {
//            ThreeDTester.removeShape(it)
//        }
//
//        ThreeDTester.removeShape(line)

        return@lambda returnValue
    }
}

fun <T> dijkstra(
    from: List<T>, neighbors: (T) -> List<T>, isWalkable: (T) -> Boolean, distance: (T, T) -> Double
): Map<T, Double> {
    val distances = mutableMapOf<T, Double>().withDefault { Double.POSITIVE_INFINITY }
    val priorityQueue = PriorityQueue<Pair<T, Double>>(compareBy { it.second })
    val visited = mutableSetOf<Pair<T, Double>>()

    priorityQueue.addAll(from.map { it to .0 })
    from.forEach { distances[it] = .0 }

    while (priorityQueue.isNotEmpty()) {
        val (node, currentDistance) = priorityQueue.poll()
        if (!visited.add(node to currentDistance)) continue

        neighbors(node).forEach { adjacent ->
            if (!isWalkable(adjacent)) return@forEach
            val totalDistance = currentDistance + distance(node, adjacent)
            if (totalDistance > distances.getValue(adjacent)) return@forEach
            distances[adjacent] = totalDistance
            priorityQueue.add(adjacent to totalDistance)
        }
    }

    return distances
}