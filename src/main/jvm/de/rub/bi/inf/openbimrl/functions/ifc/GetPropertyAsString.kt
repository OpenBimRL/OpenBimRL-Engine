package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Retrieves the property value of a specific entity as String value.")
class GetPropertyAsString(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "IfcElement", collectionType = IfcPointer::class)
    lateinit var ifcElements: Collection<IfcPointer>

    @FunctionInput(1, name = "PropertySet_Name")
    lateinit var propertySetName: String

    @FunctionInput(2, name = "Property_Name")
    lateinit var propertyName: String

    @FunctionOutput(0)
    var value: Any? = null

    override fun execute() {
        val resolved = ifcElements.filter { element ->
            element.properties.containsKey(propertySetName) &&
                element.properties[propertySetName]!!.containsKey(propertyName)
        }.map { element ->
            element.properties[propertySetName]!![propertyName]!!
        }

        value = if (resolved.size == 1) resolved[0] else resolved
    }
}
