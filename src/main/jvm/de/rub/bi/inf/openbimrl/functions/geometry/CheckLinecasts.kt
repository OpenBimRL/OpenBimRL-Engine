package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D
import org.apache.commons.geometry.euclidean.threed.line.Segment3D

/**
 * Checks if a ray passing through the scene has an intersection with a geometric object.
 *
 * @author Marcel Stepien
 */
class CheckLinecasts(nodeProxy: NodeProxy?) : AbstractFunction(nodeProxy) {

    override fun execute() {
        val elements0 = getInputAsCollection(0)
        val elements1 = getInputAsCollection(1)
        if (elements0.isEmpty() || elements1.isEmpty()) return

        val filter = ArrayList<Boolean>()
        for (ele1 in elements1) {
            if (ele1 is Segment3D) {
                filter.add(
                    handleRegionBSPTrees(ele1, elements0)
                )
            }
        }
        setResult(0, filter)
    }

    private fun handleRegionBSPTrees(segment: Segment3D, elements: Collection<*>?): Boolean {
        var flag = false
        for (ele in elements!!) {
            if (ele is Collection<*>) {
                flag = flag || handleRegionBSPTrees(segment, ele)
            }
            if (ele is RegionBSPTree3D) {
                flag = flag || check(segment, ele)
            }
        }
        return flag
    }

    private fun check(segment: Segment3D, region: RegionBSPTree3D): Boolean {
        val regionM1 = region.copy()
        val casts = regionM1.linecast(segment)
        return casts.isNotEmpty()
    }
}
