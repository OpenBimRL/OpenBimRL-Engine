package de.rub.bi.inf.nativelib

import de.rub.bi.inf.openbimrl.utils.pathfinding.IfcTestHelper
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NativeEngineSmokeTest {

    @BeforeAll
    fun loadModel() {
        NativeEngine.loadNative()
        assertTrue(
            IfcTestHelper.loadPathfindingMinimalIfc(),
            "Failed to load ${IfcTestHelper.pathfindingMinimalIfcPath()}",
        )
    }

    @Test
    fun initIfcAndFilterByGuid() {
        val handle = NativeEngine.filterByGuid(IfcTestHelper.DOOR_1_GUID)
        assertNotEquals(0L, handle)
        val door = IfcPointer.fromHandle(handle)
        assertTrue(door != null)
        assertTrue(door!!.guid == IfcTestHelper.DOOR_1_GUID)
        assertTrue(door.type == "IfcDoor")
    }
}
