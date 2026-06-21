package de.rub.bi.inf.openbimrl.functions.ifc

import com.sun.jna.Pointer
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(
    description = "Filters a IFC model and retrieves the element of a certain GUID.",
    inputs = [
        FunctionPort(0, "GUID", String::class),
    ],
    outputs = [
        FunctionPort(0, "IfcElement", IfcPointer::class),
    ],
)
class FilterByGUID(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    override fun executeNative() {
        nativeLib.filterByGUID()
    }

    override fun handlePointerOutput(at: Int, pointer: Pointer?) {
        if (pointer == null) return
        super.handlePointerOutput(at, IfcPointer(pointer))
    }
}
