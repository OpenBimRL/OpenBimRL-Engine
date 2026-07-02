package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromMemory
import java.util.*
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

/**
 * returns the Buildings bounding box
 * @author Florian Becker
 */
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

    override fun executeNative() = nativeLib.calculatingBuildingBounds()

    override fun handleMemory(memoryQueue: Queue<MemoryStructure>) {
        val (center, bbox) = boundingBoxFromMemory(memoryQueue.remove())
        bounds = bbox
        centerPoint = center
    }
}
