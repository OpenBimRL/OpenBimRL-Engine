package de.rub.bi.inf.openbimrl.functions.annotations

import de.rub.bi.inf.openbimrl.functions.geometry.CalculateDijkstraSearch
import de.rub.bi.inf.openbimrl.functions.math.CreatePlane
import de.rub.bi.inf.openbimrl.functions.math.DistancePointPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FunctionPortDiscoveryTest {

    @Test
    fun `uses field annotations when present`() {
        val ports = findFunctionPortDefinitions(CreatePlane::class.java)

        assertEquals(listOf("Point", "Axis U", "Axis V"), ports.inputs.map { it.displayName })
        assertEquals(listOf("Plane"), ports.outputs.map { it.displayName })
    }

    @Test
    fun `uses class level ports when fields are absent`() {
        val ports = findFunctionPortDefinitions(DistancePointPoint::class.java)

        assertEquals(listOf("Point A", "Point B"), ports.inputs.map { it.displayName })
        assertEquals(listOf("Distance"), ports.outputs.map { it.displayName })
    }

    @Test
    fun `field based discovery uses capitalized field names when port name is omitted`() {
        val ports = findFunctionPortDefinitions(CalculateDijkstraSearch::class.java)

        assertEquals(
            listOf("Start", "BuildingBoundingBox", "Obstacles", "PassageWays", "Layout", "ObstaclePadding", "MaxDistance"),
            ports.inputs.map { it.displayName },
        )
    }
}
