package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.nativelib.NativeEngine
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromDoubles
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

@OpenBIMRLFunction(
    description = "Returns the building's bounding box.",
    outputs = [
        FunctionPort(0, "Bounds", BoundingBox::class),
        FunctionPort(1, "CenterPoints", Point3d::class),
    ],
)
class CalculateBuildingBounds(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    @FunctionOutput(0)
    var bounds: BoundingBox? = null

    @FunctionOutput(1)
    var centerPoint: Point3d? = null

    override fun executeNative() {
        val values = NativeEngine.calculatingBuildingBounds() ?: return
        val (center, bbox) = boundingBoxFromDoubles(values)
        bounds = bbox
        centerPoint = center
    }
}
