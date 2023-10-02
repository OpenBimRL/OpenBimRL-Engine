package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCCartesianPoint;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCPolyline;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Calculates the length of a Curve (like an IfcPolyline).
 *
 * @author Marcel Stepien
 */
public class GetCurveLength extends AbstractFunction {

    public GetCurveLength(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        final var polylines = getInputAsCollection(0);
        if (polylines.isEmpty())return;

        final var resultValues = new ArrayList<Double>();

        for (Object repItemObj : polylines) {
            if (repItemObj instanceof IIFCPolyline polyline) {

                double distanceMeasured = 0.0;
                boolean first = true;
                Point3d previous = null;

                for (IIFCCartesianPoint point : polyline.getPoints()) {
                    Point3d currentP = new Point3d(
                            point.getCoordinates().get(0),
                            point.getCoordinates().get(1),
                            point.getCoordinates().get(2)
                    );

                    if (!first) {
                        distanceMeasured += previous.distance(currentP);
                    } else {
                        first = false;
                    }

                    previous = currentP;
                }

                resultValues.add(distanceMeasured);


            }
        }


        if (resultValues.size() == 1) {
            setResult(0, resultValues.get(0));
        } else {
            setResult(0, resultValues);
        }

    }

}
