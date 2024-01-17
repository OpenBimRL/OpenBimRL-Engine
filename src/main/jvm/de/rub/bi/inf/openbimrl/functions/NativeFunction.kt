package de.rub.bi.inf.openbimrl.functions

import com.sun.jna.Memory
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import de.rub.bi.inf.nativelib.FunctionsLibrary
import de.rub.bi.inf.nativelib.FunctionsNative
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel
import java.util.*

abstract class NativeFunction(nodeProxy: NodeProxy?) : AbstractFunction(nodeProxy) {
    protected val nativeLib: FunctionsLibrary = FunctionsNative.getInstance()

    protected data class MemoryStructure(val at: Int, val size: Int, val memory: Memory)

    abstract fun executeNative()

    private fun <T> getInputAs(at: Int, clazz: Class<T>?): T? {
        val output = getInput<Any>(at)
        return try {
            if (clazz!!.isInstance(output)) clazz.cast(output) else null
        } catch (e: ClassCastException) { // ignore error
            null
        }
    }

    final override fun execute(ifcModel: IIFCModel?) {
        val memoryQueue: Queue<MemoryStructure> = LinkedList()
        nativeLib.init_function(
            { at: Int -> getInputAs(at, Pointer::class.java) },
            { at: Int -> getInputAs(at, Double::class.javaPrimitiveType)!! },
            { at: Int -> getInputAs(at, Int::class.javaPrimitiveType)!! },
            { at: Int -> getInputAs(at, String::class.java) },
            this::handlePointerOutput,
            { pos: Int, result: Double -> this.setResult(pos, result) },
            { pos: Int, result: Int -> this.setResult(pos, result) },
            { pos: Int, result: String? -> this.setResult(pos, result) },
            { at: Int, size: Int ->
                val memory = Memory((size * NativeLong.SIZE).toLong())
                memoryQueue.add(MemoryStructure(at, size, memory))
                memory
            })

        executeNative()

        handleMemory(memoryQueue)

    }

    protected open fun handleMemory(memoryQueue: Queue<MemoryStructure>) {
        memoryQueue.clear() // technically not necessary
    }

    protected open fun handlePointerOutput(at: Int, pointer: Pointer) {
        if (pointer == Pointer.NULL)
            setResult(at, null)
        else
            setResult(at, pointer)
    }
}
