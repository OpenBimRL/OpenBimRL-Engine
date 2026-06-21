package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

@OpenBIMRLFunction(description = "Calculates the height given a list of entities.")
class GetHeight(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "IfcElement_List", collectionType = Any::class)
    lateinit var elements: Collection<Any>

    @FunctionOutput(0)
    var minHeight: Any? = null

    @FunctionOutput(1)
    var maxHeight: Any? = null

    override fun execute() {
        val lowerHeights = ArrayList<Double>()
        val upperHeights = ArrayList<Double>()

        elements.filterIsInstance<BoundingBox>().forEach { element ->
            lowerHeights.add(Point3d().apply { element.getLower(this) }.z)
            upperHeights.add(Point3d().apply { element.getUpper(this) }.z)
        }

        minHeight = if (lowerHeights.size == 1) lowerHeights[0] else lowerHeights
        maxHeight = if (upperHeights.size == 1) upperHeights[0] else upperHeights
    }
}
