package de.rub.bi.inf.openbimrl.functions

import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer

interface VisualizingFunction {
    fun setComposer(composer: GltfVisualComposer)
}
