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

/**
 * Absolute Three.js Y-up position (double). Quantized to float only after subtracting [origin].
 */
internal data class SphereInstance(
    val x: Double,
    val y: Double,
    val z: Double,
    val scale: Double,
    val r: Float,
    val g: Float,
    val b: Float,
)

internal data class BoxInstance(
    val centerX: Double,
    val centerY: Double,
    val centerZ: Double,
    val sizeX: Double,
    val sizeY: Double,
    val sizeZ: Double,
)

internal data class LineSegmentInstance(
    val x1: Double,
    val y1: Double,
    val z1: Double,
    val x2: Double,
    val y2: Double,
    val z2: Double,
)

/**
 * Accumulates check visuals and encodes them as a binary GLB (glTF 2.0).
 *
 * Input geometry from IFC uses Z-up world coordinates (x, y, z).
 * Internally this matches native bounding boxes: engine (x, z, y).
 * GLB output uses Three.js Y-up viewer space: (engine.x, engine.y, -engine.z).
 *
 * Large CRS coordinates are stored in double until encode time, then written
 * relative to a local origin so float32 meshes stay precise (avoids collapsed
 * line segments and flicker far from the world origin).
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
            // Native bbox is already engine XZY; only negate Z for Three.js.
            boxInstances.add(
                BoxInstance(
                    centerX = (lower.x + upper.x) * 0.5,
                    centerY = (lower.y + upper.y) * 0.5,
                    centerZ = -(lower.z + upper.z) * 0.5,
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
            val rawDirection = ifcVectorToEngine(straight.direction)
            val directionLength = rawDirection.length()
            val direction = normalized(rawDirection)
            // planePlaneDebugStraight encodes separation in |direction|; an oversized
            // SegmentLength would dominate the camera frustum and hide the gap.
            val effectiveLength =
                if (directionLength > 1.0 + 1e-6 && segmentLength > directionLength * 4.0) {
                    directionLength * 2.0
                } else {
                    segmentLength
                }
            val halfOffset = Vector3d(direction).apply { scale(effectiveLength * 0.5) }

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
                x1 = start.x,
                y1 = start.y,
                z1 = -start.z,
                x2 = end.x,
                y2 = end.y,
                z2 = -end.z,
            ),
        )
    }

    private fun addSphere(point: Point3d, radius: Double, color: RGB) {
        sphereInstances.add(
            SphereInstance(
                x = point.x,
                y = point.y,
                z = -point.z,
                scale = radius,
                r = color.r,
                g = color.g,
                b = color.b,
            ),
        )
    }

    fun toGlb(): ByteArray? {
        if (isEmpty) return null
        val origin = computeLocalOrigin()
        return GltfGlbEncoder.encode(
            localizeSpheres(origin),
            localizeBoxes(origin),
            localizeLines(origin),
            originX = origin[0],
            originY = origin[1],
            originZ = origin[2],
        )
    }

    private fun computeLocalOrigin(): DoubleArray {
        var sx = 0.0
        var sy = 0.0
        var sz = 0.0
        var count = 0
        sphereInstances.forEach {
            sx += it.x
            sy += it.y
            sz += it.z
            count++
        }
        boxInstances.forEach {
            sx += it.centerX
            sy += it.centerY
            sz += it.centerZ
            count++
        }
        lineInstances.forEach {
            sx += (it.x1 + it.x2) * 0.5
            sy += (it.y1 + it.y2) * 0.5
            sz += (it.z1 + it.z2) * 0.5
            count++
        }
        if (count == 0) return doubleArrayOf(0.0, 0.0, 0.0)
        return doubleArrayOf(sx / count, sy / count, sz / count)
    }

    private fun localizeSpheres(origin: DoubleArray): List<SphereInstance> =
        sphereInstances.map {
            it.copy(x = it.x - origin[0], y = it.y - origin[1], z = it.z - origin[2])
        }

    private fun localizeBoxes(origin: DoubleArray): List<BoxInstance> =
        boxInstances.map {
            it.copy(
                centerX = it.centerX - origin[0],
                centerY = it.centerY - origin[1],
                centerZ = it.centerZ - origin[2],
            )
        }

    private fun localizeLines(origin: DoubleArray): List<LineSegmentInstance> =
        lineInstances.map {
            LineSegmentInstance(
                x1 = it.x1 - origin[0],
                y1 = it.y1 - origin[1],
                z1 = it.z1 - origin[2],
                x2 = it.x2 - origin[0],
                y2 = it.y2 - origin[1],
                z2 = it.z2 - origin[2],
            )
        }

    companion object {
        private const val DEFAULT_SPHERE_RADIUS = 0.25
    }
}
