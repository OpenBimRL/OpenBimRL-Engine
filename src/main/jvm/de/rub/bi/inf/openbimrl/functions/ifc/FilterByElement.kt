package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.nativelib.NativeEngine
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(
    description = "Filters a IFC model and retrieves all elements of a certain type.",
    inputs = [
        FunctionPort(0, "IfcType", String::class),
    ],
    outputs = [
        FunctionPort(0, "IfcElement_List", IfcPointer::class, isCollection = true),
    ],
)
class FilterByElement(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    override fun executeNative() {
        val ifcType = getInput<String>(0) ?: return
        setResult(0, IfcPointer.fromHandles(NativeEngine.filterByElement(ifcType)))
    }
}
