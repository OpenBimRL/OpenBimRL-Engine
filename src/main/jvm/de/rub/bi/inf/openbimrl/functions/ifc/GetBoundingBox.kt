package de.rub.bi.inf.openbimrl.functions.ifc

import com.sun.jna.Pointer
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromMemory
import java.util.*

import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import kotlin.collections.ArrayList

/**
 * Retrieves the BoundingBox of an IFC element.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
class GetBoundingBox(nodeProxy: NodeProxy) : NativeFunction(nodeProxy) {
    private var currentPointer: IfcPointer? = null

    override fun handlePointerInput(at: Int): Pointer? {
        if (currentPointer == null) throw IndexOutOfBoundsException("tried to call native function with insufficient elements")

        return currentPointer
    }

    override fun executeNative() {
        when (val input = getInput<Any>(0)) {
            is IfcPointer -> {
                currentPointer = input
                nativeLib.getBoundingBox()
            }

            is Collection<*> -> input.filterIsInstance<IfcPointer>().forEach {
                currentPointer = it
                nativeLib.getBoundingBox()
            }
        }
    }

    override fun handleMemory(memoryQueue: Queue<MemoryStructure>) {
        when (getInput<Any>(0)) { // when single element
            is IfcPointer -> {
                val result = boundingBoxFromMemory(memoryQueue.remove())
                setResult(0, result.second)
                setResult(1, result.first)
            }

            is Collection<*> -> { // when collection was requested
                val centerPoints = ArrayList<Point3d>(memoryQueue.size)
                val bBoxes = ArrayList<BoundingBox>(memoryQueue.size)

                for ((index, element) in memoryQueue.withIndex()) {
                    val result = boundingBoxFromMemory(element)
                    centerPoints.add(index, result.first)
                    bBoxes.add(index, result.second)
                }
                setResult(0, bBoxes)
                setResult(1, centerPoints)
            }
        }
    }
}
