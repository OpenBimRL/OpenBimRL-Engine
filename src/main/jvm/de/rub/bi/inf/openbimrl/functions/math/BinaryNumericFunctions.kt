package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(
    description = "Performes an addition operator given two number values.",
    inputs = [
        FunctionPort(0, "number(s)", Double::class),
        FunctionPort(1, "number(s)", Double::class),
    ],
    outputs = [
        FunctionPort(0, "result", Double::class),
    ],
)
class Addition(nodeProxy: NodeProxy) : PairwiseNumericFunction(nodeProxy) {
    override fun combine(first: Double, second: Double): Double = first + second
}

@OpenBIMRLFunction(
    description = "Performes an subtraction operator given two number values.",
    inputs = [
        FunctionPort(0, "number(s)", Double::class),
        FunctionPort(1, "number(s)", Double::class),
    ],
    outputs = [
        FunctionPort(0, "result", Double::class),
    ],
)
class Subtraction(nodeProxy: NodeProxy) : PairwiseNumericFunction(nodeProxy) {
    override fun combine(first: Double, second: Double): Double = first - second
}

@OpenBIMRLFunction(
    name = "multiply",
    description = "Performes an multiplication operator given two number values.",
    inputs = [
        FunctionPort(0, "number(s)", Double::class),
        FunctionPort(1, "number(s)", Double::class),
    ],
    outputs = [
        FunctionPort(0, "result", Double::class),
    ],
)
class Multiplication(nodeProxy: NodeProxy) : PairwiseNumericFunction(nodeProxy) {
    override fun combine(first: Double, second: Double): Double = first * second
}
