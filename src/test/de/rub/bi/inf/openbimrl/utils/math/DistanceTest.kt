package de.rub.bi.inf.openbimrl.utils.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.vecmath.Point3d
import javax.vecmath.Vector3d

class DistanceTest {

    private fun horizontalPlane(origin: Point3d = Point3d(0.0, 0.0, 0.0)) =
        Plane(origin, Vector3d(1.0, 0.0, 0.0), Vector3d(0.0, 1.0, 0.0))

    @Test
    fun `point to point distance`() {
        assertEquals(5.0, pointPointDistance(Point3d(0.0, 0.0, 0.0), Point3d(3.0, 4.0, 0.0)), 1e-9)
    }

    @Test
    fun `point to straight distance`() {
        val line = Straight(Point3d(0.0, 0.0, 0.0), Vector3d(1.0, 0.0, 0.0))
        assertEquals(3.0, pointStraightDistance(Point3d(2.0, 3.0, 0.0), line), 1e-9)
    }

    @Test
    fun `point to plane distance`() {
        val plane = horizontalPlane()
        assertEquals(4.0, pointPlaneDistance(Point3d(1.0, 2.0, 4.0), plane), 1e-9)
    }

    @Test
    fun `parallel straight lines distance`() {
        val first = Straight(Point3d(0.0, 0.0, 0.0), Vector3d(1.0, 0.0, 0.0))
        val second = Straight(Point3d(0.0, 2.0, 0.0), Vector3d(1.0, 0.0, 0.0))
        assertEquals(2.0, straightStraightDistance(first, second), 1e-9)
    }

    @Test
    fun `skew straight lines distance`() {
        val first = Straight(Point3d(0.0, 0.0, 0.0), Vector3d(1.0, 0.0, 0.0))
        val second = Straight(Point3d(0.0, 1.0, 1.0), Vector3d(0.0, 1.0, 0.0))
        assertEquals(1.0, straightStraightDistance(first, second), 1e-9)
    }

    @Test
    fun `straight parallel to plane distance`() {
        val line = Straight(Point3d(0.0, 0.0, 2.0), Vector3d(1.0, 0.0, 0.0))
        val plane = horizontalPlane()
        assertEquals(2.0, straightPlaneDistance(line, plane), 1e-9)
    }

    @Test
    fun `straight intersecting plane distance`() {
        val line = Straight(Point3d(0.0, 0.0, 0.0), Vector3d(1.0, 0.0, 1.0))
        val plane = horizontalPlane()
        assertEquals(0.0, straightPlaneDistance(line, plane), 1e-9)
    }

    @Test
    fun `parallel planes distance`() {
        val first = horizontalPlane()
        val second = horizontalPlane(Point3d(0.0, 0.0, 5.0))
        assertEquals(5.0, planePlaneDistance(first, second), 1e-9)
    }

    @Test
    fun `intersecting planes distance`() {
        val first = Plane(Point3d(0.0, 0.0, 0.0), Vector3d(0.0, 1.0, 0.0), Vector3d(0.0, 0.0, 1.0))
        val second = Plane(Point3d(0.0, 0.0, 0.0), Vector3d(1.0, 0.0, 0.0), Vector3d(0.0, 0.0, 1.0))
        assertEquals(0.0, planePlaneDistance(first, second), 1e-9)
    }

    @Test
    fun `distanceBetween dispatches by runtime type`() {
        val point = Point3d(0.0, 0.0, 0.0)
        val line = Straight(point, Vector3d(1.0, 0.0, 0.0))
        val plane = horizontalPlane()

        assertEquals(5.0, distanceBetween(Point3d(3.0, 4.0, 0.0), point), 1e-9)
        assertEquals(2.0, distanceBetween(Point3d(0.0, 2.0, 0.0), line), 1e-9)
        assertEquals(5.0, distanceBetween(Point3d(0.0, 0.0, 5.0), plane), 1e-9)
        assertEquals(2.0, distanceBetween(line, Straight(Point3d(0.0, 2.0, 0.0), Vector3d(1.0, 0.0, 0.0))), 1e-9)
        assertEquals(2.0, distanceBetween(Straight(Point3d(0.0, 0.0, 2.0), Vector3d(1.0, 0.0, 0.0)), plane), 1e-9)
        assertEquals(5.0, distanceBetween(plane, horizontalPlane(Point3d(0.0, 0.0, 5.0))), 1e-9)
    }
}
