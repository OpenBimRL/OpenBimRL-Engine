package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction

/**
 * Performs a multiplication operation given two number values.
 *
 * @author Florian Becker
 */
class Multiplication(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    override fun execute() {
        val object0 = getInput<Any>(0)
        val object1 = getInput<Any>(1)

        val results = ArrayList<Double>()
        if (object0 is List<*> &&
            object1 is List<*> &&
            ((object0 as Collection<*>).size == (object1 as Collection<*>).size)
        ) {
            for (i in (object0 as Collection<*>).indices) {
                results.add(
                    object0[i].toString().toDouble() * object1[i].toString().toDouble()
                )
            }
        } else if (object0 is List<*> && object1 !is List<*>) {
            for (i in (object0 as Collection<*>).indices) {
                results.add(
                    object0[i].toString().toDouble() * object1.toString().toDouble()
                )
            }
        } else if (object1 is List<*> && object0 !is List<*>) {
            for (i in (object1 as Collection<*>).indices) {
                results.add(
                    object0.toString().toDouble() * object1[i].toString().toDouble()
                )
            }
        } else {
            val operand0 = object0.toString().toDouble()
            val operand1 = object1.toString().toDouble()
            results.add(operand0 * operand1)
        }

        if (results.size == 1) {
            setResult(0, results[0])
        } else {
            setResult(0, results)
        }
    }
}
