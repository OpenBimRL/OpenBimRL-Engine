package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.nativelib.NativeEngine
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(
    name = "getElementById",
    description = "Retrieves an IFC element by its GlobalId (GUID).",
    inputs = [
        FunctionPort(0, "GUID", String::class),
    ],
    outputs = [
        FunctionPort(0, "IfcElement", IfcPointer::class),
    ],
)
class GetElementById(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    override fun executeNative() {
        val guid = getInput<String>(0) ?: return
        setResult(0, IfcPointer.fromHandle(NativeEngine.filterByGuid(guid)))
    }
}
