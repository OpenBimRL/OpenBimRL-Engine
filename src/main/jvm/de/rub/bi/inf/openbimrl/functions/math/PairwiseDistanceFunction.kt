package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.utils.math.distanceBetween

abstract class PairwiseDistanceFunction(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    protected open fun distance(first: Any, second: Any): Double = distanceBetween(first, second)

    override fun execute() {
        val first = getInput<Any>(0) ?: return
        val second = getInput<Any>(1) ?: return
        setResult(0, computePairwise(first, second))
    }

    private fun computePairwise(first: Any, second: Any): Any {
        if (first is List<*> && second is List<*> && first.size == second.size) {
            return first.indices.map { index -> distance(first[index]!!, second[index]!!) }
        }

        if (first is List<*> && second !is List<*>) {
            return first.map { value -> distance(value!!, second) }
        }

        if (second is List<*> && first !is List<*>) {
            return second.map { value -> distance(first, value!!) }
        }

        return distance(first, second)
    }
}
