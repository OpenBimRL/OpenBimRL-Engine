package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;

/**
 * Find the IfcStorey that is related to a specific entity.
 *
 * @author Marcel Stepien
 */
public class GetStorey extends AbstractFunction {

    public GetStorey(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        final var objects = getInputAsCollection(0);

        final var resultValues = new ArrayList<>();

/*        for (Object o : objects) {
            IfcObjectDefinition.Ifc4 ifcObjectDefinition = (IfcObjectDefinition.Ifc4) o;

            if (ifcObjectDefinition instanceof IfcElement) {
                for (IfcRelContainedInSpatialStructure relContains :
                        ((IfcElement) ifcObjectDefinition).getContainedInStructure_Inverse()) {

                    if (relContains instanceof IfcRelContainedInSpatialStructure.Ifc4) {
                        Object spatialElement = ((IfcRelContainedInSpatialStructure.Ifc4) relContains).getRelatingStructure();
                        if (spatialElement instanceof IfcBuildingStorey) {

                            resultValues.add(((IfcBuildingStorey) spatialElement));

                        }
                    }
                }
            }

        }*/

        if (resultValues.size() == 1) {
            setResult(0, resultValues.get(0));
        } else {
            setResult(0, resultValues);
        }
    }

}
