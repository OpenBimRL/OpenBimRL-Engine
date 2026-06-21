package de.rub.bi.inf.openbimrl.utils.pathfinding

import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.openbimrl.utils.math.neighbors
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Orientation
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.pixelToHex
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.TestReporter
import java.nio.file.Paths
import kotlin.system.measureNanoTime

data class PathfindingBenchmarkResult(
    val label: String,
    val warmupRuns: Int,
    val measuredRuns: Int,
    val reachableHexes: Int,
    val legacyBuildMs: Double,
    val nativeBuildMs: Double,
    val legacyDijkstraMs: Double,
    val nativeDijkstraMs: Double,
) {
    val legacyTotalMs: Double get() = legacyBuildMs + legacyDijkstraMs
    val nativeTotalMs: Double get() = nativeBuildMs + nativeDijkstraMs
    val speedupTotal: Double get() = legacyTotalMs / nativeTotalMs
}

object PathFindingBenchmark {

    fun run(fileName: String, doorCount: Int, warmupRuns: Int = 3, measuredRuns: Int = 10): PathfindingBenchmarkResult {
        val ifcPath = Paths.get("src", "test", "resources", fileName).toFile().absolutePath
        assertTrue(IfcTestHelper.loadIfc(ifcPath), "Failed to load $fileName")

        val walls = IfcTestHelper.filterByElement("IfcWall")
        val openings = IfcTestHelper.filterByElement("IfcOpeningElement")
        val doors = IfcTestHelper.filterByElement("IfcDoor")

        val obstacles = geometryFromPointers(walls)
        val passages = geometryFromPointers(openings)
        val (center, bounds) = IfcTestHelper.calculateBuildingBounds()

        val layout = Layout(
            orientation = Orientation.Pointy,
            origin = Point(center.x.toFloat(), center.z.toFloat()),
            size = Point(0.3f, 0.3f),
        )

        clearGeometryBuffer()
        fillGeometryBuffer(arrayOf(*passages.toTypedArray(), *obstacles.toTypedArray()))

        val walkable = isWalkable(layout, bounds.toRect(), obstacles, passages)
        val starts = doors.take(doorCount).map { door ->
            val polygon = geometryFromPointers(listOf(door)).first()
            pixelToHex(
                layout,
                Point(polygon.bounds2D.centerX.toFloat(), polygon.bounds2D.centerY.toFloat()),
            ).hexRound()
        }

        val reachableHexes = collectReachableHexes(starts, walkable)

        repeat(warmupRuns) {
            movementCost(layout, obstacles, passages)
            movementCostNative(
                layout = layout,
                starts = starts,
                isWalkable = walkable,
                obstaclePointers = walls,
                passagePointers = openings,
            )
        }

        val legacyBuildNs = averageNanos(measuredRuns) {
            movementCost(layout, obstacles, passages)
        }
        val nativeBuildNs = averageNanos(measuredRuns) {
            movementCostNative(
                layout = layout,
                starts = starts,
                isWalkable = walkable,
                obstaclePointers = walls,
                passagePointers = openings,
            )
        }

        val legacyCost = movementCost(layout, obstacles, passages)
        val nativeCost = movementCostNative(
            layout = layout,
            starts = starts,
            isWalkable = walkable,
            obstaclePointers = walls,
            passagePointers = openings,
        )

        repeat(warmupRuns) {
            dijkstra(from = starts, neighbors = ::neighbors, isWalkable = walkable, distance = legacyCost)
            dijkstra(from = starts, neighbors = ::neighbors, isWalkable = walkable, distance = nativeCost)
        }

        val legacyDijkstraNs = averageNanos(measuredRuns) {
            dijkstra(from = starts, neighbors = ::neighbors, isWalkable = walkable, distance = legacyCost)
        }
        val nativeDijkstraNs = averageNanos(measuredRuns) {
            dijkstra(from = starts, neighbors = ::neighbors, isWalkable = walkable, distance = nativeCost)
        }

        return PathfindingBenchmarkResult(
            label = fileName,
            warmupRuns = warmupRuns,
            measuredRuns = measuredRuns,
            reachableHexes = reachableHexes.size,
            legacyBuildMs = legacyBuildNs / 1_000_000.0,
            nativeBuildMs = nativeBuildNs / 1_000_000.0,
            legacyDijkstraMs = legacyDijkstraNs / 1_000_000.0,
            nativeDijkstraMs = nativeDijkstraNs / 1_000_000.0,
        )
    }

    fun report(testReporter: TestReporter, result: PathfindingBenchmarkResult) {
        val summary = buildString {
            appendLine("Pathfinding benchmark: ${result.label}")
            appendLine("warmup runs: ${result.warmupRuns}, measured runs: ${result.measuredRuns}")
            appendLine("reachable hexes: ${result.reachableHexes}")
            appendLine("movementCost build:       ${"%.3f".format(result.legacyBuildMs)} ms")
            appendLine("movementCostNative build: ${"%.3f".format(result.nativeBuildMs)} ms")
            appendLine("dijkstra (legacy costs):  ${"%.3f".format(result.legacyDijkstraMs)} ms")
            appendLine("dijkstra (native costs):  ${"%.3f".format(result.nativeDijkstraMs)} ms")
            appendLine("end-to-end legacy:        ${"%.3f".format(result.legacyTotalMs)} ms")
            appendLine("end-to-end native:        ${"%.3f".format(result.nativeTotalMs)} ms")
            appendLine("end-to-end speedup:       ${"%.2f".format(result.speedupTotal)}x")
        }

        testReporter.publishEntry("benchmark", summary)
        println(summary)
    }

    private fun collectReachableHexes(
        starts: List<HexCoordinates>,
        isWalkable: (HexCoordinates) -> Boolean,
    ): Set<HexCoordinates> {
        val queue = ArrayDeque<HexCoordinates>()
        val reachable = linkedSetOf<HexCoordinates>()

        starts.forEach { start ->
            if (isWalkable(start)) {
                queue.add(start)
                reachable.add(start)
            }
        }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            neighbors(current).forEach { next ->
                if (reachable.add(next) && isWalkable(next)) {
                    queue.add(next)
                }
            }
        }

        return reachable
    }

    private fun averageNanos(runs: Int, block: () -> Unit): Long {
        var total = 0L
        repeat(runs) {
            total += measureNanoTime(block)
        }
        return total / runs
    }
}
