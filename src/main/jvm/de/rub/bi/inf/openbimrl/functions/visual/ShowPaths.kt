package de.rub.bi.inf.openbimrl.functions.visual

import arrow.core.Either
import com.github.ajalt.colormath.model.RGB
import de.rub.bi.inf.extensions.GREEN
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.DisplayableFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import javax.media.j3d.BoundingSphere
import javax.vecmath.Point3d

/**
 * Loads the paths information and displays it in the 3D viewer.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
class ShowPaths(nodeProxy: NodeProxy) : DisplayableFunction(nodeProxy) {
    @FunctionInput(0)
    lateinit var path: List<Point3d>

    @FunctionInput(1)
    var radius = 1.0

    override fun execute() {
        logGraphically(path.map {
            Pair(BoundingSphere(it, radius), mapOf("color" to Either.Right(RGB.GREEN())))
        })
    }
}
