package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.extensions.toRect
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.DisplayableFunction
import de.rub.bi.inf.openbimrl.helper.neighbors
import de.rub.bi.inf.openbimrl.helper.pathfinding.filterObstacles
import de.rub.bi.inf.openbimrl.helper.pathfinding.isWalkable
import de.rub.bi.inf.openbimrl.helper.pathfinding.movementCost
import io.github.offlinebrain.khexagon.algorythm.aStar
import io.github.offlinebrain.khexagon.coordinates.HexCoordinates
import io.github.offlinebrain.khexagon.math.Layout
import io.github.offlinebrain.khexagon.math.Point
import io.github.offlinebrain.khexagon.math.distance
import io.github.offlinebrain.khexagon.math.pixelToHex
import javax.media.j3d.BoundingBox


/**
 * Performs the shortest path search based on distances from the start node to all reachable end nodes in a graph.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
class CalculateAStarSearch(nodeProxy: NodeProxy) : DisplayableFunction(nodeProxy) {
    /**
     * start: [IfcPointer], end: [IfcPointer], bounds: [BoundingBox], obstacles: [List], layout [Layout]
     */
    override fun execute() {
        val start = getInputAsCollection(0)?.filterIsInstance<IfcPointer>()?.get(0)?.polygon?.value
        val end = getInputAsCollection(1)?.filterIsInstance<IfcPointer>()?.get(0)?.polygon?.value
        val bounds = getInputAsCollection(2)?.filterIsInstance<BoundingBox>()?.get(0)?.toRect()

        val obstacles = filterObstacles(getInputAsCollection(3))
        val layout = getInput<Layout>(4)

        if (start?.isEmpty == true || end?.isEmpty == true || bounds == null || layout == null) return
        val startHexCoordinate =
            pixelToHex(layout, start!!.get().bounds2D.let { Point(it.x.toFloat(), it.y.toFloat()) }).hexRound()
        val endHexCoordinates =
            pixelToHex(layout, end!!.get().bounds2D.let {
                Point(it.x.toFloat(), it.y.toFloat())
            }).hexRound()

        aStar(
            from = startHexCoordinate,
            to = endHexCoordinates,
            neighbors = ::neighbors,
            isWalkable = isWalkable(layout, bounds, obstacles),
            distance = HexCoordinates::distance,
            movementCost = movementCost(layout, obstacles)
        )
    }
}
