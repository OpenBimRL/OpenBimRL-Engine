package de.rub.bi.inf.openbimrl.functions

import de.rub.bi.inf.logger.BoundsWithProperties
import de.rub.bi.inf.logger.RuleLogger
import de.rub.bi.inf.openbimrl.NodeProxy
import java.util.*

abstract class DisplayableFunction(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    protected var logger: Optional<RuleLogger> = Optional.empty()

    fun setLogger(logger: RuleLogger) {
        this.logger = Optional.of(logger)
    }

    protected open fun logGraphically(what: List<BoundsWithProperties>) {
        if (logger.isEmpty) return
        logger.get().logGraphicalOutput(this.nodeProxy.node.id, what)
    }
}