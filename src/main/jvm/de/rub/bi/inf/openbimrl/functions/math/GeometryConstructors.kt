package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.utils.math.Straight
import javax.vecmath.Point3d
import javax.vecmath.Vector3d

@OpenBIMRLFunction(
    name = "createStraight",
    description = "Creates a straight line from a point and a direction vector.",
)
class CreateStraight(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    @FunctionInput(0)
    lateinit var point: Point3d

    @FunctionInput(1)
    lateinit var direction: Vector3d

    @FunctionOutput(0)
    var straight: Straight? = null

    override fun execute() {
        straight = Straight(Point3d(point), Vector3d(direction))
    }
}

@OpenBIMRLFunction(
    name = "createPlane",
    description = "Creates a plane from a point on the plane and two non-parallel in-plane axis vectors.",
)
class CreatePlane(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    @FunctionInput(0)
    lateinit var point: Point3d

    @FunctionInput(1)
    lateinit var axisU: Vector3d

    @FunctionInput(2)
    lateinit var axisV: Vector3d

    @FunctionOutput(0)
    var plane: Plane? = null

    override fun execute() {
        plane = Plane(Point3d(point), Vector3d(axisU), Vector3d(axisV))
    }
}
