package de.rub.bi.inf.openbimrl.functions

import de.rub.bi.inf.logger.RuleLogger
import de.rub.bi.inf.openbimrl.NodeProxy
import java.util.Optional

abstract class DisplayableFunction(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    protected var logger: Optional<RuleLogger> = Optional.empty()

    fun setLogger(logger: RuleLogger) {
        this.logger = Optional.of(logger)
    }
}