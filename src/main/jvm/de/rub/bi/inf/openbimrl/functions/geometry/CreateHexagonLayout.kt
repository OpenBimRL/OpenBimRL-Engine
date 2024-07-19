package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Orientation
import io.github.offlinebrain.khexagon.math.Point
import javax.vecmath.Point3d


/**
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
class CreateHexagonLayout(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    /**
     * expected inputs: Center: [Point3d], size: [Double]
     */
    override fun execute() {
        val center = getInputAsCollection(0).filterIsInstance<Point3d>()[0]
        val size = getInput<Any>(1).toString().toDouble().toFloat()

        val layout = Layout(Orientation.Pointy, Point(center.x.toFloat(), center.z.toFloat()), Point(size, size))
        setResult(0, layout)
    }
}
