package de.rub.bi.inf.openbimrl.functions.visualize

import com.github.ajalt.colormath.model.SRGB
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.VisualizingFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.math.Straight
import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer

private const val DEFAULT_SEGMENT_LENGTH = 10.0
private const val DEFAULT_REFERENCE_POINT_SIZE = 0.15

@OpenBIMRLFunction(
    packageName = "visualize",
    name = "straights",
    description = "Visualizes straight line approximations as a segment with a reference point marker.",
    isVisualizer = true,
    type = "visualizeType",
)
class VisualizeStraights(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy), VisualizingFunction {

    @FunctionInput(0, Straight::class)
    lateinit var straights: Collection<Straight>

    @FunctionInput(1, nullable = true)
    var segmentLength: String? = null

    @FunctionInput(2, nullable = true)
    var referencePointSize: String? = null

    @FunctionInput(3, nullable = true)
    var color: String? = null

    private var composer: GltfVisualComposer? = null

    override fun setComposer(composer: GltfVisualComposer) {
        this.composer = composer
    }

    override fun execute() {
        composer?.addStraights(
            straights,
            parseOptionalDouble(segmentLength, DEFAULT_SEGMENT_LENGTH),
            parseOptionalDouble(referencePointSize, DEFAULT_REFERENCE_POINT_SIZE),
            parseColor(color, SRGB.from255(0, 128, 255, 255)),
        )
    }
}
