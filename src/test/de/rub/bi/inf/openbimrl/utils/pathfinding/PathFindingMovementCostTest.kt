package de.rub.bi.inf.openbimrl.utils.pathfinding

import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.openbimrl.utils.math.neighbors
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Orientation
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.hexToPixel
import io.github.offlinebrain.khexagon.math.pixelToHex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PathFindingMovementCostTest {

    private lateinit var layout: Layout
    private lateinit var walkable: (HexCoordinates) -> Boolean
    private lateinit var starts: List<HexCoordinates>
    private lateinit var walls: List<de.rub.bi.inf.nativelib.IfcPointer>
    private lateinit var openings: List<de.rub.bi.inf.nativelib.IfcPointer>
    private lateinit var doors: List<de.rub.bi.inf.nativelib.IfcPointer>

    @BeforeAll
    fun setUp() {
        IfcTestHelper.loadNativeLibrary()

        val ifcPath = Paths.get("src", "test", "resources", "pathfinding_minimal.ifc")
            .toFile()
            .absolutePath

        assertTrue(
            IfcTestHelper.loadIfc(ifcPath),
            "Failed to load minimal pathfinding IFC model at $ifcPath",
        )

        walls = IfcTestHelper.filterByElement("IfcWall")
        openings = IfcTestHelper.filterByElement("IfcOpeningElement")
        doors = IfcTestHelper.filterByElement("IfcDoor")

        assertEquals(6, walls.size, "Expected 4 outer walls and 2 partition walls")
        assertEquals(2, openings.size, "Expected two door openings")
        assertEquals(2, doors.size, "Expected two doors")

        val obstacles = geometryFromPointers(walls)
        val passages = geometryFromPointers(openings)

        assertFalse(obstacles.isEmpty(), "Wall geometry should be available")
        assertFalse(passages.isEmpty(), "Opening geometry should be available")

        val (center, bounds) = IfcTestHelper.calculateBuildingBounds()
        val boundsPath = bounds.toRect()

        layout = Layout(
            orientation = Orientation.Pointy,
            origin = Point(center.x.toFloat(), center.z.toFloat()),
            size = Point(0.3f, 0.3f),
        )

        clearGeometryBuffer()
        fillGeometryBuffer(arrayOf(*passages.toTypedArray(), *obstacles.toTypedArray()))

        walkable = isWalkable(layout, boundsPath, obstacles, passages)
        starts = doors.map { door ->
            val polygon = geometryFromPointers(listOf(door)).first()
            pixelToHex(
                layout,
                Point(polygon.bounds2D.centerX.toFloat(), polygon.bounds2D.centerY.toFloat()),
            ).hexRound()
        }
    }

    @Test
    fun `unobstructed edge costs match exactly between legacy and native`() {
        val obstacles = geometryFromPointers(walls)
        val passages = geometryFromPointers(openings)

        val legacyCost = movementCost(layout, obstacles, passages)
        val nativeCost = movementCostNative(
            layout = layout,
            starts = starts,
            isWalkable = walkable,
            obstaclePointers = walls,
            passagePointers = openings,
        )

        val reachable = collectReachableHexes(starts, walkable)
        assertTrue(reachable.size > 10, "Expected a non-trivial reachable area")

        var comparedEdges = 0
        reachable.forEach { node ->
            neighbors(node).forEach { neighbor ->
                if (!reachable.contains(neighbor) || !walkable(neighbor)) return@forEach

                val legacy = legacyCost(node, neighbor)
                val native = nativeCost(node, neighbor)

                if (legacy.isFinite() && native.isFinite()) {
                    assertEquals(
                        legacy,
                        native,
                        1e-6,
                        "Unobstructed edge cost mismatch for $node -> $neighbor",
                    )
                    comparedEdges++
                }
            }
        }

        assertTrue(comparedEdges > 0, "Expected at least one unobstructed comparable edge")
    }

    @Test
    fun `dijkstra distance maps match for legacy and native movement costs`() {
        val obstacles = geometryFromPointers(walls)
        val passages = geometryFromPointers(openings)

        val legacyDistances = dijkstra(
            from = starts,
            neighbors = ::neighbors,
            isWalkable = walkable,
            distance = movementCost(layout, obstacles, passages),
        )

        val nativeDistances = dijkstra(
            from = starts,
            neighbors = ::neighbors,
            isWalkable = walkable,
            distance = movementCostNative(
                layout = layout,
                starts = starts,
                isWalkable = walkable,
                obstaclePointers = walls,
                passagePointers = openings,
            ),
        )

        val keys = legacyDistances.keys.union(nativeDistances.keys)
        assertTrue(keys.isNotEmpty(), "Expected dijkstra to reach at least one hex")

        keys.forEach { hex ->
            assertCostsEqual(
                legacyDistances.getValue(hex),
                nativeDistances.getValue(hex),
                hex,
                hex,
            )
        }
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

    private fun assertCostsEqual(
        legacy: Double,
        native: Double,
        from: HexCoordinates,
        to: HexCoordinates,
    ) {
        val bothInfinite = legacy.isInfinite() && native.isInfinite()
        val bothFinite = legacy.isFinite() && native.isFinite()

        assertTrue(
            bothInfinite || bothFinite,
            "Cost mismatch for $from -> $to: legacy=$legacy native=$native",
        )

        if (bothFinite) {
            assertEquals(
                legacy,
                native,
                1e-6,
                "Cost mismatch for $from -> $to (pixel ${hexToPixel(layout, from)} -> ${hexToPixel(layout, to)})",
            )
        }
    }
}
