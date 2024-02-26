package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;

/**
 * Converts a String to a Vector3D list. (Example: x,y,z;x,y,z;... )
 *
 * @author Marcel Stepien
 */
public class ConvertStrToVectorList extends AbstractFunction {

    public ConvertStrToVectorList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        String str = getInput(0);

        final var points = new ArrayList<Vector3D>();

        for (String strPoint : str.split(";")) {

            String[] strPointCoords = strPoint.split(",");

            final double xCoord = Double.parseDouble(strPointCoords[0]),
                    yCoord = Double.parseDouble(strPointCoords[1]),
                    zCoord = Double.parseDouble(strPointCoords[2]);

            points.add(Vector3D.of(xCoord, yCoord, zCoord));
        }

        setResult(0, points);
    }

}
