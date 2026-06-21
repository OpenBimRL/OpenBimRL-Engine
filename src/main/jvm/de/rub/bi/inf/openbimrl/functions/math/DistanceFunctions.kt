package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import de.rub.bi.inf.openbimrl.utils.math.Plane
import de.rub.bi.inf.openbimrl.utils.math.Straight
import de.rub.bi.inf.openbimrl.utils.math.planePlaneDistance
import de.rub.bi.inf.openbimrl.utils.math.pointPlaneDistance
import de.rub.bi.inf.openbimrl.utils.math.pointPointDistance
import de.rub.bi.inf.openbimrl.utils.math.pointStraightDistance
import de.rub.bi.inf.openbimrl.utils.math.straightPlaneDistance
import de.rub.bi.inf.openbimrl.utils.math.straightStraightDistance
import javax.vecmath.Point3d

@OpenBIMRLFunction(
    name = "distance",
    description = "Calculates the distance between two geometric objects. Supports Point3d, Straight, and Plane inputs.",
    inputs = [
        FunctionPort(0, "Geometry A"),
        FunctionPort(1, "Geometry B"),
    ],
    outputs = [
        FunctionPort(0, "Distance", Double::class),
    ],
)
class Distance(nodeProxy: NodeProxy) : PairwiseDistanceFunction(nodeProxy)

@OpenBIMRLFunction(
    name = "distancePointPoint",
    description = "Calculates the Euclidean distance between two points.",
    inputs = [
        FunctionPort(0, "Point A", Point3d::class),
        FunctionPort(1, "Point B", Point3d::class),
    ],
    outputs = [
        FunctionPort(0, "Distance", Double::class),
    ],
)
class DistancePointPoint(nodeProxy: NodeProxy) : PairwiseDistanceFunction(nodeProxy) {
    override fun distance(first: Any, second: Any) = pointPointDistance(first as Point3d, second as Point3d)
}

@OpenBIMRLFunction(
    name = "distancePointStraight",
    description = "Calculates the shortest distance from a point to a straight line.",
    inputs = [
        FunctionPort(0, "Point", Point3d::class),
        FunctionPort(1, "Straight", Straight::class),
    ],
    outputs = [
        FunctionPort(0, "Distance", Double::class),
    ],
)
class DistancePointStraight(nodeProxy: NodeProxy) : PairwiseDistanceFunction(nodeProxy) {
    override fun distance(first: Any, second: Any) = pointStraightDistance(first as Point3d, second as Straight)
}

@OpenBIMRLFunction(
    name = "distancePointPlane",
    description = "Calculates the shortest distance from a point to a plane.",
    inputs = [
        FunctionPort(0, "Point", Point3d::class),
        FunctionPort(1, "Plane", Plane::class),
    ],
    outputs = [
        FunctionPort(0, "Distance", Double::class),
    ],
)
class DistancePointPlane(nodeProxy: NodeProxy) : PairwiseDistanceFunction(nodeProxy) {
    override fun distance(first: Any, second: Any) = pointPlaneDistance(first as Point3d, second as Plane)
}

@OpenBIMRLFunction(
    name = "distanceStraightStraight",
    description = "Calculates the shortest distance between two straight lines.",
    inputs = [
        FunctionPort(0, "Straight A", Straight::class),
        FunctionPort(1, "Straight B", Straight::class),
    ],
    outputs = [
        FunctionPort(0, "Distance", Double::class),
    ],
)
class DistanceStraightStraight(nodeProxy: NodeProxy) : PairwiseDistanceFunction(nodeProxy) {
    override fun distance(first: Any, second: Any) = straightStraightDistance(first as Straight, second as Straight)
}

@OpenBIMRLFunction(
    name = "distanceStraightPlane",
    description = "Calculates the shortest distance between a straight line and a plane.",
    inputs = [
        FunctionPort(0, "Straight", Straight::class),
        FunctionPort(1, "Plane", Plane::class),
    ],
    outputs = [
        FunctionPort(0, "Distance", Double::class),
    ],
)
class DistanceStraightPlane(nodeProxy: NodeProxy) : PairwiseDistanceFunction(nodeProxy) {
    override fun distance(first: Any, second: Any) = straightPlaneDistance(first as Straight, second as Plane)
}

@OpenBIMRLFunction(
    name = "distancePlanePlane",
    description = "Calculates the shortest distance between two planes.",
    inputs = [
        FunctionPort(0, "Plane A", Plane::class),
        FunctionPort(1, "Plane B", Plane::class),
    ],
    outputs = [
        FunctionPort(0, "Distance", Double::class),
    ],
)
class DistancePlanePlane(nodeProxy: NodeProxy) : PairwiseDistanceFunction(nodeProxy) {
    override fun distance(first: Any, second: Any) = planePlaneDistance(first as Plane, second as Plane)
}
