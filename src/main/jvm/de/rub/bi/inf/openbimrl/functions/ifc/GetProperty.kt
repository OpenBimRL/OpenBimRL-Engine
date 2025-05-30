package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

/**
 * Retrieves the property value of a specific entity.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
@OpenBIMRLFunction
class GetProperty(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    override fun execute() {
        val ifcElements = getInputAsCollection(0)
        val propertySetName = getInput<String>(1)
        val propertyName = getInput<String>(2)

        val result = ifcElements.filterIsInstance<IfcPointer>().filter {
            if (!it.properties.containsKey(propertySetName)) return@filter false
            return@filter it.properties[propertySetName]!!.containsKey(propertyName)
        }.map {
            it.properties[propertySetName]!![propertyName]!!
        }

        setResult(0, result)
    }
}
