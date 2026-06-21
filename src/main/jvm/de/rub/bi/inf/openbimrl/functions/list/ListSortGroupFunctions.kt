package de.rub.bi.inf.openbimrl.functions.list

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Groups a list of items by key and value pairs.")
class GroupBy(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "List", collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionInput(1, name = "Reference List", collectionType = Any::class)
    lateinit var referenceList: Collection<Any>

    @FunctionOutput(0, name = "Key")
    var keys: ArrayList<Any>? = null

    @FunctionOutput(1, name = "Value")
    var values: ArrayList<Any>? = null

    override fun execute() {
        if (list.size != referenceList.size) return

        val listValues = list.toList()
        val referenceValues = referenceList.toList()
        val bins = linkedMapOf<Any, ArrayList<Any>>()

        listValues.forEachIndexed { index, value ->
            val key = referenceValues[index]
            bins.computeIfAbsent(key) { ArrayList() }.add(value)
        }

        keys = ArrayList(listValues.size)
        values = ArrayList(listValues.size)
        referenceValues.forEach { key ->
            keys!!.add(key)
            values!!.add(bins.getValue(key))
        }
    }
}

@OpenBIMRLFunction(description = "Sorts a list of items in ASC or DESC order.")
class OrderBy(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "List", collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionInput(1, name = "Reference List", collectionType = Any::class)
    lateinit var referenceList: Collection<Any>

    @FunctionInput(2, name = "Order", nullable = true)
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
        sortedReference.forEach { key ->
            sortedList!!.add(originalList[oldPositions.getValue(key)])
        }
        sortedReferenceList = sortedReference
    }
}
