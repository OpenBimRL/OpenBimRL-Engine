package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import javax.media.j3d.BoundingBox

/**
 * Checks if geometries of two BoundingBoxes are intersecting.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
class CheckIntersection(nodeProxy: NodeProxy?) : AbstractFunction(nodeProxy) {
    override fun execute() {
        val input1 = getInput<Any>(0)
        val input2 = getInput<Any>(1)

        val result: Any? = when {
            input1 is Collection<*> && input2 is Collection<*> -> input1.filterIsInstance<BoundingBox>().map {
                intersectAny(it, input2.filterIsInstance<BoundingBox>())
            }

            // if input2 is not a collection
            input1 is Collection<*> && input2 is BoundingBox -> intersectAny(
                input2,
                input1.filterIsInstance<BoundingBox>()
            )
            // if input1 is not a collection
            input1 is BoundingBox && input2 is Collection<*> -> intersectAny(
                input1,
                input2.filterIsInstance<BoundingBox>()
            )
            // none are collections
            input1 is BoundingBox && input2 is BoundingBox -> input1.intersect(input2)
            else -> null
        }

        setResult(0, result)
    }

    private fun intersectAny(boundingBox: BoundingBox, targets: Collection<BoundingBox>): List<Boolean> {
        return targets.map { it.intersect(boundingBox) }
    }


}
