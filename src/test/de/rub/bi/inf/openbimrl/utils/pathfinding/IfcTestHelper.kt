package de.rub.bi.inf.openbimrl.utils.pathfinding

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import de.rub.bi.inf.nativelib.FunctionsNative
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromMemory
import de.rub.bi.inf.openbimrl.functions.NativeFunction
import java.nio.file.Paths
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

object IfcTestHelper {

    const val DOOR_1_GUID = "0PathTest000000000022"
    const val DOOR_2_GUID = "0PathTest000000000026"

    fun pathfindingMinimalIfcPath(): String =
        Paths.get("src", "test", "resources", "pathfinding_minimal.ifc")
            .toFile()
            .absolutePath

    fun showDistancesOpenBimRLPath(): String =
        Paths.get("src", "test", "resources", "show_distances.openbimrl")
            .toFile()
            .absolutePath

    fun loadNativeLibrary() {
        FunctionsNative.create("lib.so")
    }

    fun loadIfc(absolutePath: String): Boolean =
        FunctionsNative.getInstance().initIfc(absolutePath)

    fun loadPathfindingMinimalIfc(): Boolean =
        loadIfc(pathfindingMinimalIfcPath())

    fun filterByElement(ifcType: String): List<IfcPointer> {
        var buffer: Memory? = null
        var bufferSize = 0L

        FunctionsNative.getInstance().init_function(
            { null },
            { 0.0 },
            { 0 },
            { at -> if (at == 0) ifcType else null },
            { _, _ -> },
            { _, _ -> },
            { _, _ -> },
            { _, _ -> },
            { _, size: NativeLong ->
                bufferSize = size.toLong()
                Memory(bufferSize).also { buffer = it }
            },
        )

        FunctionsNative.getInstance().filterByElement()

        val memory = buffer ?: return emptyList()
        val pointerCount = (bufferSize / Native.POINTER_SIZE).toInt()
        return buildList {
            for (index in 0 until pointerCount) {
                val pointer = memory.getPointer(index * Native.POINTER_SIZE.toLong())
                if (pointer != null && pointer != Pointer.NULL) {
                    add(IfcPointer(pointer))
                }
            }
        }
    }

    fun calculateBuildingBounds(): Pair<Point3d, BoundingBox> {
        var buffer: Memory? = null

        FunctionsNative.getInstance().init_function(
            { null },
            { 0.0 },
            { 0 },
            { null },
            { _, _ -> },
            { _, _ -> },
            { _, _ -> },
            { _, _ -> },
            { _, size: NativeLong ->
                Memory(size.toLong()).also { buffer = it }
            },
        )

        FunctionsNative.getInstance().calculatingBuildingBounds()

        return boundingBoxFromMemory(NativeFunction.MemoryStructure(0, 6 * 8L, buffer!!))
    }

    fun getElementByGuid(guid: String): IfcPointer? {
        var result: Pointer? = null

        FunctionsNative.getInstance().init_function(
            { null },
            { 0.0 },
            { 0 },
            { at -> if (at == 0) guid else null },
            { at, pointer ->
                if (at == 0 && pointer != null && pointer != Pointer.NULL) {
                    result = pointer
                }
            },
            { _, _ -> },
            { _, _ -> },
            { _, _ -> },
            { _, _ -> Pointer.NULL },
        )

        FunctionsNative.getInstance().filterByGUID()

        return result?.let { IfcPointer(it) }
    }
}
