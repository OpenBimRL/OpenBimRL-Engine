package de.rub.bi.inf.openbimrl.functions.geometry

import arrow.core.Either
import com.github.ajalt.colormath.model.RGB
import de.rub.bi.inf.extensions.*
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.DisplayableFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.addPaddingToObstacles
import de.rub.bi.inf.openbimrl.utils.math.neighbors
import de.rub.bi.inf.openbimrl.utils.pathfinding.*
import io.github.offlinebrain.khexagon.algorythm.aStar
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.*
import javax.media.j3d.BoundingBox
import javax.media.j3d.BoundingSphere
import javax.vecmath.Point3d
import javax.vecmath.Vector2d
import kotlin.math.max


/**
 * Performs the shortest path search based on distances from the start node to all reachable end nodes in a graph.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
@OpenBIMRLFunction
class CalculateAStarSearch(nodeProxy: NodeProxy) : DisplayableFunction(nodeProxy) {

    @FunctionInput(0, IfcPointer::class)
    lateinit var starts: List<IfcPointer>

    @FunctionInput(1, IfcPointer::class)
    lateinit var ends: List<IfcPointer>

    @FunctionInput(2, BoundingBox::class)
    lateinit var bBox: List<BoundingBox>

    @FunctionInput(6, nullable = false)
    lateinit var layout: Layout

    @FunctionOutput(0, Point3d::class)
    var path: List<Point3d>? = null


    /**
     * start: [IfcPointer], end: [IfcPointer], bounds: [BoundingBox], obstacles: [List], layout [Layout]
     */
    override fun execute() {
        val starts = this.starts.map { it.polygon.value }
        val ends = this.ends.map { it.polygon.value }
        val bounds = bBox.getOrNull(0)?.toRect()
        val passages = geometryFromPointers(getInputAsCollection(4))
        val obstaclePadding = getInput<Any?>(5)?.toString()?.toDouble() ?: 0.0
        val obstacles = addPaddingToObstacles(geometryFromPointers(getInputAsCollection(3)), obstaclePadding)

        if (bounds == null) return

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

        if (paths.size == 0) return

        val path = paths.reduce { acc: List<HexCoordinates>, hexCoordinates: List<HexCoordinates> ->
            val newList = ArrayList<HexCoordinates>(acc.size + hexCoordinates.size)
            newList.addAll(acc)
            newList.addAll(hexCoordinates)
            return@reduce newList
        }

        this.path = path.map { hexToPixel(layout, it).toPoint3d() }

        this.logGraphically(path.let {
            it.map { key ->
                Pair(
                    // can safely assert that bBox[0] is not null due to check at the start of execute
                    BoundingSphere(hexToPixel(layout, key).toPoint3d(max(bBox[0].lower().y, bBox[0].upper().y)), .25),
                    mapOf("color" to Either.Right(RGB.GREEN()))
                )
            }
        })
    }
}
