package de.rub.bi.inf.openbimrl.utils.ifc

import com.sun.jna.Memory
import com.sun.jna.NativeLong
import de.rub.bi.inf.nativelib.FunctionsLibrary
import de.rub.bi.inf.nativelib.FunctionsNative
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromMemory
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import javax.vecmath.Vector3d

enum class ApproximationSource {
    PLACEMENT,
    REPRESENTATION,
    BBOX,
    PCA,
    ;

    companion object {
        fun fromNative(code: Int): ApproximationSource = when (code) {
            1 -> PLACEMENT
            2 -> REPRESENTATION
            3 -> BBOX
            else -> BBOX
        }
    }
}

data class ApproximationResult<T>(
    val value: T,
    val source: ApproximationSource,
)

data class NativeElementFrame(
    val point: Point3d,
    val axisX: Vector3d,
    val axisZ: Vector3d,
    val source: ApproximationSource,
)

object NativeIfcGeometry {
    private val nativeLib: FunctionsLibrary = FunctionsNative.getInstance()

    fun fetchBoundingBox(element: IfcPointer): Pair<Point3d, BoundingBox>? {
        var buffer: Memory? = null
        var bufferSize = 0L

        nativeLib.init_function(
            { at -> if (at == 0) element else null },
            { 0.0 },
            { 0 },
            { null },
            { _, _ -> },
            { _, _ -> },
            { _, _ -> },
            { _, _ -> },
            { _, size: NativeLong ->
                bufferSize = size.toLong()
                Memory(bufferSize).also { buffer = it }
            },
        )
        nativeLib.getBoundingBox()
        val mem = buffer ?: return null
        return boundingBoxFromMemory(NativeFunction.MemoryStructure(0, bufferSize, mem))
    }

    fun fetchElementFrame(element: IfcPointer): NativeElementFrame? {
        var buffer: Memory? = null
        var bufferSize = 0L
        var sourceCode: Int? = null

        nativeLib.init_function(
            { at -> if (at == 0) element else null },
            { 0.0 },
            { 0 },
            { null },
            { _, _ -> },
            { _, _ -> },
            { at, value -> if (at == 1) sourceCode = value },
            { _, _ -> },
            { _, size: NativeLong ->
                bufferSize = size.toLong()
                Memory(bufferSize).also { buffer = it }
            },
        )
        nativeLib.getElementFrame()
        val mem = buffer ?: return null
        val coords = mem.getDoubleArray(0, 9)
        val source = sourceCode ?: return null
        return NativeElementFrame(
            point = Point3d(coords[0], coords[1], coords[2]),
            axisX = Vector3d(coords[3], coords[4], coords[5]),
            axisZ = Vector3d(coords[6], coords[7], coords[8]),
            source = ApproximationSource.fromNative(source),
        )
    }
}
