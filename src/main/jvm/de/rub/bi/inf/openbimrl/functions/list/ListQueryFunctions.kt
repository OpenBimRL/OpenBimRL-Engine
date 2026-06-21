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

    @FunctionInput(0, collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionInput(1, collectionType = Any::class)
    lateinit var index: Collection<Any>

    @FunctionOutput(0)
    var item: Any? = null

    override fun execute() {
        val listValues = list.toList()
        val elementsAt = index.mapIndexed { position, positionInput ->
            val parsedIndex = parseIntInput(positionInput)
            if (index.size == 1) {
                listValues[parsedIndex]
            } else {
                val values = listValues[position]
                if (values is List<*>) values[parsedIndex] else values
            }
        }

        item = singleOrList(elementsAt)
    }
}

@OpenBIMRLFunction(description = "Retrieves an item index of a specific value contained in a list of elements.")
class GetItemIndexInList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionInput(1, collectionType = Any::class)
    lateinit var item: Collection<Any>

    @FunctionOutput(0)
    var index: Any? = null

    override fun execute() {
        val listValues = ArrayList(list)
        val indices = item.map { findIndex(listValues, it) }
        index = singleOrList(indices)
    }
}

@OpenBIMRLFunction(description = "Checks if a list of elements are included in another set individually.")
class ElementIncludedInList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, collectionType = Any::class)
    lateinit var elements: Collection<Any>

    @FunctionInput(1, collectionType = Any::class)
    lateinit var inclusionList: Collection<Any>

    @FunctionOutput(0, name = "ListOfChecks")
    var included: List<Boolean>? = null

    override fun execute() {
        included = elements.map { inclusionList.contains(it) }
    }
}

@OpenBIMRLFunction(
    name = "allElementIncludedInList",
    description = "Checks if a list of elements are included in another set holistically.",
)
class AllElementsIncludedInList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, collectionType = Any::class)
    lateinit var elements: Collection<Any>

    @FunctionInput(1, collectionType = Any::class)
    lateinit var inclusionList: Collection<Any>

    @FunctionOutput(0)
    var check: Boolean? = null

    override fun execute() {
        check = inclusionList.containsAll(elements)
    }
}
