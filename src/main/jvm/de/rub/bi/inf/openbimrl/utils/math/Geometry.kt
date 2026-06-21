package de.rub.bi.inf.openbimrl.utils.math

import javax.vecmath.Point3d
import javax.vecmath.Vector3d

private const val EPSILON = 1e-9

/**
 * Infinite straight line defined by a point and a non-zero direction vector.
 */
data class Straight(val point: Point3d, val direction: Vector3d) {
    init {
        require(direction.lengthSquared() > 0.0) { "Straight direction must be non-zero" }
    }
}

/**
 * Infinite plane defined by a point on the plane and two non-parallel in-plane axis vectors.
 * The plane normal is derived as axisU × axisV.
 */
data class Plane(
    val point: Point3d,
    val axisU: Vector3d,
    val axisV: Vector3d,
) {
    init {
        require(axisU.lengthSquared() > 0.0) { "Plane axisU must be non-zero" }
        require(axisV.lengthSquared() > 0.0) { "Plane axisV must be non-zero" }
        val cross = Vector3d().apply { cross(axisU, axisV) }
        require(cross.lengthSquared() > EPSILON) { "Plane axisU and axisV must not be parallel" }
    }
}

fun normalized(vector: Vector3d): Vector3d =
    Vector3d(vector).apply { normalize() }

fun planeNormal(plane: Plane): Vector3d =
    normalized(Vector3d().apply { cross(plane.axisU, plane.axisV) })

private fun Vector3d.lengthSquared(): Double = x * x + y * y + z * z
