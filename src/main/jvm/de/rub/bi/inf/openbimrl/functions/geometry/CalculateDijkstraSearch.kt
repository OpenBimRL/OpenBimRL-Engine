package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.toPoint3d
import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.InvalidFunctionInputException
import de.rub.bi.inf.openbimrl.utils.addPaddingToObstacles
import de.rub.bi.inf.openbimrl.utils.math.neighbors
import de.rub.bi.inf.openbimrl.utils.pathfinding.*
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.hexToPixel
import io.github.offlinebrain.khexagon.math.pixelToHex
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import kotlin.math.max

@OpenBIMRLFunction(name = "calculateDistancesFromElement")
class CalculateDijkstraSearch(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, IfcPointer::class)
    lateinit var start: Collection<IfcPointer>

    @FunctionInput(1)
    lateinit var buildingBoundingBox: BoundingBox

    @FunctionInput(2, IfcPointer::class)
    lateinit var obstacles: Collection<IfcPointer>

    @FunctionInput(3, IfcPointer::class)
    lateinit var passageWays: Collection<IfcPointer>

    @FunctionInput(4)
    lateinit var layout: Layout

    @FunctionInput(5, nullable = true)
    var obstaclePadding: String? = null

    @FunctionOutput(0, name = "Points", collectionType = Point3d::class)
    var points: List<Point3d>? = null

    @FunctionOutput(1, name = "Distances", collectionType = Double::class)
    var distances: List<Double>? = null

    override fun execute() {
        val startGeometry = geometryFromPointers(start)
        val bounds = buildingBoundingBox.toRect()
        val passages = geometryFromPointers(passageWays)
        val obstaclePaddingDouble = this.obstaclePadding.let {
            try {
                return@let it?.toDouble() ?: 0.0
            } catch (e: NumberFormatException) {
                throw InvalidFunctionInputException("Error in function ${this.javaClass.getAnnotation(OpenBIMRLFunction::class.java).name}! Could not convert $it to double.")
            }
        }
        val obstacles = addPaddingToObstacles(geometryFromPointers(this.obstacles), obstaclePaddingDouble)

        if (startGeometry.isEmpty()) return
        val startHexCoordinates = startGeometry.map { point ->
            pixelToHex(layout, point.bounds2D.let { Point(it.x.toFloat(), it.y.toFloat()) }).hexRound()
        }

        clearGeometryBuffer()
        fillGeometryBuffer(arrayOf(*passages.toTypedArray(), *obstacles.toTypedArray()))

        val walkable = isWalkable(layout, bounds, obstacles, passages)
        val movementCost = if (obstaclePaddingDouble == 0.0) {
            movementCostNative(
                layout = layout,
                starts = startHexCoordinates,
                isWalkable = walkable,
                obstaclePointers = this.obstacles,
                passagePointers = passageWays,
            )
        } else {
            movementCost(layout, obstacles, passages)
        }

        val path = dijkstra<HexCoordinates>(
            from = startHexCoordinates,
            neighbors = ::neighbors,
            isWalkable = walkable,
            distance = movementCost,
        )

        val elevationY = max(buildingBoundingBox.lower().y, buildingBoundingBox.upper().y)
        val pointList = ArrayList<Point3d>(path.size)
        val distanceList = ArrayList<Double>(path.size)
        path.forEach { (hex, distance) ->
            pointList.add(hexToPixel(layout, hex).toPoint3d(elevationY))
            distanceList.add(distance)
        }
        points = pointList
        distances = distanceList
    }
}
