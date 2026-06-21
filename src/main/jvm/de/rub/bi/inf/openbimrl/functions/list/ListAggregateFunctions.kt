package de.rub.bi.inf.openbimrl.functions.list

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Calculates the sum of all numbers contained in a given list.")
class Sum(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, collectionType = Double::class)
    lateinit var list: Collection<Double>

    @FunctionOutput(0)
    var result: Double? = null

    override fun execute() {
        result = list.sum()
    }
}

@OpenBIMRLFunction(description = "Counts the items in a list and returns the number.")
class Count(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, collectionType = Any::class)
    lateinit var list: Collection<Any>

    @FunctionOutput(0)
    var result: Int? = null

    override fun execute() {
        result = list.size
    }
}
