package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction

abstract class PairwiseNumericFunction(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    protected open fun combine(first: Double, second: Double): Double = first + second

    override fun execute() {
        val first = getInput<Any>(0) ?: return
        val second = getInput<Any>(1) ?: return
        setResult(0, computePairwise(first, second))
    }

    private fun computePairwise(first: Any, second: Any): Any {
        val results = when {
            first is List<*> && second is List<*> && first.size == second.size ->
                first.indices.map { index ->
                    combine(first[index]!!.toDouble(), second[index]!!.toDouble())
                }

            first is List<*> && second !is List<*> -> {
                val scalar = second.toDouble()
                first.map { value -> combine(value!!.toDouble(), scalar) }
            }

            second is List<*> && first !is List<*> -> {
                val scalar = first.toDouble()
                second.map { value -> combine(scalar, value!!.toDouble()) }
            }

            else -> combine(first.toDouble(), second.toDouble())
        }

        return if (results is List<*> && results.size == 1) results[0]!! else results
    }

    private fun Any.toDouble(): Double = toString().toDouble()
}
