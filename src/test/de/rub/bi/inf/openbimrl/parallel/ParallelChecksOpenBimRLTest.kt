package de.rub.bi.inf.openbimrl.parallel

import de.rub.bi.inf.logger.RuleLogger
import de.rub.bi.inf.model.AbstractRuleDefinition
import de.rub.bi.inf.model.RuleBase
import de.rub.bi.inf.openbimrl.OpenRule
import de.rub.bi.inf.openbimrl.utils.OpenBimRLReader
import de.rub.bi.inf.openbimrl.utils.pathfinding.IfcTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.io.File

class ParallelChecksOpenBimRLTest {

    companion object {
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

    private fun runRule(ifcPath: String, openBimRLPath: String): Pair<OpenRule, RuleLogger> {
        assumeTrue(nativeAvailable, "Native library unavailable in this environment")
        assumeTrue(IfcTestHelper.loadIfc(ifcPath), "Failed to load IFC model: $ifcPath")

        RuleBase.getInstance().resetAllRules()
        OpenBimRLReader(listOf(File(openBimRLPath)))

        val rules = RuleBase.getInstance().rules
        assertEquals(1, rules.size)

        val rule = rules[0] as OpenRule
        val logger = RuleLogger()
        rule.check(logger)
        return rule to logger
    }

    @Test
    fun `rails parallel gauge rule passes on fixture model`() {
        val (rule, logger) = runRule(
            IfcTestHelper.railsParallelGaugeIfcPath(),
            IfcTestHelper.railsParallelGaugeOpenBimRLPath(),
        )

        assertNotEquals(
            AbstractRuleDefinition.CheckedStatus.FAILED,
            rule.checkedStatus,
            rule.checkingProtocol,
        )
        assertFalse(
            rule.checkingProtocol.contains("Please Check Node"),
            "Precalculation produced null outputs:\n${rule.checkingProtocol}",
        )

        assertEquals(1, rule.children.size)
        assertEquals(
            AbstractRuleDefinition.CheckedStatus.SUCCESS,
            rule.children[0].checkedStatus,
            rule.children[0].checkingProtocol,
        )

        val separation = logger.getResults()["RailSeparation"] as Double
        val parallel = logger.getResults()["RailsParallel"] as Boolean
        assertTrue(parallel)
        assertEquals(1.435, separation, 0.01)

        val glb = rule.getVisualGlb()
        assertNotNull(glb)
        assertTrue(String(glb!!, Charsets.UTF_8).contains("geometryLines"))
    }

    @Test
    fun `walls parallel rule passes on fixture model`() {
        val (rule, logger) = runRule(
            IfcTestHelper.wallsParallelIfcPath(),
            IfcTestHelper.wallsParallelOpenBimRLPath(),
        )

        assertNotEquals(
            AbstractRuleDefinition.CheckedStatus.FAILED,
            rule.checkedStatus,
            rule.checkingProtocol,
        )
        assertFalse(
            rule.checkingProtocol.contains("Please Check Node"),
            "Precalculation produced null outputs:\n${rule.checkingProtocol}",
        )

        assertEquals(1, rule.children.size)
        assertEquals(
            AbstractRuleDefinition.CheckedStatus.SUCCESS,
            rule.children[0].checkedStatus,
            rule.children[0].checkingProtocol,
        )

        val spacing = logger.getResults()["WallSpacing"] as Double
        val parallel = logger.getResults()["WallsParallel"] as Boolean
        assertTrue(parallel)
        assertEquals(4.0, spacing, 0.05)

        val glb = rule.getVisualGlb()
        assertNotNull(glb)
        assertTrue(String(glb!!, Charsets.UTF_8).contains("geometryLines"))
    }
}
