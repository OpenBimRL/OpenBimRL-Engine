package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import java.util.stream.Collectors
import javax.media.j3d.BoundingBox

/**
 * Checks if geometries of two BoundingBoxes are intersecting.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
class CheckIntersection(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    override fun execute() {
        val input1 = getInput<Any>(0)
        val input2 = getInput<Any>(1)

        val result: Any? = when {
            input1 is Collection<*> && input2 is Collection<*> ->
                input1
                    .filterIsInstance<BoundingBox>()
                    .parallelStream()
                    .map {
                        intersectAny(it, input2.filterIsInstance<BoundingBox>())
                    }
                    .collect(Collectors.toList()) // THIS. IS. FAST.

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
        if (targets.size > 100)
            return targets.parallelStream().map { it.intersect(boundingBox) }.collect(Collectors.toList())
        return targets.map { it.intersect(boundingBox) }
    }


}
