package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import java.util.stream.Collectors

/**
 * Filters a list of IFC entities by their quantity value. Returns all entities that contain the quantity information.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
@OpenBIMRLFunction
class FilterByQuantity(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    override fun execute() {
        val ifcPointer = getInputAsCollection(0)
        val quantitySetName = getInput<String>(1)
        val quantityName = getInput<String>(2)
        val quantityValue = let {
            val temp = getInput<Any>(3)
            if (temp is Double) return@let listOf(temp)
            if (temp is String)
            // works even if there is no ';'
                return@let temp.split(';').map(String::toDouble)

            return@let listOf(temp.toString().toDouble())
        }


        val result = ifcPointer.filterIsInstance<IfcPointer>().parallelStream().filter {
            if (!it.quantities.containsKey(quantitySetName)) return@filter false

            // !! is ok due to prev check
            if (!it.quantities[quantitySetName]!!.containsKey(quantityName)) return@filter false

            // !! is ok due to prev check
            val itQuantityValue = it.quantities[quantitySetName]!![quantityName]!!

            return@filter (itQuantityValue in quantityValue)
        }.collect(Collectors.toList())

        setResult(0, result)
    }

}
