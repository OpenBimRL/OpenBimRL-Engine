package de.rub.bi.inf.openbimrl.utils.pathfinding

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestReporter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PathFindingMovementCostBenchmarkTest {

    @BeforeAll
    fun loadNativeLibrary() {
        IfcTestHelper.loadNativeLibrary()
    }

    @Test
    fun `benchmark legacy and native pathfinding on minimal model`(testReporter: TestReporter) {
        PathFindingBenchmark.report(
            testReporter,
            PathFindingBenchmark.run("pathfinding_minimal.ifc", doorCount = 2),
        )
    }
}
