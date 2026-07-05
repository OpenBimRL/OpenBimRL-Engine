package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.utils.math.Straight
import de.rub.bi.inf.openbimrl.utils.math.planePlaneMetric
import de.rub.bi.inf.openbimrl.utils.math.straightStraightMetric

@OpenBIMRLFunction(
    name = "straightStraightMetric",
    description = "Distance and parallelism between two straight lines. IsParallel distinguishes coincident from intersecting skew lines when distance is zero.",
)
class StraightStraightMetric(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    @FunctionInput(0, name = "Straight A")
    lateinit var straightA: Straight

    @FunctionInput(1, name = "Straight B")
    lateinit var straightB: Straight

    @FunctionOutput(0, name = "Distance")
    var distance: Double? = null

    @FunctionOutput(1, name = "IsParallel")
    var isParallel: Boolean? = null

    override fun execute() {
        val metric = straightStraightMetric(straightA, straightB)
        distance = metric.distance
        isParallel = metric.isParallel
    }
}

@OpenBIMRLFunction(
    name = "planePlaneMetric",
    description = "Distance and parallelism between two planes. IsParallel distinguishes coincident from intersecting planes when distance is zero.",
)
class PlanePlaneMetric(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    @FunctionInput(0, name = "Plane A")
    lateinit var planeA: Plane

    @FunctionInput(1, name = "Plane B")
    lateinit var planeB: Plane

    @FunctionOutput(0, name = "Distance")
    var distance: Double? = null

    @FunctionOutput(1, name = "IsParallel")
    var isParallel: Boolean? = null

    override fun execute() {
        val metric = planePlaneMetric(planeA, planeB)
        distance = metric.distance
        isParallel = metric.isParallel
    }
}
