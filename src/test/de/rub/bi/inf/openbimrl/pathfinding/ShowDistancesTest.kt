package de.rub.bi.inf.openbimrl.pathfinding

import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.toPoint3d
import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.utils.addPaddingToObstacles
import de.rub.bi.inf.openbimrl.utils.math.neighbors
import de.rub.bi.inf.openbimrl.utils.pathfinding.IfcTestHelper
import de.rub.bi.inf.openbimrl.utils.pathfinding.clearGeometryBuffer
import de.rub.bi.inf.openbimrl.utils.pathfinding.dijkstra
import de.rub.bi.inf.openbimrl.utils.pathfinding.fillGeometryBuffer
import de.rub.bi.inf.openbimrl.utils.pathfinding.geometryFromPointers
import de.rub.bi.inf.openbimrl.utils.pathfinding.isWalkable
import de.rub.bi.inf.openbimrl.utils.pathfinding.movementCost
import de.rub.bi.inf.openbimrl.utils.pathfinding.movementCostNative
import de.rub.bi.inf.openbimrl.visualization.GltfVisualComposer
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Orientation
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.hexRound
import io.github.offlinebrain.khexagon.math.hexToPixel
import io.github.offlinebrain.khexagon.math.pixelToHex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import kotlin.math.max

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShowDistancesTest {

    companion object {
        /** Default IFC under src/test/resources; override via OPENBIMRL_TEST_IFC or -Dopenbimrl.test.ifc */
        const val DEFAULT_IFC_FILE = "2024-10-25_IC6_ASR_Journal_Paper.ifc"
        const val START_GUID = "0Q4YfcC0z0pe5NxBNGF\$xb"
        const val WALL_IFC_TYPE = "IfcWall"
        const val COLUMN_IFC_TYPE = "IfcColumn"
        const val OPENING_IFC_TYPE = "IfcOpeningElement"
        const val HEXAGON_SIZE = 0.1
        const val MAX_DISTANCE = 5.0
        const val OBSTACLE_PADDING = 0.0
    }

    private lateinit var buildingBounds: BoundingBox
    private lateinit var centerPoint: Point3d
    private lateinit var obstacles: List<IfcPointer>
    private lateinit var passages: List<IfcPointer>
    private lateinit var start: IfcPointer
    private lateinit var layout: Layout
    private lateinit var distancePoints: List<Point3d>
    private lateinit var distanceValues: List<Double>

    @BeforeAll
    fun setUp() {
        IfcTestHelper.loadNativeLibrary()

        val ifcPath = IfcTestHelper.resolveTestIfcPath(DEFAULT_IFC_FILE)
        assertTrue(IfcTestHelper.loadIfc(ifcPath), "Failed to load IFC: $ifcPath")

        val startGuid = IfcTestHelper.resolveTestStartGuid(START_GUID)

        val (center, bounds) = IfcTestHelper.calculateBuildingBounds()
        centerPoint = center
        buildingBounds = bounds

        obstacles = IfcTestHelper.filterByElement(WALL_IFC_TYPE) +
            IfcTestHelper.filterByElement(COLUMN_IFC_TYPE)
        passages = IfcTestHelper.filterByElement(OPENING_IFC_TYPE)

        start = IfcTestHelper.getElementByGuid(startGuid)
            ?: error("Start element not found: $startGuid (IFC: $ifcPath)")

        layout = Layout(
            Orientation.Pointy,
            origin = Point(centerPoint.x.toFloat(), centerPoint.z.toFloat()),
            size = Point(HEXAGON_SIZE.toFloat(), HEXAGON_SIZE.toFloat()),
        )

        val (points, distances) = calculateDistances()
        distancePoints = points
        distanceValues = distances
    }

    private fun calculateDistances(): Pair<List<Point3d>, List<Double>> {
        val startGeometry = geometryFromPointers(listOf(start))
        val bounds = buildingBounds.toRect()
        val passageGeometry = geometryFromPointers(passages)
        val obstacleGeometry = addPaddingToObstacles(
            geometryFromPointers(obstacles),
            OBSTACLE_PADDING,
        )

        require(startGeometry.isNotEmpty()) { "Start element has no footprint geometry" }

        val startHexCoordinates = startGeometry.map { footprint ->
            pixelToHex(
                layout,
                footprint.bounds2D.let { Point(it.x.toFloat(), it.y.toFloat()) },
            ).hexRound()
        }

        clearGeometryBuffer()
        fillGeometryBuffer(
            arrayOf(
                *passageGeometry.toTypedArray(),
                *obstacleGeometry.toTypedArray(),
            ),
        )

        val walkable = isWalkable(layout, bounds, obstacleGeometry, passageGeometry)
        val edgeCost = if (OBSTACLE_PADDING == 0.0) {
            movementCostNative(
                layout = layout,
                starts = startHexCoordinates,
                isWalkable = walkable,
                obstaclePointers = obstacles,
                passagePointers = passages,
            )
        } else {
            movementCost(layout, obstacleGeometry, passageGeometry)
        }

        val distanceField = dijkstra(
            from = startHexCoordinates,
            neighbors = ::neighbors,
            isWalkable = walkable,
            distance = edgeCost,
        )

        val elevationY = max(buildingBounds.lower().y, buildingBounds.upper().y)
        val points = ArrayList<Point3d>(distanceField.size)
        val distances = ArrayList<Double>(distanceField.size)
        distanceField.forEach { (hex, distance) ->
            points.add(hexToPixel(layout, hex).toPoint3d(elevationY))
            distances.add(distance)
        }
        return points to distances
    }

    @Test
    fun `distance field completes on selected IFC model`() {
        assertTrue(distancePoints.isNotEmpty(), "Expected at least one distance sample")
        assertEquals(distancePoints.size, distanceValues.size, "Points and distances must be parallel arrays")
    }

    @Test
    fun `distance field starts at zero distance`() {
        assertTrue(distanceValues.any { it == 0.0 }, "Expected start hex to have zero distance")
        assertTrue(distanceValues.all { it.isFinite() || it == Double.POSITIVE_INFINITY })
    }

    @Test
    fun `distance heatmap produces GLB`() {
        val composer = GltfVisualComposer()
        composer.addDistanceHeatmap(distancePoints, distanceValues, MAX_DISTANCE)

        val glb = composer.toGlb()
        assertNotNull(glb)
        assertTrue(glb!!.size > 12, "Expected non-empty GLB payload")

        assertEquals('g'.code.toByte(), glb[0])
        assertEquals('l'.code.toByte(), glb[1])
        assertEquals('T'.code.toByte(), glb[2])
        assertEquals('F'.code.toByte(), glb[3])
    }
}
