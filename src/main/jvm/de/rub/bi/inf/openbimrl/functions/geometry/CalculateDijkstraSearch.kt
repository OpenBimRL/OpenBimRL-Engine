package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.helper.neighbors
import de.rub.bi.inf.openbimrl.helper.pathfinding.filterObstacles
import de.rub.bi.inf.openbimrl.helper.pathfinding.isWalkable
import de.rub.bi.inf.openbimrl.helper.pathfinding.movementCost
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.pixelToHex
import java.util.*
import javax.media.j3d.BoundingBox

class CalculateDijkstraSearch(nodeProxy: NodeProxy?) : AbstractFunction(nodeProxy) {
    /**
     * start: [IfcPointer], end: [IfcPointer], bounds: [BoundingBox], obstacles: [List], layout [Layout]
     */
    override fun execute() {
        val start = getInputAsCollection(0)?.filterIsInstance<IfcPointer>()?.get(0)?.polygon?.value
        val bounds = getInputAsCollection(2)?.filterIsInstance<BoundingBox>()?.get(0)?.toRect()
        val obstacles = filterObstacles(getInputAsCollection(3))
        val layout = getInput<Layout>(4)

        if (start?.isEmpty == true || bounds == null || layout == null) return
        val startHexCoordinate =
            pixelToHex(layout, start!!.get().bounds2D.let { Point(it.x.toFloat(), it.y.toFloat()) }).hexRound()

        dijkstra<HexCoordinates>(
            from = startHexCoordinate,
            neighbors = ::neighbors,
            isWalkable = isWalkable(layout, bounds, obstacles),
            distance = movementCost(layout, obstacles)
        )
    }

    private fun <T> dijkstra(from: T, neighbors: (T) -> List<T>, isWalkable: (T) -> Boolean, distance: (T, T) -> Double): Map<T, Double> {
        val distances = mutableMapOf<T, Double>().withDefault { Double.POSITIVE_INFINITY }
        val priorityQueue = PriorityQueue<Pair<T, Double>>(compareBy { it.second })
        val visited = mutableSetOf<Pair<T, Double>>()

        priorityQueue.add(from to .0)
        distances[from] = .0

        while (priorityQueue.isNotEmpty()) {
            val  (node, currentDistance) = priorityQueue.poll()
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