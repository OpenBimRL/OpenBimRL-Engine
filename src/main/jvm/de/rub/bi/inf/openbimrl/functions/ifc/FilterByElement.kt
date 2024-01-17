package de.rub.bi.inf.openbimrl.functions.ifc

import com.sun.jna.Pointer
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import java.util.*

/**
 * Filters an IFC model and retrieves all elements of a certain type.
 *
 * @author Marcel Stepien
 */
class FilterByElement(nodeProxy: NodeProxy?) : NativeFunction(nodeProxy) {
    override fun executeNative() = nativeLib.filterByElement()

    override fun handleMemory(memoryQueue: Queue<MemoryStructure>) {
        for (memoryStructure in memoryQueue) {
            if (memoryStructure.memory == Pointer.NULL)
                setResult(memoryStructure.at, emptyList<IfcPointer>())
            else setResult(
                memoryStructure.at,
                memoryStructure.memory.getLongArray(0, memoryStructure.size).map { IfcPointer(it) })
        }
        memoryQueue.clear() // technically this should automatically happen.
    }

    override fun handlePointerOutput(at: Int, pointer: Pointer) {
        setResult(at, emptyList<IfcPointer>())
    }
}
