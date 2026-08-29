package de.rub.bi.inf.openbimrl.utils.ifc

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.nativelib.NativeEngine
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromDoubles
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
    fun fetchBoundingBox(element: IfcPointer): Pair<Point3d, BoundingBox>? {
        val bounds = NativeEngine.getBoundingBox(element.handle) ?: return null
        return boundingBoxFromDoubles(bounds)
    }

    fun fetchElementFrame(element: IfcPointer): NativeElementFrame? {
        val coords = NativeEngine.getElementFrame(element.handle) ?: return null
        if (coords.size < 9) return null
        val source = ApproximationSource.fromNative(
            NativeEngine.getElementFrameSource(element.handle),
        )
        return NativeElementFrame(
            point = Point3d(coords[0], coords[1], coords[2]),
            axisX = Vector3d(coords[3], coords[4], coords[5]),
            axisZ = Vector3d(coords[6], coords[7], coords[8]),
            source = source,
        )
    }
}
