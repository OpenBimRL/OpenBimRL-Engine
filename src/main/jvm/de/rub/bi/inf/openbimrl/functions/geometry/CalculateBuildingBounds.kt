package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.helper.boundingBoxFromMemory
import java.util.*

class CalculateBuildingBounds(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    override fun executeNative() = nativeLib.calculatingBuildingBounds()


    override fun handleMemory(memoryQueue: Queue<MemoryStructure>) {
        val result = boundingBoxFromMemory(memoryQueue.remove())
        setResult(0, result.second)
        setResult(1, result.first)
    }
}