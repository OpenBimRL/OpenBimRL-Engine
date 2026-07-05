package de.rub.bi.inf.openbimrl.functions.visualize

import com.github.ajalt.colormath.model.SRGB
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.VisualizingFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer
import javax.vecmath.Point3d

private const val DEFAULT_POINTS_POINT_SIZE = 1.0

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
