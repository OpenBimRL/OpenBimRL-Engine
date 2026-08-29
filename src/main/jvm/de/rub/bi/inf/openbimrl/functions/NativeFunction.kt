package de.rub.bi.inf.openbimrl.functions

import de.rub.bi.inf.openbimrl.NodeProxy

abstract class NativeFunction(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    abstract fun executeNative()

    final override fun execute() {
        executeNative()
    }
}
