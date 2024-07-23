package de.rub.bi.inf.openbimrl.functions.ifc

import com.sun.jna.Pointer
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction

/**
 * Filters an IFC model and retrieves the element of a certain GUID.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
class FilterByGUID(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    override fun executeNative() {
        nativeLib.filterByGUID()
    }

    override fun handlePointerOutput(at: Int, pointer: Pointer?) {
        if (pointer == null) return
        super.handlePointerOutput(at, IfcPointer(pointer))
    }
}
