package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Calculates the distance of bounds by proximity of their coordinates in cardinal directions.
 *
 * @author Marcel Stepien
 */
public class CalculateClosestDistanceToBounds extends AbstractFunction {

    public CalculateClosestDistanceToBounds(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
/*
        Object input0 = getInput(0);
        Object input1 = getInput(1);

        if (input0 == null || input1 == null)
            return;

        Collection<?> geometryGroupA = null;
        if (input0 instanceof Collection<?>) {
            geometryGroupA = (Collection<?>) input0;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            newList.add(input0);
            geometryGroupA = newList;
        }

        Collection<?> geometryGroupB = null;
        if (input1 instanceof Collection<?>) {
            geometryGroupB = (Collection<?>) input1;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            newList.add(input1);
            geometryGroupB = newList;
        }

        ArrayList<List<?>> resultValues = new ArrayList<List<?>>();
        for (Object og1 : geometryGroupA) {
            if (og1 instanceof ArrayList<?>) {


                ArrayList<Double> distances = new ArrayList<>();
                for (Object o1 : (ArrayList<?>) og1) {
                    if (o1 instanceof GeometryArray) {
                        GeometryInfo geoA = new GeometryInfo((GeometryArray) o1);
                        geoA.recomputeIndices();
                        Double minDistance = Double.NaN;
                        for (Point3f coodinateA : geoA.getCoordinates()) {

                            for (Object og2 : geometryGroupB) {
                                if (og2 instanceof ArrayList<?>) {

                                    for (Object o2 : (ArrayList<?>) og2) {
                                        if (o2 instanceof GeometryArray) {
                                            GeometryInfo geoB = new GeometryInfo((GeometryArray) o2);
                                            geoB.recomputeIndices();

                                            for (Point3f coodinateB : geoB.getCoordinates()) {

                                                double x = coodinateB.getX() - coodinateA.getX();
                                                x = Math.sqrt(x * x);
                                                if (minDistance.isNaN() || minDistance > x) {
                                                    minDistance = x;
                                                }

                                                double y = coodinateB.getY() - coodinateA.getY();
                                                y = Math.sqrt(y * y);
                                                if (minDistance.isNaN() || minDistance > y) {
                                                    minDistance = y;
                                                }

                                                double z = coodinateB.getZ() - coodinateA.getZ();
                                                z = Math.sqrt(z * z);
                                                if (minDistance.isNaN() || minDistance > z) {
                                                    minDistance = z;
                                                }
                                            }

                                        }

                                    }

                                }
                            }
                        }
                        distances.add(minDistance);

                    }
                }

                resultValues.add(distances);
            }
        }

        setResult(0, resultValues);*/
    }

}
