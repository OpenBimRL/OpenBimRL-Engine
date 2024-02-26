package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Joins two RegionalBSPTree3D representations into one.
 *
 * @author Marcel Stepien
 */
public class UnifyRegionBSPTree extends AbstractFunction {

    public UnifyRegionBSPTree(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Object input0 = getInput(0);

        if (input0 == null)
            return;

        Collection<?> bspTrees = null;
        if (input0 instanceof Collection<?>) {
            bspTrees = (Collection<?>) input0;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            newList.add(input0);
            bspTrees = newList;
        }

        RegionBSPTree3D unified = RegionBSPTree3D.empty();

        for (Object ele0 : bspTrees) {
            if (ele0 instanceof RegionBSPTree3D) {

                unified.union((RegionBSPTree3D) ele0);

            }
        }

        setResult(0, unified);

    }

}
