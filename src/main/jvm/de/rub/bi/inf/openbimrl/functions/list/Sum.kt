package de.rub.bi.inf.openbimrl.functions.list

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel
import de.rub.bi.inf.openbimrl.functions.AbstractFunction

/**
 * Calculates the sum of all numbers contained in a given list.
 *
 * @author Marcel Stepien
 */
class Sum(nodeProxy: NodeProxy?) : AbstractFunction(nodeProxy) {
    override fun execute() {
        val objects = getInputAsCollection(0)

        if (objects.filterIsInstance<Collection<Any?>>().isNotEmpty())
            setResult(0, objects.map { handleCollection(if (it is Collection<Any?>) it else listOf(it)) })
        else
            setResult(0, handleCollection(objects))
    }

    private fun handleCollection(collection: Collection<Any?>): Double =
        collection.filterNotNull().fold(0.0) { acc, item -> acc + (item.toString().toDouble()) }
}
