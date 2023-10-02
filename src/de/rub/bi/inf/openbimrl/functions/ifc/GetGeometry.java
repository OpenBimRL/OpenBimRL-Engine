package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import javax.media.j3d.Geometry;
import java.util.ArrayList;

/**
 * Retrieves the geometry of an IFC element.
 *
 * @author Marcel Stepien
 */
public class GetGeometry extends AbstractFunction {

    public GetGeometry(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        final var objects = getInputAsCollection(0);
        if (objects.isEmpty()) return;

        final var resultValues = new ArrayList<Geometry>();

        for (Object o : objects) {
            ClassInterface classInterface = (ClassInterface) o;
            CadObject cad = ifcModel.getCadObjectModel().getCadObject(classInterface);

            if (cad != null) {
                for (SolidShape shape : cad.getSolidShapes()) {
                    MultiAppearanceShape3D sShape = ((MultiAppearanceShape3D) shape);
                    resultValues.add(sShape.getGeometry());
                }
            } else {
                System.err.println("No geometry for: " + classInterface.getClassName() + " [in line " + classInterface.getStepLineNumber() + "]");
            }

        }

        if (resultValues.size() == 1) {
            setResult(0, resultValues.get(0));
        } else {
            setResult(0, resultValues);
        }

    }

}
