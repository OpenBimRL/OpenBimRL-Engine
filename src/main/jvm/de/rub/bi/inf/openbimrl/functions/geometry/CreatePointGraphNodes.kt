package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D
import org.apache.commons.geometry.euclidean.threed.Vector3D

/**
 * Computes nodes for a graph containing with certain parametric behavior, such a for creating triangles or quad patches.
 *
 * @author Marcel Stepien
 */
class CreatePointGraphNodes(nodeProxy: NodeProxy?) : AbstractFunction(nodeProxy) {
    override fun execute() {
        if (getInput<Any?>(0) == null) return

        val bspTrees = getInputAsCollection(0)

        val stepSize = (getInput<String>(1) ?: return).toDouble()

        val height = (getInput<String>(2) ?: return).toDouble()

        val jitter = (getInput<String>(3) ?: "0.0").toDouble()


        val listOfNodes = ArrayList<ArrayList<Vector3D>>()

        bspTrees.filterIsInstance<RegionBSPTree3D>().forEach { e ->
            val nodes = createGraphNodes(e, stepSize, height, jitter)
            listOfNodes.add(nodes)
        }

        setResult(0, listOfNodes)
    }

    private fun createGraphNodes(
        bspTree: RegionBSPTree3D,
        stepSize: Double,
        height: Double,
        jitter: Double
    ): ArrayList<Vector3D> {
        val cA = bspTree.bounds.min
        val cB = bspTree.bounds.max

        val margin = 0.2

        var offsetX = (cB.x - cA.x - margin * 2) % stepSize
        val offsetXPerPoint = ((cB.x - cA.x - margin * 2) / stepSize).toInt().toDouble()
        offsetX /= offsetXPerPoint

        var offsetY = (cB.y - cA.y - margin * 2) % stepSize
        val offsetYPerPoint = ((cB.y - cA.y - margin * 2) / stepSize).toInt().toDouble()
        offsetY /= offsetYPerPoint

        val linearPointList = ArrayList<Vector3D>()

        var xStep = 0
        var x = cA.x + margin
        while (x <= cB.x) {
            var jitterOffset = 0.0
            if ((xStep % 2) != 0) {
                jitterOffset = jitter
            }

            var y = cA.y + jitterOffset + margin
            while (y <= cB.y) {
                val gridPoint = Vector3D.of(x, y, height)

                if (bspTree.contains(gridPoint)) {
                    linearPointList.add(gridPoint)
                }

                y += (stepSize + offsetY)
            }

            xStep++
            x += (stepSize + offsetX)
        }

        return linearPointList
    }
}
