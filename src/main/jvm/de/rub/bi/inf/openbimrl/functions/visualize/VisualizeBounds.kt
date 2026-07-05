package de.rub.bi.inf.openbimrl.functions.visualize

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.VisualizingFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer
import javax.media.j3d.BoundingBox

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
        composer?.addBounds(bounds.filterIsInstance<BoundingBox>(), pad)
    }
}
