package de.rub.bi.inf.openbimrl.utils.ifc

import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.utils.math.Straight
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import javax.vecmath.Vector3d

private data class BboxAxes(
    val center: Point3d,
    val longest: Vector3d,
    val mid: Vector3d,
    val shortest: Vector3d,
)

object ElementApproximationService {
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
            Straight(Point3d(axes.center), Vector3d(axes.longest)),
            ApproximationSource.BBOX,
        )
    }

    fun toPlane(element: IfcPointer): ApproximationResult<Plane> {
        frameApproximation(element)?.let { (frame, source) ->
            return ApproximationResult(
                Plane(
                    Point3d(frame.point),
                    Vector3d(frame.axisX),
                    Vector3d(frame.axisZ),
                ),
                source,
            )
        }

        val (center, bbox) = bboxApproximation(element)
        val axes = axesFromBoundingBox(bbox, center)
        val normal = Vector3d(axes.shortest)
        val axisU = Vector3d(axes.longest)
        val axisV = Vector3d(axes.mid)
        return ApproximationResult(
            Plane(Point3d(axes.center), axisU, axisV),
            ApproximationSource.BBOX,
        )
    }

    fun toPoint(element: IfcPointer): ApproximationResult<Point3d> {
        frameApproximation(element)?.let { (frame, source) ->
            return ApproximationResult(Point3d(frame.point), source)
        }

        val (center, _) = bboxApproximation(element)
        return ApproximationResult(Point3d(center), ApproximationSource.BBOX)
    }

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
