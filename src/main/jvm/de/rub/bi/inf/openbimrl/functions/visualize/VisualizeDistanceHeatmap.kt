package de.rub.bi.inf.openbimrl.functions.visualize

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.VisualizingFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer
import javax.vecmath.Point3d

private const val DEFAULT_POINT_SIZE = 0.25

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
