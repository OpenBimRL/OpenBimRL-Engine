package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Orientation
import io.github.offlinebrain.khexagon.math.Point
import javax.vecmath.Point3d

/**
 * @author Marcel Stepien (reworked by Florian Becker)
 */
@OpenBIMRLFunction(description = "Creates a pointy-top hexagon grid layout from a center point and cell size.")
class CreateHexagonLayout(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, Point3d::class)
    lateinit var center: Collection<Point3d>

    @FunctionInput(1, Double::class)
    lateinit var size: Collection<Double>

    @FunctionOutput(0)
    var layout: Layout? = null

    override fun execute() {
        val centerPoint = center.first()
        val cellSize = size.first().toFloat()
        layout = Layout(
            Orientation.Pointy,
            origin = Point(centerPoint.x.toFloat(), centerPoint.z.toFloat()),
            size = Point(cellSize, cellSize),
        )
    }
}
