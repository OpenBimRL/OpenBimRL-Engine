package de.rub.bi.inf.openbimrl.functions.geometry

import arrow.core.Either
import com.github.ajalt.colormath.model.RGB
import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.toPoint3d
import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.DisplayableFunction
import de.rub.bi.inf.openbimrl.helper.addPaddingToObstacles
import de.rub.bi.inf.openbimrl.helper.neighbors
import de.rub.bi.inf.openbimrl.helper.pathfinding.*
import io.github.offlinebrain.khexagon.algorythm.aStar
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.*
import javax.media.j3d.BoundingBox
import javax.media.j3d.BoundingSphere
import kotlin.math.max


/**
 * Performs the shortest path search based on distances from the start node to all reachable end nodes in a graph.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
class CalculateAStarSearch(nodeProxy: NodeProxy) : DisplayableFunction(nodeProxy) {
    /**
     * start: [IfcPointer], end: [IfcPointer], bounds: [BoundingBox], obstacles: [List], layout [Layout]
     */
    override fun execute() {
        val starts = getInputAsCollection(0).filterIsInstance<IfcPointer>().map { it.polygon.value }
        val ends = getInputAsCollection(1).filterIsInstance<IfcPointer>().map { it.polygon.value }
        val bBox = getInputAsCollection(2).filterIsInstance<BoundingBox>().getOrNull(0)
        val bounds = bBox?.toRect()
        val passages = geometryFromPointers(getInputAsCollection(4))
        val obstaclePadding = getInput<Any?>(5)?.toString()?.toDouble() ?: 0.0
        val layout = getInput<Layout>(6)
        val obstacles = addPaddingToObstacles(geometryFromPointers(getInputAsCollection(3)), obstaclePadding)

        if (layout == null || bounds == null) return


        clearGeometryBuffer()
        fillGeometryBuffer(arrayOf(*passages.toTypedArray(), *obstacles.toTypedArray()))

        val paths = ArrayList<List<HexCoordinates>>(starts.size * ends.size)

        starts.forEach start@{ start ->
            ends.forEach end@{ end ->
                if (start.isEmpty) return@start
                if (end.isEmpty) return@end

                val startHexCoordinate =
                    pixelToHex(layout, start.get().bounds2D.let { Point(it.x.toFloat(), it.y.toFloat()) }).hexRound()
                val endHexCoordinates = pixelToHex(layout, end.get().bounds2D.let {
                    Point(it.x.toFloat(), it.y.toFloat())
                }).hexRound()
                val path = aStar(
                    from = startHexCoordinate,
                    to = endHexCoordinates,
                    neighbors = ::neighbors,
                    isWalkable = isWalkable(layout, bounds, obstacles, passages),
                    distance = HexCoordinates::distance,
                    movementCost = movementCost(layout, obstacles, passages)
                )

                paths.add(path)
            }
        }

        // setResult(0, paths)


        if (logger.isEmpty) return
        val path = paths.reduce { acc: List<HexCoordinates>, hexCoordinates: List<HexCoordinates> ->
            val newList = ArrayList<HexCoordinates>(acc.size + hexCoordinates.size)
            newList.addAll(acc)
            newList.addAll(hexCoordinates)
            return@reduce newList
        }

        logger.get().logGraphicalOutput(this.nodeProxy.node.id, path.let {
            it.map { key ->
                Pair(
                    BoundingSphere(hexToPixel(layout, key).toPoint3d(max(bBox.lower().y, bBox.upper().y)), .25),
                    mapOf("color" to Either.Right(RGB("0F0").toHex()))
                )
            }
        })
    }
}
