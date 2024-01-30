package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction

/**
 * Filters a list of IFC entities by their property definition. Returns all entities that contain the property information.
 *
 * @author Marcel Stepien
 */
class FilterByProperty(nodeProxy: NodeProxy?) : NativeFunction(nodeProxy) {
    override fun executeNative() {
        val ifcPointer = getInputAsCollection(0)
        val propertySetName = getInput<String>(1)
        val propertyName = getInput<String>(2)
        val propertyValue = getInput<String?>(3)

        println("check for $propertyName=$propertyValue in $propertySetName")

        val result = ifcPointer.filter {
            if (it !is IfcPointer) return@filter false
            if (propertyValue == null) return@filter true

            if (!it.properties.containsKey(propertySetName)) return@filter false

            // !! is ok due to prev check
            if (!it.properties[propertySetName]!!.containsKey(propertyName)) return@filter false

            val itPropertyValue = it.properties[propertySetName]?.get(propertyName)
            if (itPropertyValue == null) return@filter false

            return@filter compareValues(propertyValue, itPropertyValue)
        }

        setResult(0, result)
    }

    private fun compareValues(val1: String, val2: String): Boolean {
        val collection1 = if (val1.contains(';')) val1.split(';') else listOf(val1)
        val collection2 = if (val2.contains(';')) val2.split(';') else listOf(val2)

        return collection1.any { it in collection2 }
    }
}
