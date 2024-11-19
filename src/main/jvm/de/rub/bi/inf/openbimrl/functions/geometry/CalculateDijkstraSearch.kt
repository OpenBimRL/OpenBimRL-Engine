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
import de.rub.bi.inf.openbimrl.utils.addPaddingToObstacles
import de.rub.bi.inf.openbimrl.utils.math.neighbors
import de.rub.bi.inf.openbimrl.utils.pathfinding.*
import de.rub.bi.inf.openbimrl.utils.math.lerp
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.hexToPixel
import io.github.offlinebrain.khexagon.math.pixelToHex
import javax.media.j3d.BoundingBox
import javax.media.j3d.BoundingSphere
import kotlin.math.*

class CalculateDijkstraSearch(nodeProxy: NodeProxy) : DisplayableFunction(nodeProxy) {
    /**
     * start: [IfcPointer], bounds: [BoundingBox], obstacles: [List], passage ways [List], layout [Layout],
     * maxDistance (optional) [Double]
     */
    override fun execute() {

        val start = geometryFromPointers(getInputAsCollection(0))
        val bBox = getInputAsCollection(1).filterIsInstance<BoundingBox>().getOrNull(0)
        val bounds = bBox?.toRect()
        val passages = geometryFromPointers(getInputAsCollection(3))
        val layout = getInput<Layout>(4)
        val obstaclePadding = getInput<Any?>(5)?.toString()?.toDouble() ?: 0.0
        val maxDistance = getInput<Any?>(6)?.toString()?.toDouble() ?: 100.0

        val obstacles = addPaddingToObstacles(geometryFromPointers(getInputAsCollection(2)), obstaclePadding)

        if (start.isEmpty() || bounds == null || layout == null) return
        val startHexCoordinates = start.map { point ->
            pixelToHex(layout, point.bounds2D.let { Point(it.x.toFloat(), it.y.toFloat()) }).hexRound()
        }

        clearGeometryBuffer()
        fillGeometryBuffer(arrayOf(*passages.toTypedArray(), *obstacles.toTypedArray()))

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
                    ).toSRGB()).toHex()
                Pair(
                    BoundingSphere(hexToPixel(layout, key).toPoint3d(max(bBox.lower().y, bBox.upper().y)), .25),
                    mapOf("color" to Either.Right(color))
                )
            }
        })
    }
}
