package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(
    description = "Filters a list of IFC entities by their quantity value. Returns all entities that contain the quantity information.",
)
class FilterByQuantity(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "IfcElement List", collectionType = IfcPointer::class)
    lateinit var ifcElements: Collection<IfcPointer>

    @FunctionInput(1)
    lateinit var quantitySetName: String

    @FunctionInput(2)
    lateinit var quantityName: String

    @FunctionInput(3)
    lateinit var value: Any

    @FunctionOutput(0, name = "IfcElement List", collectionType = IfcPointer::class)
    var result: List<IfcPointer>? = null

    override fun execute() {
        val quantityValues = when (val input = value) {
            is Double -> listOf(input)
            is String -> input.split(';').map(String::toDouble)
            else -> listOf(input.toString().toDouble())
        }

        result = ifcElements.filter { element ->
            if (!element.quantities.containsKey(quantitySetName)) return@filter false
            if (!element.quantities[quantitySetName]!!.containsKey(quantityName)) return@filter false

            val elementQuantityValue = element.quantities[quantitySetName]!![quantityName]!!
            elementQuantityValue in quantityValues
        }
    }
}
