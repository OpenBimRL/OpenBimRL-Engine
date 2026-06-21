package de.rub.bi.inf.openbimrl.utils.list

fun singleOrList(items: List<Any?>): Any? =
    if (items.size == 1) items[0] else items

fun parseIntInput(value: Any): Int = value.toString().toInt()

fun findIndex(list: List<*>, item: Any?): Int {
    val target = item?.toString() ?: return -1
    list.forEachIndexed { index, candidate ->
        if (candidate?.toString() == target) return index
    }
    return -1
}
