package de.rub.bi.inf.openbimrl.pathfinding

import de.rub.bi.inf.logger.RuleLogger
import de.rub.bi.inf.model.AbstractRuleDefinition
import de.rub.bi.inf.model.RuleBase
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShowDistancesOpenBimRLTest {

    companion object {
        private const val DISTANCE_NODE_ID = "81d1cb18-d268-8548-6a44-42f1204882a6"
    }

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
    fun `show distances graph produces distance visuals`() {
        val logger = RuleLogger()
        RuleBase.getInstance().rules[0].check(logger)

        val distanceNodeLog = logger.getLogs().entries.firstOrNull { it.key.contains("calculateDistancesFromElement") }
        assertNotNull(distanceNodeLog, "Expected calculateDistancesFromElement node in execution log")

        val visuals = logger.getGraphicalOutputs()[DISTANCE_NODE_ID]
        assertNotNull(visuals)
        assertTrue(visuals!!.isNotEmpty(), "Expected at least one distance visual sphere")
    }
}
