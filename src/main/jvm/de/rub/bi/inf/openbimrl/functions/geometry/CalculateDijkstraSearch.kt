package de.rub.bi.inf.openbimrl.functions.geometry

import arrow.core.Either
import de.rub.bi.inf.utils.math.lerp
import de.rub.bi.inf.extensions.toPoint3d
import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.DisplayableFunction
import de.rub.bi.inf.openbimrl.helper.neighbors
import de.rub.bi.inf.openbimrl.helper.pathfinding.filterObstacles
import de.rub.bi.inf.openbimrl.helper.pathfinding.isWalkable
import de.rub.bi.inf.openbimrl.helper.pathfinding.movementCost
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.hexToPixel
import io.github.offlinebrain.khexagon.math.pixelToHex
import java.util.*
import javax.media.j3d.BoundingBox
import javax.media.j3d.BoundingSphere

class CalculateDijkstraSearch(nodeProxy: NodeProxy?) : DisplayableFunction(nodeProxy) {
    /**
     * start: [IfcPointer], bounds: [BoundingBox], obstacles: [List], layout [Layout], maxDistance [Double]
     */
    override fun execute() {
        val start = getInputAsCollection(0)?.filterIsInstance<IfcPointer>()?.get(0)?.polygon?.value
        val bounds = getInputAsCollection(1)?.filterIsInstance<BoundingBox>()?.get(0)?.toRect()
        val obstacles = filterObstacles(getInputAsCollection(2))
        val layout = getInput<Layout>(3)
        val maxDistance = getInput<Double?>(4) ?: 100.0

        if (start?.isEmpty == true || bounds == null || layout == null) return
        val startHexCoordinate =
            pixelToHex(layout, start!!.get().bounds2D.let { Point(it.x.toFloat(), it.y.toFloat()) }).hexRound()

        val path = dijkstra<HexCoordinates>(
            from = startHexCoordinate,
            neighbors = ::neighbors,
            isWalkable = isWalkable(layout, bounds, obstacles),
            distance = movementCost(layout, obstacles)
        )

        if (logger.isEmpty) return
        logger.get().logGraphicalOutput(this.nodeProxy.node.id, path.let {
            it.keys.map { key ->
                Pair(
                    BoundingSphere(hexToPixel(layout, key).toPoint3d(), 1.0),
                    mapOf("color" to Either.Left(lerp(it[key]!!, .0, maxDistance, .0, 255.0 * 255.0).toInt()))
                )
            }
        })
    }

    private fun <T> dijkstra(
        from: T, neighbors: (T) -> List<T>, isWalkable: (T) -> Boolean, distance: (T, T) -> Double
    ): Map<T, Double> {
        val distances = mutableMapOf<T, Double>().withDefault { Double.POSITIVE_INFINITY }
        val priorityQueue = PriorityQueue<Pair<T, Double>>(compareBy { it.second })
        val visited = mutableSetOf<Pair<T, Double>>()

        priorityQueue.add(from to .0)
        distances[from] = .0

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDistance) = priorityQueue.poll()
            if (!visited.add(node to currentDistance)) continue

            neighbors(node).forEach { adjacent ->
                if (!isWalkable(adjacent)) return@forEach
                val totalDistance = currentDistance + distance(node, adjacent)
                if (totalDistance < distances.getValue(adjacent)) return@forEach
                distances[adjacent] = totalDistance
                priorityQueue.add(adjacent to totalDistance)
            }
        }

        return distances
    }
}
