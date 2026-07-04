package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.ifc.ElementApproximationService
import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.utils.math.Straight
import javax.vecmath.Point3d

@OpenBIMRLFunction(
    name = "convertToStraight",
    description = "Approximates an IFC element as a straight centerline for axis-based checks.",
)
class ConvertToStraight(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    @FunctionInput(0, name = "IfcElement")
    lateinit var element: IfcPointer

    @FunctionOutput(0, name = "Straight")
    var straight: Straight? = null

    @FunctionOutput(1, name = "Source")
    var source: String? = null

    override fun execute() {
        val result = ElementApproximationService.toStraight(element)
        straight = result.value
        source = result.source.name
    }
}

@OpenBIMRLFunction(
    name = "convertToPlane",
    description = "Approximates an IFC element as a plane for face-based parallelism checks.",
)
class ConvertToPlane(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    @FunctionInput(0, name = "IfcElement")
    lateinit var element: IfcPointer

    @FunctionOutput(0, name = "Plane")
    var plane: Plane? = null

    @FunctionOutput(1, name = "Source")
    var source: String? = null

    override fun execute() {
        val result = ElementApproximationService.toPlane(element)
        plane = result.value
        source = result.source.name
    }
}

@OpenBIMRLFunction(
    name = "convertToPoint",
    description = "Approximates an IFC element as a geometric reference point.",
)
class ConvertToPoint(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    @FunctionInput(0, name = "IfcElement")
    lateinit var element: IfcPointer

    @FunctionOutput(0, name = "Point")
    var point: Point3d? = null

    @FunctionOutput(1, name = "Source")
    var source: String? = null

    override fun execute() {
        val result = ElementApproximationService.toPoint(element)
        point = result.value
        source = result.source.name
    }
}
