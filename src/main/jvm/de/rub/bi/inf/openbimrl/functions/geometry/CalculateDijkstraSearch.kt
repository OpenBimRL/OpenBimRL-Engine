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
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.InvalidFunctionInputException
import de.rub.bi.inf.openbimrl.utils.addPaddingToObstacles
import de.rub.bi.inf.openbimrl.utils.math.lerp
import de.rub.bi.inf.openbimrl.utils.math.neighbors
import de.rub.bi.inf.openbimrl.utils.pathfinding.*
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.hexToPixel
import io.github.offlinebrain.khexagon.math.pixelToHex
import javax.media.j3d.BoundingBox
import javax.media.j3d.BoundingSphere
import kotlin.math.max
import kotlin.math.min

@OpenBIMRLFunction(name = "calculateDistancesFromElement")
class CalculateDijkstraSearch(nodeProxy: NodeProxy) : DisplayableFunction(nodeProxy) {

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

    @FunctionInput(6, nullable = true)
    var maxDistance: String? = null

    /**
     * start: [IfcPointer], bounds: [BoundingBox], obstacles: [List], passage ways [List], layout [Layout],
     * maxDistance (optional) [Double]
     */
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
        val maxDistance = this.maxDistance.let {
            try {
                it?.toDouble() ?: 100.0
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

        logGraphically(path.let {
            it.keys.map { key ->
                val interpolation = min(lerp(min(it[key]!!, maxDistance), .0, maxDistance, .0, 1.0), 1.0)
                val color =
                    (if (it[key]!! == Double.POSITIVE_INFINITY) SRGB(203, 203, 203) else colorInterpolator.interpolate(
                        interpolation
                    ).toSRGB()).toHex()
                Pair(
                    BoundingSphere(
                        hexToPixel(layout, key).toPoint3d(
                            max(
                                buildingBoundingBox.lower().y, buildingBoundingBox.upper().y
                            )
                        ), .25
                    ), mapOf("color" to Either.Right(color))
                )
            }
        })
    }
}
