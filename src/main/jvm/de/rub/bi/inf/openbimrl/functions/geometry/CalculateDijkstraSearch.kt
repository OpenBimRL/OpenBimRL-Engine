package de.rub.bi.inf.openbimrl.functions.geometry

import arrow.core.Either
import com.github.ajalt.colormath.model.Oklab
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.SRGB
import com.github.ajalt.colormath.transform.interpolator
import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.toPoint3d
import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.DisplayableFunction
import de.rub.bi.inf.openbimrl.helper.neighbors
import de.rub.bi.inf.openbimrl.helper.pathfinding.geometryFromPointers
import de.rub.bi.inf.openbimrl.helper.pathfinding.isWalkable
import de.rub.bi.inf.openbimrl.helper.pathfinding.movementCost
import de.rub.bi.inf.utils.math.lerp
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.hexToPixel
import io.github.offlinebrain.khexagon.math.pixelToHex
import java.util.*
import javax.media.j3d.BoundingBox
import javax.media.j3d.BoundingSphere
import kotlin.math.max
import kotlin.math.min

class CalculateDijkstraSearch(nodeProxy: NodeProxy) : DisplayableFunction(nodeProxy) {
    /**
     * start: [IfcPointer], bounds: [BoundingBox], obstacles: [List], passage ways [List], layout [Layout],
     * maxDistance (optional) [Double]
     */
    override fun execute() {

        val start = geometryFromPointers(getInputAsCollection(0))
        val bBox = getInputAsCollection(1).filterIsInstance<BoundingBox>().getOrNull(0)
        val bounds = bBox?.toRect()
        val obstacles = geometryFromPointers(getInputAsCollection(2))
        val passages = geometryFromPointers(getInputAsCollection(3))
        val layout = getInput<Layout>(4)
        val maxDistance = getInput<Any?>(5)?.toString()?.toDouble() ?: 100.0

        if (start.isEmpty() || bounds == null || layout == null) return
        val startHexCoordinates = start.map { point ->
            pixelToHex(
                layout,
                point.bounds2D.let { Point(it.x.toFloat(), it.y.toFloat()) }).hexRound()
        }

        val path = dijkstra<HexCoordinates>(
            from = startHexCoordinates,
            neighbors = ::neighbors,
            isWalkable = isWalkable(layout, bounds, obstacles, passages),
            distance = movementCost(layout, obstacles, passages)
        )

        val colorInterpolator = Oklab.interpolator {
            stop(RGB("0F0"))
            stop(RGB("FF0"))
            stop(RGB("F00"))
        }



        if (logger.isEmpty) return
        logger.get().logGraphicalOutput(this.nodeProxy.node.id, path.let {
            it.keys.map { key ->
                val interpolation = min(lerp(min(it[key]!!, maxDistance), .0, maxDistance, .0, 1.0), 1.0)
                val color =
                    (if (it[key]!! == Double.POSITIVE_INFINITY) SRGB(203, 203, 203) else colorInterpolator.interpolate(
                        interpolation
                    )
                        .toSRGB()).toHex()
                Pair(
                    BoundingSphere(hexToPixel(layout, key).toPoint3d(max(bBox.lower().y, bBox.upper().y)), .25),
                    mapOf("color" to Either.Right(color))
                )
            }
        })
    }

    private fun <T> dijkstra(
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
}
