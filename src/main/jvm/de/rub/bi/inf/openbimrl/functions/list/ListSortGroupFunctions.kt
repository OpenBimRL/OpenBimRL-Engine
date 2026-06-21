package de.rub.bi.inf.openbimrl.functions.list

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Groups a list of items by key and value pairs.")
class GroupBy(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionInput(1, name = "Reference List", collectionType = Any::class)
    lateinit var referenceList: Collection<Any>

    @FunctionOutput(0)
    var key: ArrayList<Any>? = null

    @FunctionOutput(1)
    var value: ArrayList<Any>? = null

    override fun execute() {
        if (list.size != referenceList.size) return

        val listValues = list.toList()
        val referenceValues = referenceList.toList()
        val bins = linkedMapOf<Any, ArrayList<Any>>()

        listValues.forEachIndexed { index, item ->
            val groupKey = referenceValues[index]
            bins.computeIfAbsent(groupKey) { ArrayList() }.add(item)
        }

        key = ArrayList(listValues.size)
        value = ArrayList(listValues.size)
        referenceValues.forEach { groupKey ->
            key!!.add(groupKey)
            value!!.add(bins.getValue(groupKey))
        }
    }
}

@OpenBIMRLFunction(description = "Sorts a list of items in ASC or DESC order.")
class OrderBy(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionInput(1, name = "Reference List", collectionType = Any::class)
    lateinit var referenceList: Collection<Any>

    @FunctionInput(2, nullable = true)
    var order: String? = null

    @FunctionOutput(0, name = "Sorted List")
    var sortedList: ArrayList<Any>? = null

    @FunctionOutput(1, name = "Sorted Reference Values")
    var sortedReferenceList: ArrayList<Any>? = null

    override fun execute() {
        if (list.size != referenceList.size) return

        val originalList = ArrayList(list)
        val originalReference = ArrayList(referenceList)
        val oldPositions = originalReference.withIndex().associate { (index, value) -> value to index }

        val sortedReference = if (order.equals("desc", ignoreCase = true)) {
            originalReference.sortedWith(compareByDescending { it.toString() }).toCollection(ArrayList())
        } else {
            originalReference.sortedWith(compareBy { it.toString() }).toCollection(ArrayList())
        }

        sortedList = ArrayList(originalList.size)
        sortedReference.forEach { groupKey ->
            sortedList!!.add(originalList[oldPositions.getValue(groupKey)])
        }
        sortedReferenceList = sortedReference
    }
}
