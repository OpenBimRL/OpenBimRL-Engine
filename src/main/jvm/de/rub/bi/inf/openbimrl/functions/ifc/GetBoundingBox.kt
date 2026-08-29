package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.nativelib.NativeEngine
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromDoubles
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

@OpenBIMRLFunction(
    description = "Retrieves the BoundingBox of an IFC element.",
    inputs = [
        FunctionPort(0, "IfcElement", IfcPointer::class),
    ],
    outputs = [
        FunctionPort(0, "Bounds", BoundingBox::class),
        FunctionPort(1, "CenterPoints", Point3d::class),
    ],
)
class GetBoundingBox(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    override fun executeNative() {
        when (val input = getInput<Any>(0)) {
            is IfcPointer -> applyBoundingBox(input)
            is Collection<*> -> {
                val elements = input.filterIsInstance<IfcPointer>()
                val centers = ArrayList<Point3d>(elements.size)
                val boxes = ArrayList<BoundingBox>(elements.size)
                elements.forEach { element ->
                    val bounds = NativeEngine.getBoundingBox(element.handle) ?: return@forEach
                    val (center, box) = boundingBoxFromDoubles(bounds)
                    centers.add(center)
                    boxes.add(box)
                }
                setResult(0, boxes)
                setResult(1, centers)
            }
        }
    }

    private fun applyBoundingBox(element: IfcPointer) {
        val bounds = NativeEngine.getBoundingBox(element.handle) ?: return
        val (center, box) = boundingBoxFromDoubles(bounds)
        setResult(0, box)
        setResult(1, center)
    }
}
