package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Retrieves the class type of element as String value.")
class GetIfcType(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0)
    lateinit var ifcElement: Any

    @FunctionOutput(0)
    var ifcType: Any? = null

    override fun execute() {
        val input = ifcElement
        ifcType = when (input) {
            is Collection<*> -> input.filterIsInstance<IfcPointer>().map { it.type }
            is IfcPointer -> input.type
            else -> null
        }
    }
}
