package de.rub.bi.inf.openbimrl.utils.ifc

import de.rub.bi.inf.openbimrl.utils.pathfinding.IfcTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Manual/regression probe for the georeferenced SOM bridge model (gitignored locally).
 * Ensures ObjectPlacement frames land on the same CRS point as native bboxes so
 * visualize.straights overlays align with the IFC mesh.
 */
class SomStraightProbeTest {
    companion object {
        private const val GUID = "31FqyXpT1D0vOUzb7b6vg5"
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

    @Test
    fun `beam placement frame matches bbox center in world CRS`() {
        assumeTrue(nativeAvailable, "Native library unavailable")
        val ifcFile =
            Paths.get("src", "test", "resources", "300_IB_BW_EÜ Brückentor_SOM.ifc").toFile()
        assumeTrue(ifcFile.isFile, "SOM IFC not present (local-only fixture)")
        assumeTrue(IfcTestHelper.loadIfc(ifcFile.absolutePath))

        val element = IfcTestHelper.getElementByGuid(GUID)
        requireNotNull(element)

        val (bboxCenterEngine, _) = NativeIfcGeometry.fetchBoundingBox(element)!!
        val frame = NativeIfcGeometry.fetchElementFrame(element)
        requireNotNull(frame)
        assertEquals(ApproximationSource.PLACEMENT, frame.source)

        // Frame is IFC XYZ; native bbox center is engine XZY (x, z, y).
        val frameEngineX = frame.point.x
        val frameEngineY = frame.point.z
        val frameEngineZ = frame.point.y

        val delta =
            hypot(
                hypot(frameEngineX - bboxCenterEngine.x, frameEngineY - bboxCenterEngine.y),
                frameEngineZ - bboxCenterEngine.z,
            )
        assertTrue(
            delta < 20.0,
            "Placement frame should sit near bbox center; delta=$delta m " +
                "frameEngine=($frameEngineX,$frameEngineY,$frameEngineZ) bbox=$bboxCenterEngine",
        )
        // Must be in the positive CRS octant (inverted OCC transform produced -site).
        assertTrue(frame.point.x > 1_000_000.0, "Expected positive easting, got ${frame.point.x}")
        assertTrue(frame.point.y > 1_000_000.0, "Expected positive northing, got ${frame.point.y}")
        assertTrue(abs(frame.point.x + bboxCenterEngine.x) > 1_000_000.0)
    }
}
