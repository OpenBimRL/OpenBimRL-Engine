package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

@OpenBIMRLFunction(description = "Retrives the GUID of an IFC element.")
class GetGlobalID(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    @FunctionInput(0, name = "IfcElement_List", collectionType = IfcPointer::class)
    lateinit var ifcElements: Collection<IfcPointer>

    @FunctionOutput(0, name = "ID_List")
    var ids: Any? = null

    override fun execute() {
        if (ifcElements.isEmpty()) return

        val guids = ifcElements.map { it.guid }
        ids = if (guids.size == 1) guids[0] else guids
    }
}
