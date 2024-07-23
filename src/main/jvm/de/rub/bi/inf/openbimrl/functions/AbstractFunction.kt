package de.rub.bi.inf.openbimrl.functions

import de.rub.bi.inf.openbimrl.NodeProxy

/**
 * An abstract super-type of all functions supported by the OpenBimRL engine.
 * Instances of this class need to be registered in [FunctionFactory].
 * Furthermore, an implemented function can be addressed in an OpenBimRL file as
 * and elementary node of the pre-calculation graph.
 *
 * @author Marcel Stepien, Andre Vonthron (reworked by Florian Becker)
 */
abstract class AbstractFunction(@JvmField protected var nodeProxy: NodeProxy) {
    @JvmField
    val results: ArrayList<Any?>

    init {
        val outputLength = nodeProxy.node.outputs?.output?.size ?: 0
        results = ArrayList(outputLength)
        for (i in 0 until outputLength) {
            results.add(null) //initializing for set(i, element) method
        }
    }

    abstract fun execute()

    protected inline fun <reified T> getInput(pos: Int): T? {
        return when (val data = nodeProxy.getInputEdge(pos)?.currentData){
            is T -> data
            else -> null
        }
    }

    protected fun <T> getInput(pos: Int, clazz: Class<T>): T? {
        val data = nodeProxy.getInputEdge(pos)?.currentData
        return when {
            clazz.isInstance(data) -> clazz.cast(data)
            else -> null
        }
    }

    protected fun getInputAsCollection(pos: Int): Collection<*> {
        val `in` = getInput<Any>(pos)
        if (`in` is Collection<*>) return `in`

        if (`in` == null) return ArrayList<Any>()
        return listOf(`in`)
    }

    protected fun setResult(pos: Int, result: Any?) {
        results[pos] = result
    }
}
