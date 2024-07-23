package de.rub.bi.inf.openbimrl.functions.filter

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction

/**
 * Inverts the result of a filter. Basically, performing a boolean flip on all values of a list.
 *
 * @author Marcel Stepien
 */
class FilterInvert(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    override fun execute() {
        val input0 = getInput<Any>(0) ?: return

        var filterMap = LinkedHashMap<Any, List<Boolean>>()

        if (input0 is ArrayList<*>) {
            val mask = input0.filterIsInstance<Boolean>()
            filterMap["0"] = mask
        }

        if (input0 is LinkedHashMap<*, *>) {
            // dangerous cast!
            filterMap = input0 as LinkedHashMap<Any, List<Boolean>>
        }

        val resultValues = LinkedHashMap<Any, ArrayList<*>>()

        for (maskKey in filterMap.keys) {
            val mask = filterMap[maskKey] ?: continue

            val filteredList = ArrayList<Any?>()
            for (i in mask.indices) {
                val flag = mask[i]
                filteredList.add(!flag)
            }

            resultValues[maskKey] = filteredList
        }

        if (resultValues.keys.size == 1) {
            val firstKey = resultValues.keys.toTypedArray()[0]
            setResult(0, resultValues[firstKey])
        } else {
            setResult(0, resultValues)
        }
    }
}
