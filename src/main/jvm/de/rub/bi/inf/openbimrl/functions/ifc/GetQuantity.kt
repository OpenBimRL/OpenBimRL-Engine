package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Retrieves the quantity value of a specific entity.")
class GetQuantity(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "IfcElement", collectionType = IfcPointer::class)
    lateinit var ifcElements: Collection<IfcPointer>

    @FunctionInput(1)
    lateinit var quantitySetName: String

    @FunctionInput(2)
    lateinit var quantityName: String

    @FunctionOutput(0)
    var value: List<Double>? = null

    override fun execute() {
        value = ifcElements.filter { element ->
            element.quantities.containsKey(quantitySetName) &&
                element.quantities[quantitySetName]!!.containsKey(quantityName)
        }.map { element ->
            element.quantities[quantitySetName]!![quantityName]!!
        }
    }
}
