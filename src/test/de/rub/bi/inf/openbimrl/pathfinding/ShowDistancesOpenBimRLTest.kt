package de.rub.bi.inf.openbimrl.pathfinding

import de.rub.bi.inf.logger.RuleLogger
import de.rub.bi.inf.model.AbstractRuleDefinition
import de.rub.bi.inf.model.RuleBase
import de.rub.bi.inf.openbimrl.OpenRule
import de.rub.bi.inf.openbimrl.utils.OpenBimRLReader
import de.rub.bi.inf.openbimrl.utils.pathfinding.IfcTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import javax.vecmath.Point3d

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShowDistancesOpenBimRLTest {

    @BeforeAll
    fun setUp() {
        IfcTestHelper.loadNativeLibrary()
        assertTrue(
            IfcTestHelper.loadPathfindingMinimalIfc(),
            "Failed to load ${IfcTestHelper.pathfindingMinimalIfcPath()}",
        )

        RuleBase.getInstance().resetAllRules()
        OpenBimRLReader(listOf(File(IfcTestHelper.showDistancesOpenBimRLPath())))
    }

    private fun openRule(): OpenRule =
        RuleBase.getInstance().rules[0] as OpenRule

    @Test
    fun `show distances graph completes on pathfinding minimal model`() {
        val rules = RuleBase.getInstance().rules
        assertEquals(1, rules.size)

        val logger = RuleLogger()
        rules[0].check(logger)

        assertNotEquals(
            AbstractRuleDefinition.CheckedStatus.FAILED,
            rules[0].checkedStatus,
            rules[0].checkingProtocol,
        )

        val protocol = rules[0].checkingProtocol
        assertFalse(
            protocol.contains("Please Check Node"),
            "Precalculation produced null outputs:\n$protocol",
        )
    }

    @Test
    fun `show distances graph outputs points and distances arrays`() {
        val logger = RuleLogger()
        openRule().check(logger)

        val distanceNodeLog = logger.getLogs().entries.firstOrNull {
            it.key.contains("calculateDistancesFromElement")
        }
        assertNotNull(distanceNodeLog, "Expected calculateDistancesFromElement node in execution log")

        assertTrue(distanceNodeLog!!.value.outputs.size >= 2)
        val points = distanceNodeLog.value.outputs[0] as? Collection<*>
        val distances = distanceNodeLog.value.outputs[1] as? Collection<*>
        assertNotNull(points)
        assertNotNull(distances)
        assertTrue(points!!.isNotEmpty(), "Expected at least one distance point")
        assertEquals(points.size, distances!!.size, "Points and distances must be parallel arrays")
        assertTrue(points.first() is Point3d)
        assertTrue(distances.first() is Double)
    }

    @Test
    fun `show distances graph produces GLB via visualize distanceHeatmap node`() {
        val rule = openRule()
        rule.check(RuleLogger())

        val glb = rule.getVisualGlb()
        assertNotNull(glb)
        assertTrue(glb!!.size > 12, "Expected non-empty GLB payload")

        assertEquals('g'.code.toByte(), glb[0])
        assertEquals('l'.code.toByte(), glb[1])
        assertEquals('T'.code.toByte(), glb[2])
        assertEquals('F'.code.toByte(), glb[3])
    }
}
