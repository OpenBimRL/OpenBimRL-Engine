package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import kotlin.collections.AbstractCollection

/**
 * Retrieves the class type of element as String value.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
class GetIfcType(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    override fun execute() {
        when (val input = getInput<Any>(0)) {
            is AbstractCollection<*> -> setResult(0, input.filterIsInstance<IfcPointer>().map { it.type })
            is IfcPointer -> setResult(0, input.type)
        }
    }
}
