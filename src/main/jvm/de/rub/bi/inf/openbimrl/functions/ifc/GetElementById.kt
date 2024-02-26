package de.rub.bi.inf.openbimrl.functions.ifc

import com.sun.jna.Pointer
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction

/**
 * Retrieves the IFC entity of a specific id from the model.
 *
 * For some reason this is the exact behaviour as the FilterByGUID function...
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
class GetElementById(nodeProxy: NodeProxy?) : NativeFunction(nodeProxy) {
    override fun executeNative() {
        nativeLib.filterByGUID()
    }

    override fun handlePointerOutput(at: Int, pointer: Pointer?) {
        if (pointer == null) return
        super.handlePointerOutput(at, IfcPointer(pointer))
    }
}
