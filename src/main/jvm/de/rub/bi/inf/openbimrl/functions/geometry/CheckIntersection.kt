package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D

/**
 * Checks if geometies of two RegionalBSPTree3D are intersecting.
 *
 * @author Marcel Stepien
 */
class CheckIntersection(nodeProxy: NodeProxy?) : AbstractFunction(nodeProxy) {
    override fun execute() {
        val elements0 = getInputAsCollection(0)
        val elements1 = getInputAsCollection(1)
        if (elements0.isEmpty() || elements1.isEmpty()) return

        val resultValues = LinkedHashMap<Any, ArrayList<Boolean>>()
        for (ele0 in elements0) {
            if (ele0 !is RegionBSPTree3D)
                continue
            // else
            val filter = ArrayList<Boolean>()
            for (ele1 in elements1) { // TODO extract
                if (ele1 !is RegionBSPTree3D)
                    continue
                // else
                try {
                    val regionM1 = ele0.copy()
                    val regionM2 = ele1.copy()
                    regionM1.intersection(regionM2)
                    if (regionM1.size > 0.0) // may be reduced to: filter.add(regionM1.size > 0.0)
                        filter.add(true)
                    else
                        filter.add(false)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            resultValues[ele0] = filter

        }
        setResult(0, resultValues)
    }
}
