package de.rub.bi.inf.openbimrl.functions.visualize

import com.github.ajalt.colormath.model.SRGB
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.VisualizingFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer

private const val DEFAULT_IFC_POINT_SIZE = 0.5

@OpenBIMRLFunction(
    packageName = "visualize",
    name = "ifcElements",
    description = "Highlights IFC elements in the viewer mesh.",
    isVisualizer = true,
    type = "visualizeType",
)
class VisualizeIfcElements(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy), VisualizingFunction {

    @FunctionInput(0, IfcPointer::class)
    lateinit var elements: Collection<IfcPointer>

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
