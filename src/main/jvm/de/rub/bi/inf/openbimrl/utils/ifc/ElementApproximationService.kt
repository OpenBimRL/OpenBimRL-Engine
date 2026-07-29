package de.rub.bi.inf.openbimrl.utils.ifc

import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.utils.math.Straight
import de.rub.bi.inf.openbimrl.utils.math.normalized
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import javax.vecmath.Vector3d
import kotlin.math.abs

private data class BboxAxes(
    val center: Point3d,
    val longest: Vector3d,
    val mid: Vector3d,
    val shortest: Vector3d,
)

object ElementApproximationService {
    private const val AXIS_EPSILON = 1e-6

    fun toStraight(element: IfcPointer): ApproximationResult<Straight> {
        frameApproximation(element)?.let { (frame, source) ->
            return ApproximationResult(
                Straight(Point3d(frame.point), Vector3d(frame.axisX)),
                source,
            )
        }

        val (center, bbox) = bboxApproximation(element)
        val axes = axesFromBoundingBox(bbox, center)
        return ApproximationResult(
            Straight(enginePointToIfc(axes.center), engineVectorToIfc(axes.longest)),
            ApproximationSource.BBOX,
        )
    }

    fun toPlane(element: IfcPointer): ApproximationResult<Plane> {
        val frameResult = frameApproximation(element)
        if (frameResult != null && shouldTrustFrameForPlane(frameResult.first)) {
            val frame = frameResult.first
            return ApproximationResult(
                Plane(
                    Point3d(frame.point),
                    Vector3d(frame.axisX),
                    Vector3d(frame.axisZ),
                ),
                frameResult.second,
            )
        }

        // World-default ObjectPlacement (Axis/RefDirection unset) often does not match
        // Brep solids: prefer the AABB face whose normal is the thinnest extent.
        val (center, bbox) = bboxApproximation(element)
        val axes = axesFromBoundingBox(bbox, center)
        return ApproximationResult(
            Plane(
                enginePointToIfc(axes.center),
                engineVectorToIfc(axes.longest),
                engineVectorToIfc(axes.mid),
            ),
            ApproximationSource.BBOX,
        )
    }

    fun toPoint(element: IfcPointer): ApproximationResult<Point3d> {
        frameApproximation(element)?.let { (frame, source) ->
            return ApproximationResult(Point3d(frame.point), source)
        }

        val (center, _) = bboxApproximation(element)
        return ApproximationResult(enginePointToIfc(center), ApproximationSource.BBOX)
    }

    private fun shouldTrustFrameForPlane(frame: NativeElementFrame): Boolean {
        if (frame.source == ApproximationSource.REPRESENTATION) return true
        // Oriented placements are authoritative; identity world axes are not (common for Breps).
        return !isWorldAlignedIdentityFrame(frame)
    }

    private fun isWorldAlignedIdentityFrame(frame: NativeElementFrame): Boolean {
        val x = normalized(frame.axisX)
        val z = normalized(frame.axisZ)
        val xIsWorldX = near(abs(x.x), 1.0) && nearZero(x.y) && nearZero(x.z)
        val zIsWorldZ = near(abs(z.z), 1.0) && nearZero(z.x) && nearZero(z.y)
        return xIsWorldX && zIsWorldZ
    }

    private fun near(a: Double, b: Double): Boolean = abs(a - b) <= AXIS_EPSILON

    private fun nearZero(a: Double): Boolean = abs(a) <= AXIS_EPSILON

    /**
     * Native bounding boxes are stored as engine XZY (IFC x,z,y).
     * Plane/Straight math and visualizers expect IFC XYZ (Z-up).
     */
    private fun enginePointToIfc(point: Point3d): Point3d =
        Point3d(point.x, point.z, point.y)

    private fun engineVectorToIfc(vector: Vector3d): Vector3d =
        Vector3d(vector.x, vector.z, vector.y)

    private fun frameApproximation(
        element: IfcPointer,
    ): Pair<NativeElementFrame, ApproximationSource>? {
        val frame = NativeIfcGeometry.fetchElementFrame(element) ?: return null
        return frame to frame.source
    }

    private fun bboxApproximation(element: IfcPointer): Pair<Point3d, BoundingBox> {
        val bbox = NativeIfcGeometry.fetchBoundingBox(element)
            ?: throw IllegalStateException("Unable to compute bounding box for IFC element")
        return bbox
    }

    private fun axesFromBoundingBox(bbox: BoundingBox, center: Point3d): BboxAxes {
        val lower = bbox.lower()
        val upper = bbox.upper()
        val edges = listOf(
            Edge(upper.x - lower.x, Vector3d(1.0, 0.0, 0.0)),
            Edge(upper.y - lower.y, Vector3d(0.0, 1.0, 0.0)),
            Edge(upper.z - lower.z, Vector3d(0.0, 0.0, 1.0)),
        ).sortedByDescending { it.length }

        return BboxAxes(
            center = Point3d(center),
            longest = Vector3d(edges[0].direction),
            mid = Vector3d(edges[1].direction),
            shortest = Vector3d(edges[2].direction),
        )
    }

    private data class Edge(val length: Double, val direction: Vector3d)
}
