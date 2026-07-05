package de.rub.bi.inf.openbimrl.utils.math

import de.rub.bi.inf.openbimrl.utils.InvalidFunctionInputException
import javax.vecmath.Point3d
import javax.vecmath.Tuple3d
import javax.vecmath.Vector3d
import kotlin.math.abs
import kotlin.math.sqrt

private const val EPSILON = 1e-9

data class GeometryMetric(val distance: Double, val isParallel: Boolean)

fun areDirectionsParallel(first: Vector3d, second: Vector3d): Boolean {
    val cross = Vector3d().apply { cross(normalized(first), normalized(second)) }
    return cross.lengthSquared() <= EPSILON * EPSILON
}

fun straightStraightMetric(first: Straight, second: Straight): GeometryMetric {
    val isParallel = areDirectionsParallel(first.direction, second.direction)
    val distance = if (isParallel) {
        pointStraightDistance(second.point, first)
    } else {
        0.0
    }
    return GeometryMetric(distance, isParallel)
}

fun planePlaneMetric(first: Plane, second: Plane): GeometryMetric {
    val normalA = planeNormal(first)
    val normalB = planeNormal(second)
    val isParallel = areDirectionsParallel(normalA, normalB)
    val distance = if (isParallel) {
        val alignedNormal = if (normalA.dot(normalB) < 0.0) Vector3d(normalB).apply { negate() } else normalB
        val offset = Vector3d().apply { sub(second.point, first.point) }
        abs(normalA.dot(offset))
    } else {
        0.0
    }
    return GeometryMetric(distance, isParallel)
}

fun pointPointDistance(first: Point3d, second: Point3d): Double = first.distance(second)

fun pointStraightDistance(point: Point3d, straight: Straight): Double {
    val direction = normalized(straight.direction)
    val offset = Vector3d().apply { sub(point, straight.point) }
    val projection = direction.dot(offset)
    val projected = Vector3d(direction).apply { scale(projection) }
    offset.sub(projected)
    return offset.length()
}

fun pointPlaneDistance(point: Point3d, plane: Plane): Double {
    val normal = planeNormal(plane)
    val offset = Vector3d().apply { sub(point, plane.point) }
    return abs(normal.dot(offset))
}

fun straightStraightDistance(first: Straight, second: Straight): Double {
    val directionA = normalized(first.direction)
    val directionB = normalized(second.direction)
    val cross = Vector3d().apply { cross(directionA, directionB) }
    val crossLength = cross.length()

    if (crossLength <= EPSILON) {
        return pointStraightDistance(second.point, first)
    }

    val offset = Vector3d().apply { sub(second.point, first.point) }
    return abs(offset.dot(cross)) / crossLength
}

fun straightPlaneDistance(straight: Straight, plane: Plane): Double {
    val direction = normalized(straight.direction)
    val normal = planeNormal(plane)

    if (abs(direction.dot(normal)) <= EPSILON) {
        return pointPlaneDistance(straight.point, plane)
    }

    return 0.0
}

fun planePlaneDistance(first: Plane, second: Plane): Double {
    val normalA = planeNormal(first)
    val normalB = planeNormal(second)
    val cross = Vector3d().apply { cross(normalA, normalB) }

    if (cross.length() > EPSILON) {
        return 0.0
    }

    val alignedNormal = if (normalA.dot(normalB) < 0.0) Vector3d(normalB).apply { negate() } else normalB
    val offset = Vector3d().apply { sub(second.point, first.point) }
    return abs(normalA.dot(offset))
}

fun distanceBetween(first: Any, second: Any): Double = when {
    first is Point3d && second is Point3d -> pointPointDistance(first, second)
    first is Point3d && second is Straight -> pointStraightDistance(first, second)
    first is Straight && second is Point3d -> pointStraightDistance(second, first)
    first is Point3d && second is Plane -> pointPlaneDistance(first, second)
    first is Plane && second is Point3d -> pointPlaneDistance(second, first)
    first is Straight && second is Straight -> straightStraightDistance(first, second)
    first is Straight && second is Plane -> straightPlaneDistance(first, second)
    first is Plane && second is Straight -> straightPlaneDistance(second, first)
    first is Plane && second is Plane -> planePlaneDistance(first, second)
    else -> throw InvalidFunctionInputException(
        "Unsupported geometry pair for distance: ${first.javaClass.simpleName} and ${second.javaClass.simpleName}. " +
            "Expected combinations of Point3d, Straight, and Plane.",
    )
}

private fun Tuple3d.lengthSquared(): Double = x * x + y * y + z * z
