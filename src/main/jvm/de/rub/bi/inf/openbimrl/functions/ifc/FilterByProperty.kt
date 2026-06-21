package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(
    description = "Filters a list of IFC entities by their property definition. Returns all entities that contain the property information.",
)
class FilterByProperty(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "IfcElement List", collectionType = IfcPointer::class)
    lateinit var ifcElements: Collection<IfcPointer>

    @FunctionInput(1)
    lateinit var propertySetName: String

    @FunctionInput(2)
    lateinit var propertyName: String

    @FunctionInput(3, nullable = true)
    var value: String? = null

    @FunctionOutput(0, name = "IfcElement List", collectionType = IfcPointer::class)
    var result: List<IfcPointer>? = null

    override fun execute() {
        result = ifcElements.filter { element ->
            if (value == null) return@filter true
            if (!element.properties.containsKey(propertySetName)) return@filter false
            if (!element.properties[propertySetName]!!.containsKey(propertyName)) return@filter false

            val elementPropertyValue = element.properties[propertySetName]!![propertyName]!!
            compareValues(value!!, elementPropertyValue)
        }
    }

    private fun compareValues(first: String, second: String): Boolean {
        val collection1 = if (first.contains(';')) first.split(';') else listOf(first)
        val collection2 = if (second.contains(';')) second.split(';') else listOf(second)
        return collection1.any { it in collection2 }
    }
}
