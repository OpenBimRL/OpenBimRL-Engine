package de.rub.bi.inf.openbimrl.functions.annotations

import de.rub.bi.inf.openbimrl.functions.geometry.CalculateDijkstraSearch
import de.rub.bi.inf.openbimrl.functions.math.CreatePlane
import de.rub.bi.inf.openbimrl.functions.math.DistancePointPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FunctionPortDiscoveryTest {

    @Test
    fun `uses field annotations when present`() {
        val ports = findFunctionPortDefinitions(CreatePlane::class.java)

        assertEquals(listOf("Point3d", "Vector3d", "Vector3d"), ports.inputs.map { it.displayName })
        assertEquals(listOf("Plane"), ports.outputs.map { it.displayName })
    }

    @Test
    fun `uses class level ports when fields are absent`() {
        val ports = findFunctionPortDefinitions(DistancePointPoint::class.java)

        assertEquals(listOf("Point A", "Point B"), ports.inputs.map { it.displayName })
        assertEquals(listOf("Distance"), ports.outputs.map { it.displayName })
    }

    @Test
    fun `field based discovery keeps collection type names`() {
        val ports = findFunctionPortDefinitions(CalculateDijkstraSearch::class.java)

        assertTrue(ports.inputs.any { it.displayName == "IfcPointer" })
    }
}
