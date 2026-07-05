package de.rub.bi.inf.openbimrl.utils.ifc

import de.rub.bi.inf.openbimrl.utils.math.planePlaneMetric
import de.rub.bi.inf.openbimrl.utils.math.straightStraightMetric
import de.rub.bi.inf.openbimrl.utils.pathfinding.IfcTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assumptions.assumeTrue
import javax.vecmath.Point3d
import javax.vecmath.Vector3d
import java.nio.file.Paths

class ElementApproximationServiceTest {

    companion object {
        private const val LEFT_RAIL_GUID = "1O2Fr\$t4X7Zf8NOew3FLOH"
        private const val RIGHT_RAIL_GUID = "0O2Fr\$t4X7Zf8NOew3FLOH"
        private const val WALL_A_GUID = "3O2Fr\$t4X7Zf8NOew3FLOH"
        private const val WALL_B_GUID = "4O2Fr\$t4X7Zf8NOew3FLOH"

        private var nativeAvailable = false

        @BeforeAll
        @JvmStatic
        fun loadNative() {
            nativeAvailable = try {
                IfcTestHelper.loadNativeLibrary()
                true
            } catch (_: UnsatisfiedLinkError) {
                false
            }
        }
    }

    private fun cppTestResource(fileName: String): String =
        Paths.get("src", "main", "cpp", "test", "resources", fileName)
            .toFile()
            .absolutePath

    @Test
    fun `manual straight metric matches expected gauge`() {
        val first = de.rub.bi.inf.openbimrl.utils.math.Straight(
            Point3d(0.0, 0.0, 0.0),
            Vector3d(1.0, 0.0, 0.0),
        )
        val second = de.rub.bi.inf.openbimrl.utils.math.Straight(
            Point3d(0.0, 1.435, 0.0),
            Vector3d(1.0, 0.0, 0.0),
        )

        val metric = straightStraightMetric(first, second)
        assertTrue(metric.isParallel)
        assertEquals(1.435, metric.distance, 1e-9)
    }

    @Test
    fun `manual plane metric matches expected spacing`() {
        val first = de.rub.bi.inf.openbimrl.utils.math.Plane(
            Point3d(0.0, 0.0, 0.0),
            Vector3d(1.0, 0.0, 0.0),
            Vector3d(0.0, 1.0, 0.0),
        )
        val second = de.rub.bi.inf.openbimrl.utils.math.Plane(
            Point3d(0.0, 0.0, 4.0),
            Vector3d(1.0, 0.0, 0.0),
            Vector3d(0.0, 1.0, 0.0),
        )

        val metric = planePlaneMetric(first, second)
        assertTrue(metric.isParallel)
        assertEquals(4.0, metric.distance, 1e-9)
    }

    @Test
    fun `rail placement approximation yields standard gauge`() {
        assumeTrue(nativeAvailable, "Native library unavailable in this environment")
        assumeTrue(IfcTestHelper.loadIfc(cppTestResource("rails_parallel_gauge.ifc")))

        val leftRail = IfcTestHelper.getElementByGuid(LEFT_RAIL_GUID)
        val rightRail = IfcTestHelper.getElementByGuid(RIGHT_RAIL_GUID)
        requireNotNull(leftRail)
        requireNotNull(rightRail)

        val leftStraight = ElementApproximationService.toStraight(leftRail)
        val rightStraight = ElementApproximationService.toStraight(rightRail)

        assertEquals(ApproximationSource.PLACEMENT, leftStraight.source)
        assertEquals(ApproximationSource.PLACEMENT, rightStraight.source)

        val metric = straightStraightMetric(leftStraight.value, rightStraight.value)
        assertTrue(metric.isParallel)
        assertEquals(1.435, metric.distance, 0.01)
    }

    @Test
    fun `wall approximation yields parallel spacing`() {
        assumeTrue(nativeAvailable, "Native library unavailable in this environment")
        assumeTrue(IfcTestHelper.loadIfc(cppTestResource("walls_parallel.ifc")))

        val wallA = IfcTestHelper.getElementByGuid(WALL_A_GUID)
        val wallB = IfcTestHelper.getElementByGuid(WALL_B_GUID)
        requireNotNull(wallA)
        requireNotNull(wallB)

        val planeA = ElementApproximationService.toPlane(wallA)
        val planeB = ElementApproximationService.toPlane(wallB)

        assertTrue(
            planeA.source == ApproximationSource.PLACEMENT ||
                planeA.source == ApproximationSource.REPRESENTATION,
        )

        val metric = planePlaneMetric(planeA.value, planeB.value)
        assertTrue(metric.isParallel)
        assertEquals(4.0, metric.distance, 0.05)
    }
}
