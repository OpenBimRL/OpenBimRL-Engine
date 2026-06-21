package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Finds the maximum value in a list of values.")
class Maximum(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, collectionType = Double::class)
    lateinit var values: Collection<Double>

    @FunctionOutput(0)
    var maxima: Double? = null

    override fun execute() {
        maxima = values.maxOrNull()
    }
}
