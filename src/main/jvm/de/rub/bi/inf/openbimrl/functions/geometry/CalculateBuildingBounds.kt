package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromMemory
import java.util.*

/**
 * returns the Buildings bounding box
 * @author Florian Becker
 */
@OpenBIMRLFunction
class CalculateBuildingBounds(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    override fun executeNative() = nativeLib.calculatingBuildingBounds()


    override fun handleMemory(memoryQueue: Queue<MemoryStructure>) {
        val result = boundingBoxFromMemory(memoryQueue.remove())
        setResult(0, result.second)
        setResult(1, result.first)
    }
}