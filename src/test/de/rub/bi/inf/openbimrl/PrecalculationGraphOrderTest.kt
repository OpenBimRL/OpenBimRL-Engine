package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.openbimrl.utils.pathfinding.IfcTestHelper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import javax.xml.bind.JAXBElement

/**
 * Regression: edges wired only to output handle > 0 must still participate in topo sort,
 * otherwise visualizers for planePlaneMetric.Straight (handle 2) can run before the metric.
 */
class PrecalculationGraphOrderTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun loadNative() {
            // Graph construction instantiates NativeFunction subclasses which touch JNA.
            IfcTestHelper.loadNativeLibrary()
        }
    }

    @Test
    fun `wall metric straight visualizer runs after planePlaneMetric`() {
        val path = IfcTestHelper.wallsParallelOpenBimRLPath()
        @Suppress("UNCHECKED_CAST")
        val element = de.rub.bi.inf.openbimrl.io.OpenBimRLReader.readFromFile(path) as JAXBElement<BIMRuleType>
        val ctx = PrecalculationContext(element.value.precalculations)

        val metricId = "wall-metric"
        val straightVizId = "11993118-4c83-9819-604c-c9b817f60288"

        val order = ctx.graphSortedNodes.map { it.id }
        val metricIndex = order.indexOf(metricId)
        val vizIndex = order.indexOf(straightVizId)
        assertTrue(metricIndex >= 0 && vizIndex >= 0, "Missing nodes in graph: $order")
        assertTrue(
            metricIndex < vizIndex,
            "visualize.straights for metric Straight must run after planePlaneMetric (order=$order)",
        )
    }
}
