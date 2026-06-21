package de.rub.bi.inf.openbimrl.functions.ifc

import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import java.util.*

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
    override fun executeNative() = nativeLib.filterByElement()

    override fun handleMemory(memoryQueue: Queue<MemoryStructure>) {
        for (memoryStructure in memoryQueue) {
            if (memoryStructure.memory == Pointer.NULL) {
                setResult(memoryStructure.at, emptyList<IfcPointer>())
                continue
            }

            try {
                val noOfElements = (memoryStructure.size / NativeLong.SIZE).toInt()
                setResult(
                    memoryStructure.at,
                    memoryStructure.memory.getLongArray(0, noOfElements).map { IfcPointer(it) },
                )
            } catch (_: IndexOutOfBoundsException) {
                throw IllegalArgumentException("provided array was not of type Pointer[]")
            }
        }
        memoryQueue.clear()
    }

    override fun handlePointerOutput(at: Int, pointer: Pointer?) {
        setResult(at, emptyList<IfcPointer>())
    }
}
