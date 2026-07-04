package de.rub.bi.inf.openbimrl.functions.visualize

import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.SRGB
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.VisualizingFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer
import javax.vecmath.Point3d

private const val DEFAULT_POINT_SIZE = 0.25
private const val DEFAULT_POINTS_POINT_SIZE = 1.0
private const val DEFAULT_IFC_POINT_SIZE = 0.5

@OpenBIMRLFunction(
    packageName = "visualize",
    name = "distanceHeatmap",
    description = "Visualizes parallel point/distance arrays as a colored heatmap mesh.",
    isVisualizer = true,
    type = "visualizeType",
)
class VisualizeDistanceHeatmap(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy), VisualizingFunction {

    @FunctionInput(0, Point3d::class)
    lateinit var points: Collection<Point3d>

    @FunctionInput(1, Double::class)
    lateinit var distances: Collection<Double>

    @FunctionInput(2, nullable = true)
    var maxDistance: String? = null

    @FunctionInput(3, nullable = true)
    var pointSize: String? = null

    private var composer: GltfVisualComposer? = null

    override fun setComposer(composer: GltfVisualComposer) {
        this.composer = composer
    }

    override fun execute() {
        val max = maxDistance?.toDoubleOrNull() ?: 100.0
        val size = parseOptionalDouble(pointSize, DEFAULT_POINT_SIZE)
        composer?.addDistanceHeatmap(points, distances, max, size)
    }
}

@OpenBIMRLFunction(
    packageName = "visualize",
    name = "points",
    description = "Visualizes points as colored spheres.",
    isVisualizer = true,
    type = "visualizeType",
)
class VisualizePoints(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy), VisualizingFunction {

    @FunctionInput(0, Point3d::class)
    lateinit var points: Collection<Point3d>

    @FunctionInput(1, nullable = true)
    var pointSize: String? = null

    @FunctionInput(2, nullable = true)
    var color: String? = null

    private var composer: GltfVisualComposer? = null

    override fun setComposer(composer: GltfVisualComposer) {
        this.composer = composer
    }

    override fun execute() {
        val size = parseOptionalDouble(pointSize, DEFAULT_POINTS_POINT_SIZE)
        composer?.addPoints(points, size, parseColor(color, SRGB.from255(0, 255, 0, 255)))
    }
}

@OpenBIMRLFunction(
    packageName = "visualize",
    name = "bounds",
    description = "Visualizes bounding boxes as wireframe boxes.",
    isVisualizer = true,
    type = "visualizeType",
)
class VisualizeBounds(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy), VisualizingFunction {

    @FunctionInput(0, javax.media.j3d.Bounds::class)
    lateinit var bounds: Collection<javax.media.j3d.Bounds>

    @FunctionInput(1, nullable = true)
    var padding: String? = null

    private var composer: GltfVisualComposer? = null

    override fun setComposer(composer: GltfVisualComposer) {
        this.composer = composer
    }

    override fun execute() {
        val pad = parseOptionalDouble(padding, 0.0)
        composer?.addBounds(bounds.filterIsInstance<javax.media.j3d.BoundingBox>(), pad)
    }
}

@OpenBIMRLFunction(
    packageName = "visualize",
    name = "ifcElements",
    description = "Highlights IFC elements in the viewer mesh.",
    isVisualizer = true,
    type = "visualizeType",
)
class VisualizeIfcElements(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy), VisualizingFunction {

    @FunctionInput(0, de.rub.bi.inf.nativelib.IfcPointer::class)
    lateinit var elements: Collection<de.rub.bi.inf.nativelib.IfcPointer>

    @FunctionInput(1, nullable = true)
    var color: String? = null

    @FunctionInput(2, nullable = true)
    var pointSize: String? = null

    private var composer: GltfVisualComposer? = null

    override fun setComposer(composer: GltfVisualComposer) {
        this.composer = composer
    }

    override fun execute() {
        val size = parseOptionalDouble(pointSize, DEFAULT_IFC_POINT_SIZE)
        composer?.addIfcElements(elements, parseColor(color, SRGB.from255(255, 165, 0, 255)), size)
    }
}

private fun parseOptionalDouble(value: String?, default: Double): Double =
    value?.trim()?.toDoubleOrNull()?.takeIf { it > 0.0 } ?: default

private fun parseColor(value: String?, fallback: RGB): RGB {
    if (value.isNullOrBlank()) return fallback
    val trimmed = value.trim()
    return try {
        if (trimmed.startsWith("#")) {
            RGB(trimmed)
        } else {
            RGB("#$trimmed")
        }
    } catch (_: Exception) {
        try {
            val intVal = trimmed.toInt()
            val r = (intVal shr 16) and 0xFF
            val g = (intVal shr 8) and 0xFF
            val b = intVal and 0xFF
            SRGB.from255(r, g, b, 255)
        } catch (_: Exception) {
            fallback
        }
    }
}
