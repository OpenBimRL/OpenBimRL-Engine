package de.rub.bi.inf.openbimrl.visualization

import com.github.ajalt.colormath.model.Oklab
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.SRGB
import com.github.ajalt.colormath.transform.interpolator
import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.utils.math.Straight
import de.rub.bi.inf.openbimrl.utils.math.lerp
import de.rub.bi.inf.openbimrl.utils.math.normalized
import de.rub.bi.inf.openbimrl.utils.math.planeNormal
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import javax.vecmath.Vector3d
import kotlin.math.min

internal data class SphereInstance(
    val x: Float,
    val y: Float,
    val z: Float,
    val scale: Float,
    val r: Float,
    val g: Float,
    val b: Float,
)

internal data class BoxInstance(
    val centerX: Float,
    val centerY: Float,
    val centerZ: Float,
    val sizeX: Double,
    val sizeY: Double,
    val sizeZ: Double,
)

internal data class LineSegmentInstance(
    val x1: Float,
    val y1: Float,
    val z1: Float,
    val x2: Float,
    val y2: Float,
    val z2: Float,
)

/**
 * Accumulates check visuals and encodes them as a binary GLB (glTF 2.0).
 *
 * Input geometry from IFC uses Z-up world coordinates (x, y, z).
 * Internally this matches native bounding boxes: engine (x, z, y).
 * GLB output uses Three.js Y-up viewer space: (engine.x, engine.y, -engine.z).
 */
class GltfVisualComposer {

    private val sphereInstances = mutableListOf<SphereInstance>()
    private val boxInstances = mutableListOf<BoxInstance>()
    private val lineInstances = mutableListOf<LineSegmentInstance>()

    val isEmpty: Boolean
        get() = sphereInstances.isEmpty() && boxInstances.isEmpty() && lineInstances.isEmpty()

    fun addDistanceHeatmap(
        points: Collection<Point3d>,
        distances: Collection<Double>,
        maxDistance: Double = 100.0,
        pointSize: Double = DEFAULT_SPHERE_RADIUS,
    ) {
        if (points.isEmpty()) return
        val distanceList = distances.toList()
        val colorInterpolator = Oklab.interpolator {
            stop(RGB("#0F0"))
            stop(RGB("#FF0"))
            stop(RGB("#F00"))
        }
        points.forEachIndexed { index, point ->
            val distance = distanceList.getOrNull(index) ?: return@forEachIndexed
            if (!distance.isFinite()) return@forEachIndexed
            val capped = min(distance, maxDistance)
            val t = min(lerp(capped, 0.0, maxDistance, 0.0, 1.0), 1.0)
            val color = if (distance == Double.POSITIVE_INFINITY) {
                SRGB.from255(203, 203, 203, 255)
            } else {
                colorInterpolator.interpolate(t).toSRGB()
            }
            addSphere(point, pointSize, color)
        }
    }

    fun addPoints(
        points: Collection<Point3d>,
        pointSize: Double,
        color: RGB = SRGB.from255(0, 255, 0, 255),
    ) {
        points.forEach { addSphere(it, pointSize, color.toSRGB()) }
    }

    fun addBounds(bounds: Collection<BoundingBox>, padding: Double = 0.0) {
        bounds.forEach { box ->
            val lower = box.lower()
            val upper = box.upper()
            boxInstances.add(
                BoxInstance(
                    centerX = (lower.x + upper.x).toFloat() * 0.5f,
                    centerY = (lower.y + upper.y).toFloat() * 0.5f,
                    centerZ = (-(lower.z + upper.z) * 0.5).toFloat(),
                    sizeX = kotlin.math.abs(upper.x - lower.x) + padding * 2,
                    sizeY = kotlin.math.abs(upper.y - lower.y) + padding * 2,
                    sizeZ = kotlin.math.abs(upper.z - lower.z) + padding * 2,
                ),
            )
        }
    }

    fun addIfcElements(
        elements: Collection<IfcPointer>,
        color: RGB = SRGB.from255(255, 165, 0, 255),
        pointSize: Double = DEFAULT_SPHERE_RADIUS * 2,
    ) {
        val srgb = color.toSRGB()
        elements.forEach { element ->
            val polygon = element.polygon.value.orElse(null) ?: return@forEach
            val bounds2d = polygon.bounds2D
            addSphere(Point3d(bounds2d.centerX, 0.0, bounds2d.centerY), pointSize, srgb)
        }
    }

    fun addStraights(
        straights: Collection<Straight>,
        segmentLength: Double = 10.0,
        referencePointSize: Double = DEFAULT_SPHERE_RADIUS * 0.6,
        color: RGB = SRGB.from255(0, 128, 255, 255),
    ) {
        if (straights.isEmpty()) return
        val srgb = color.toSRGB()
        straights.forEach { straight ->
            val center = ifcPointToEngine(straight.point)
            val direction = normalized(ifcVectorToEngine(straight.direction))
            val halfOffset = Vector3d(direction).apply { scale(segmentLength * 0.5) }

            val start = Point3d(center).apply { sub(halfOffset) }
            val end = Point3d(center).apply { add(halfOffset) }

            addLineSegment(start, end)
            addSphere(center, referencePointSize, srgb)
        }
    }

    fun addPlanes(
        planes: Collection<Plane>,
        halfExtent: Double = 3.0,
        referencePointSize: Double = DEFAULT_SPHERE_RADIUS * 0.6,
        color: RGB = SRGB.from255(255, 64, 192, 255),
    ) {
        if (planes.isEmpty()) return
        val srgb = color.toSRGB()
        planes.forEach { plane ->
            val origin = ifcPointToEngine(plane.point)
            val axisU = normalized(ifcVectorToEngine(plane.axisU))
            val axisV = normalized(ifcVectorToEngine(plane.axisV))
            val u = Vector3d(axisU).apply { scale(halfExtent) }
            val v = Vector3d(axisV).apply { scale(halfExtent) }

            val corners = arrayOf(
                planeCorner(origin, u, v, -1.0, -1.0),
                planeCorner(origin, u, v, 1.0, -1.0),
                planeCorner(origin, u, v, 1.0, 1.0),
                planeCorner(origin, u, v, -1.0, 1.0),
            )

            for (index in corners.indices) {
                addLineSegment(corners[index], corners[(index + 1) % corners.size])
            }

            val normal = planeNormal(Plane(origin, axisU, axisV))
            val normalEnd = Point3d(origin).apply {
                add(Vector3d(normal).apply { scale(halfExtent * 0.05) })
            }
            addLineSegment(origin, normalEnd)
            addSphere(origin, referencePointSize, srgb)
        }
    }

    /** IFC Z-up (x, y, z) → engine convention used by native bounding boxes (x, z, y). */
    private fun ifcPointToEngine(point: Point3d): Point3d =
        Point3d(point.x, point.z, point.y)

    private fun ifcVectorToEngine(vector: Vector3d): Vector3d =
        Vector3d(vector.x, vector.z, vector.y)

    private fun planeCorner(
        origin: Point3d,
        axisU: Vector3d,
        axisV: Vector3d,
        uSign: Double,
        vSign: Double,
    ): Point3d {
        val uOffset = Vector3d(axisU).apply { scale(uSign) }
        val vOffset = Vector3d(axisV).apply { scale(vSign) }
        return Point3d(origin).apply {
            add(uOffset)
            add(vOffset)
        }
    }

    private fun addLineSegment(start: Point3d, end: Point3d) {
        lineInstances.add(
            LineSegmentInstance(
                x1 = start.x.toFloat(),
                y1 = start.y.toFloat(),
                z1 = (-start.z).toFloat(),
                x2 = end.x.toFloat(),
                y2 = end.y.toFloat(),
                z2 = (-end.z).toFloat(),
            ),
        )
    }

    private fun addSphere(point: Point3d, radius: Double, color: RGB) {
        sphereInstances.add(
            SphereInstance(
                x = point.x.toFloat(),
                y = point.y.toFloat(),
                z = (-point.z).toFloat(),
                scale = radius.toFloat(),
                r = color.r,
                g = color.g,
                b = color.b,
            ),
        )
    }

    fun toGlb(): ByteArray? {
        if (isEmpty) return null
        return GltfGlbEncoder.encode(sphereInstances, boxInstances, lineInstances)
    }

    companion object {
        private const val DEFAULT_SPHERE_RADIUS = 0.25
    }
}
