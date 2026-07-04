package de.rub.bi.inf.openbimrl.visualization

import com.github.ajalt.colormath.model.Oklab
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.SRGB
import com.github.ajalt.colormath.transform.interpolator
import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.utils.math.lerp
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
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

/**
 * Accumulates check visuals and encodes them as a binary GLB (glTF 2.0).
 * Positions are stored in Three.js / viewer space (Z negated from IFC/engine space).
 * The glTF document is built with [kmp.gltf.model] and packed into GLB.
 */
class GltfVisualComposer {

    private val sphereInstances = mutableListOf<SphereInstance>()
    private val boxInstances = mutableListOf<BoxInstance>()

    val isEmpty: Boolean
        get() = sphereInstances.isEmpty() && boxInstances.isEmpty()

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
        return GltfGlbEncoder.encode(sphereInstances, boxInstances)
    }

    companion object {
        private const val DEFAULT_SPHERE_RADIUS = 0.25
    }
}
