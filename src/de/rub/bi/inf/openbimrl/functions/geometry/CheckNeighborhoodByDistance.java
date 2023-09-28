package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.List;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Checks if geometic object are close to each other based on their geometric definitions.
 *
 * @author Marcel Stepien
 */
public class CheckNeighborhoodByDistance extends AbstractFunction {

    public CheckNeighborhoodByDistance(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {
        final var geometryGroupA = getInputAsCollection(0);
        final var geometryGroupB = getInputAsCollection(1);
        if (geometryGroupA.isEmpty() || geometryGroupB.isEmpty()) return;

        Object input2 = getInput(2);
        double distanceThreshold = 1.0;
        if (input2 != null)
            distanceThreshold = Double.parseDouble(input2.toString());


        final var resultValues = new ArrayList<List<?>>();

        for (Object og1 : geometryGroupA) {
            if (!(og1 instanceof ArrayList<?>)) continue;

            for (Object o1 : (ArrayList<?>) og1) {
                if (!(o1 instanceof GeometryArray)) continue;

                GeometryInfo geoA = new GeometryInfo((GeometryArray) o1);
                geoA.recomputeIndices();

                final var mask = new ArrayList<Boolean>();

                for (Object og2 : geometryGroupB) {
                    if (!(og2 instanceof ArrayList<?>)) continue;

                    boolean flag = false;

                    for (Object o2 : (ArrayList<?>) og2) {
                        if (!(o2 instanceof GeometryArray)) continue;
                        final var geoB = new GeometryInfo((GeometryArray) o2);
                        geoB.recomputeIndices();

                        for (Point3f pA : geoA.getCoordinates()) {
                            for (Point3f pB : geoB.getCoordinates()) {
                                if (pA.distance(pB) <= distanceThreshold) {

                                    flag = true;
                                    break; // flag can't be set back to false

                                }
                            }
                        }
                    }

                    mask.add(flag);
                }
                resultValues.add(mask);
            }
        }

        setResult(0, resultValues);
    }

}
