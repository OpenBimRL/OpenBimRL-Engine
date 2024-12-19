package de.rub.bi.inf.openbimrl.functions.visual

import arrow.core.Either
import com.github.ajalt.colormath.model.RGB
import de.rub.bi.inf.extensions.RED
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.DisplayableFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import javax.media.j3d.Bounds

@OpenBIMRLFunction
class VisualizeBounds(nodeProxy: NodeProxy) : DisplayableFunction(nodeProxy) {
    @FunctionInput(0, Bounds::class)
    lateinit var bounds: List<Bounds>

    override fun execute() {
        logGraphically(bounds.map {
            Pair(it, mapOf("color" to Either.Right(RGB.RED())))
        })
    }
}