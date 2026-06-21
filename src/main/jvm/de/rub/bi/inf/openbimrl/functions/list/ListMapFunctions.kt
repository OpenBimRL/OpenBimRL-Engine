package de.rub.bi.inf.openbimrl.functions.list

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.list.singleOrList

@OpenBIMRLFunction(description = "Creates a map of elements, if a key and value list is provided.")
class CreateMapByPairs(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Keys", collectionType = Any::class)
    lateinit var keys: Collection<Any>

    @FunctionInput(1, name = "Values", collectionType = Any::class)
    lateinit var values: Collection<Any>

    @FunctionOutput(0, name = "Keys")
    var resultKeys: Collection<Any>? = null

    @FunctionOutput(1, name = "Values")
    var resultValues: Collection<Any>? = null

    @FunctionOutput(2, name = "Map")
    var map: LinkedHashMap<Any, List<Any>>? = null

    override fun execute() {
        if (keys.size != values.size) return

        val keyList = keys.toList()
        val valueList = values.toList()
        val grouped = LinkedHashMap<Any, ArrayList<Any>>()

        keyList.forEachIndexed { index, rawKey ->
            val key = rawKey ?: "undefined"
            val bucket = grouped.computeIfAbsent(key) { ArrayList() }
            when (val value = valueList[index]) {
                is Collection<*> -> value.forEach { bucket.add(it as Any) }
                else -> bucket.add(value)
            }
        }

        map = LinkedHashMap(grouped.mapValues { it.value.toList() })
        resultKeys = map!!.keys
        resultValues = map!!.values
    }
}

@OpenBIMRLFunction(description = "Reverses the map, making values to keys and keys to values.")
class MapInvert(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Map", nullable = true)
    var sourceMap: Map<*, *>? = null

    @FunctionOutput(0, name = "Inverted Map")
    var invertedMap: LinkedHashMap<Any, Any>? = null

    override fun execute() {
        val map = sourceMap ?: return
        val inverted = LinkedHashMap<Any, Any>()

        map.forEach { (key, value) ->
            when (value) {
                is ArrayList<*> -> value.forEach { item ->
                    appendInverted(inverted, item, key)
                }

                is HashSet<*> -> value.forEach { item ->
                    appendInvertedSet(inverted, item, key)
                }

                else -> inverted[value as Any] = key as Any
            }
        }

        invertedMap = inverted
    }

    private fun appendInverted(target: LinkedHashMap<Any, Any>, item: Any?, key: Any?) {
        val bucket = target.computeIfAbsent(item as Any) { ArrayList<Any>() } as ArrayList<Any>
        bucket.add(key as Any)
    }

    private fun appendInvertedSet(target: LinkedHashMap<Any, Any>, item: Any?, key: Any?) {
        val bucket = target.computeIfAbsent(item as Any) { HashSet<Any>() } as HashSet<Any>
        bucket.add(key as Any)
    }
}

@OpenBIMRLFunction(description = "Retrieves the keyset of a assigned map.")
class MapKeySet(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Map", nullable = true)
    var sourceMap: Map<*, *>? = null

    @FunctionOutput(0, name = "Key Set")
    var keys: Set<Any?>? = null

    override fun execute() {
        keys = sourceMap?.keys
    }
}

@OpenBIMRLFunction(description = "Retrieves the values of a assigned map.")
class MapValues(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Map", nullable = true)
    var sourceMap: Map<*, *>? = null

    @FunctionOutput(0, name = "Values")
    var values: Collection<Any?>? = null

    override fun execute() {
        values = sourceMap?.values
    }
}

@OpenBIMRLFunction(description = "Retrieve the value in a map by key.")
class GetMapValueAtKey(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Map", nullable = true)
    var sourceMap: Map<*, *>? = null

    @FunctionInput(1, name = "Key", nullable = true)
    var key: Any? = null

    @FunctionOutput(0, name = "Value")
    var value: Any? = null

    override fun execute() {
        val map = sourceMap ?: return
        value = map[key]
    }
}

@OpenBIMRLFunction
class GetMapValueByKeyList(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Map", nullable = true)
    var sourceMap: Map<*, *>? = null

    @FunctionInput(1, name = "Keys", collectionType = Any::class)
    lateinit var keys: Collection<Any>

    @FunctionOutput(0, name = "Values")
    var values: Any? = null

    override fun execute() {
        val map = sourceMap ?: return
        val resolved = keys.map { map[it.toString()] }
        values = singleOrList(resolved)
    }
}

@OpenBIMRLFunction(description = "Generates a filter mask by number of objects contained in the Map values.")
class MapFilterByCount(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Map", nullable = true)
    var sourceMap: Map<*, *>? = null

    @FunctionInput(1, name = "Counting Limit", nullable = true)
    var minimumCountInput: String? = null

    @FunctionOutput(0, name = "Filter")
    var filter: ArrayList<Boolean>? = null

    override fun execute() {
        val map = sourceMap ?: return
        val minimumCount = minimumCountInput?.toIntOrNull() ?: 0

        filter = map.values.mapTo(ArrayList()) { value ->
            when (value) {
                is Collection<*> -> value.size >= minimumCount
                null -> 0 >= minimumCount
                else -> 1 >= minimumCount
            }
        }
    }
}

@OpenBIMRLFunction(
    description = "Counts the number of objects contained in the Map values and generates a new map based on that.",
)
class MapValueCount(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "Map", nullable = true)
    var sourceMap: Map<*, *>? = null

    @FunctionOutput(0, name = "Map Counted")
    var counts: LinkedHashMap<Any, Any>? = null

    override fun execute() {
        val map = sourceMap ?: return
        val counter = LinkedHashMap<Any, Any>()

        map.forEach { (key, value) ->
            if (value is Collection<*>) {
                counter[key as Any] = value.size
            } else {
                counter[value as Any] = 1
            }
        }

        counts = counter
    }
}
