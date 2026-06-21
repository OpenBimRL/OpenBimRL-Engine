package de.rub.bi.inf.openbimrl.functions.list

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.list.parseIntInput

@OpenBIMRLFunction(description = "Parses a delimited string into a list.")
class AsList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Values")
    lateinit var text: Any

    @FunctionOutput(0, name = "List")
    var list: Any? = null

    override fun execute() {
        val dim2Results = text.toString().split(";").map { dim1Str ->
            ArrayList<Any>(dim1Str.split(","))
        }

        list = if (dim2Results.size == 1) dim2Results[0] else dim2Results
    }
}

@OpenBIMRLFunction
class RepeatAsList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Item")
    lateinit var value: Any

    @FunctionInput(1, name = "Repeat Counter")
    lateinit var countInput: Any

    @FunctionOutput(0, name = "ArrayList")
    var list: List<Any>? = null

    override fun execute() {
        val count = parseIntInput(countInput)
        list = List(count) { value }
    }
}

@OpenBIMRLFunction(description = "Flattens the hierarchy of an Collection by one level.")
class FlattenCollection(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "List", collectionType = Any::class)
    lateinit var nestedList: Collection<Any>

    @FunctionOutput(0, name = "Flattened List")
    var flattenedList: Collection<Any>? = null

    override fun execute() {
        val flattened = ArrayList<Any>()
        nestedList.forEach { item ->
            if (item is Collection<*>) {
                item.forEach { flattened.add(it as Any) }
            } else {
                flattened.add(item)
            }
        }
        flattenedList = flattened
    }
}

@OpenBIMRLFunction(description = "Joins two collections to one ArrayList.")
class JoinCollections(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "List A", collectionType = Any::class)
    lateinit var listA: Collection<Any>

    @FunctionInput(1, name = "List B", collectionType = Any::class)
    lateinit var listB: Collection<Any>

    @FunctionOutput(0, name = "Joined List")
    var joinedList: ArrayList<Any>? = null

    override fun execute() {
        joinedList = ArrayList<Any>(listA.size + listB.size).apply {
            addAll(listA)
            addAll(listB)
        }
    }
}

@OpenBIMRLFunction(description = "Joins two collections of same size item by item to one ArrayList.")
class JoinItemByItem(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Collection A", collectionType = Any::class)
    lateinit var listA: Collection<Any>

    @FunctionInput(1, name = "Collection B", collectionType = Any::class)
    lateinit var listB: Collection<Any>

    @FunctionOutput(0, name = "Joined List")
    var joinedList: ArrayList<Any>? = null

    override fun execute() {
        if (listA.size != listB.size) return

        joinedList = listA.zip(listB).mapTo(ArrayList()) { (left, right) ->
            ArrayList<Any>().apply {
                if (left is Collection<*>) left.forEach { add(it as Any) } else add(left)
                if (right is Collection<*>) right.forEach { add(it as Any) } else add(right)
            }
        }
    }
}

@OpenBIMRLFunction(description = "Removes elements from list B in the list A.")
class RemoveFromList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "List A", collectionType = Any::class)
    lateinit var listA: Collection<Any>

    @FunctionInput(1, name = "List B", collectionType = Any::class)
    lateinit var listB: Collection<Any>

    @FunctionOutput(0, name = "Altered List A")
    var resultList: ArrayList<Any>? = null

    override fun execute() {
        resultList = ArrayList(listA).apply { removeAll(listB) }
    }
}
