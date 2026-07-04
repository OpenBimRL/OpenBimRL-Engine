package de.rub.bi.inf.openbimrl.pathfinding

import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.openbimrl.utils.pathfinding.IfcTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PathfindingMinimalIfcTest {

    @BeforeAll
    fun loadModel() {
        IfcTestHelper.loadNativeLibrary()
        assertTrue(
            IfcTestHelper.loadPathfindingMinimalIfc(),
            "Failed to load ${IfcTestHelper.pathfindingMinimalIfcPath()}",
        )
    }

    @Test
    fun `filterByElement finds six walls`() {
        assertEquals(6, IfcTestHelper.filterByElement("IfcWall").size)
    }

    @Test
    fun `filterByElement finds two doors`() {
        val doors = IfcTestHelper.filterByElement("IfcDoor")
        assertEquals(2, doors.size)
        assertEquals(setOf(IfcTestHelper.DOOR_1_GUID, IfcTestHelper.DOOR_2_GUID), doors.map { it.guid }.toSet())
    }

    @Test
    fun `filterByElement finds two openings`() {
        assertEquals(2, IfcTestHelper.filterByElement("IfcOpeningElement").size)
    }

    @Test
    fun `filterByElement finds no columns in minimal model`() {
        assertTrue(IfcTestHelper.filterByElement("IfcColumn").isEmpty())
    }

    @Test
    fun `getElementByGuid resolves door one`() {
        val door = IfcTestHelper.getElementByGuid(IfcTestHelper.DOOR_1_GUID)

        assertNotNull(door)
        assertEquals(IfcTestHelper.DOOR_1_GUID, door!!.guid)
        assertEquals("IfcDoor", door.type)
    }

    @Test
    fun `getElementByGuid returns null for unknown guid`() {
        assertEquals(null, IfcTestHelper.getElementByGuid("0PathTest000000009999"))
    }

    @Test
    fun `building bounds span the minimal footprint`() {
        val (center, bounds) = IfcTestHelper.calculateBuildingBounds()
        val lower = bounds.lower()
        val upper = bounds.upper()

        assertTrue(upper.x - lower.x > 5.0)
        assertTrue(upper.z - lower.z > 3.0)
        assertTrue(center.x in 3.0..7.0)
        assertTrue(center.z in 1.0..5.0)
    }
}
