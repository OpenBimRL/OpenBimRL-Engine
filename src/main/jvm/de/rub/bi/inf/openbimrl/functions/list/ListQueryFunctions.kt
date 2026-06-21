package de.rub.bi.inf.openbimrl.functions.list

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.list.findIndex
import de.rub.bi.inf.openbimrl.utils.list.parseIntInput
import de.rub.bi.inf.openbimrl.utils.list.singleOrList

@OpenBIMRLFunction(description = "Retrieves an item of a specific index contained in a list of elements.")
class GetElementAt(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "List", collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionInput(1, name = "Index", collectionType = Any::class)
    lateinit var indices: Collection<Any>

    @FunctionOutput(0, name = "Item")
    var item: Any? = null

    override fun execute() {
        val listValues = list.toList()
        val elementsAt = indices.mapIndexed { index, positionInput ->
            val position = parseIntInput(positionInput)
            if (indices.size == 1) {
                listValues[position]
            } else {
                val values = listValues[index]
                if (values is List<*>) values[position] else values
            }
        }

        item = singleOrList(elementsAt)
    }
}

@OpenBIMRLFunction(description = "Retrieves an item index of a specific value contained in a list of elements.")
class GetItemIndexInList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "List", collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionInput(1, name = "Item", collectionType = Any::class)
    lateinit var items: Collection<Any>

    @FunctionOutput(0, name = "Index")
    var index: Any? = null

    override fun execute() {
        val listValues = ArrayList(list)
        val indices = items.map { findIndex(listValues, it) }
        index = singleOrList(indices)
    }
}

@OpenBIMRLFunction(description = "Checks if a list of elements are included in another set individually.")
class ElementIncludedInList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Elements", collectionType = Any::class)
    lateinit var candidates: Collection<Any>

    @FunctionInput(1, name = "InclusionList", collectionType = Any::class)
    lateinit var haystack: Collection<Any>

    @FunctionOutput(0, name = "ListOfChecks")
    var included: List<Boolean>? = null

    override fun execute() {
        included = candidates.map { haystack.contains(it) }
    }
}

@OpenBIMRLFunction(
    name = "allElementIncludedInList",
    description = "Checks if a list of elements are included in another set holistically.",
)
class AllElementsIncludedInList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Elements", collectionType = Any::class)
    lateinit var candidates: Collection<Any>

    @FunctionInput(1, name = "InclusionList", collectionType = Any::class)
    lateinit var haystack: Collection<Any>

    @FunctionOutput(0, name = "Check")
    var allIncluded: Boolean? = null

    override fun execute() {
        allIncluded = haystack.containsAll(candidates)
    }
}
