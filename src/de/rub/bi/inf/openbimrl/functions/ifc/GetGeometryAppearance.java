package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;

/**
 * Retrieves the geometry appearance of an IFC element.
 *
 * @author Marcel Stepien
 */
public class GetGeometryAppearance extends AbstractFunction {

    public GetGeometryAppearance(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        final var objects = getInputAsCollection(0);
        if (objects.isEmpty()) return;

        final var resultValues = new ArrayList<MultiAppearanceShape3D>();

        for (Object o : objects) {
            ClassInterface classInterface = (ClassInterface) o;
            CadObject cad = ifcModel.getCadObjectModel().getCadObject(classInterface);

            for (SolidShape shape : cad.getSolidShapes()) {
                MultiAppearanceShape3D sShape = ((MultiAppearanceShape3D) shape);
                resultValues.add(sShape);
            }

        }

        if (resultValues.size() == 1) {
            setResult(0, resultValues.get(0));
        } else {
            setResult(0, resultValues);
        }

    }

}
