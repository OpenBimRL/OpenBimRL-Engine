package de.rub.bi.inf.openbimrl.functions.visualize

import com.github.ajalt.colormath.model.SRGB
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.VisualizingFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer

private const val DEFAULT_PLANE_HALF_EXTENT = 3.0
private const val DEFAULT_REFERENCE_POINT_SIZE = 0.15

@OpenBIMRLFunction(
    packageName = "visualize",
    name = "planes",
    description = "Visualizes plane approximations as a wireframe patch with a reference point marker.",
    isVisualizer = true,
    type = "visualizeType",
)
class VisualizePlanes(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy), VisualizingFunction {

    @FunctionInput(0, Plane::class)
    lateinit var planes: Collection<Plane>

    @FunctionInput(1, nullable = true)
    var halfExtent: String? = null

    @FunctionInput(2, nullable = true)
    var referencePointSize: String? = null

    @FunctionInput(3, nullable = true)
    var color: String? = null

    private var composer: GltfVisualComposer? = null

    override fun setComposer(composer: GltfVisualComposer) {
        this.composer = composer
    }

    override fun execute() {
        composer?.addPlanes(
            planes,
            parseOptionalDouble(halfExtent, DEFAULT_PLANE_HALF_EXTENT),
            parseOptionalDouble(referencePointSize, DEFAULT_REFERENCE_POINT_SIZE),
            parseColor(color, SRGB.from255(255, 64, 192, 255)),
        )
    }
}
