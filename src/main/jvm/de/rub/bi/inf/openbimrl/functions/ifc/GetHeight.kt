package de.rub.bi.inf.openbimrl.functions.ifc

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

/**
 * Calculates the height given a list of entities.
 *
 * @author Marcel Stepien
 */
class GetHeight(nodeProxy: NodeProxy?) : AbstractFunction(nodeProxy!!) {
    override fun execute() {
        val input0 = getInputAsCollection(0)

        val resultValues0 = ArrayList<Double>()
        val resultValues1 = ArrayList<Double>()

        input0.filterIsInstance<BoundingBox>().forEach { element ->
            resultValues0.add(Point3d().apply { element.getLower(this) }.z)
            resultValues1.add(Point3d().apply { element.getUpper(this) }.z)
        }

        // 2nd comparison not needed due that they always get added by the two of them
        if (resultValues0.size == 1 /* && resultValues1.size == 1 */) {
            setResult(0, resultValues0[0])
            setResult(1, resultValues1[0])
        } else {
            setResult(0, resultValues0)
            setResult(1, resultValues1)
        }
    }
}
