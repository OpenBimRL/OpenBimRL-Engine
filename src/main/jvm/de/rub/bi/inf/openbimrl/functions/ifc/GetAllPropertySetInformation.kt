package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Retrieves the property value of a specific entity.")
class GetAllPropertySetInformation(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0)
    lateinit var ifcElement: IfcPointer

    @FunctionOutput(0, name = "Map of PropertyInfo")
    var propertySetInformation: Map<String, Map<String, String>>? = null

    override fun execute() {
        propertySetInformation = ifcElement.properties.toMap()
    }
}
