package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Find the elements that are included in a spatial structure element.
 *
 * @author Marcel Stepien
 */
public class GetElementsInSpatialStructure extends AbstractFunction {

    public GetElementsInSpatialStructure(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Object input0 = getInputAsCollection(0);

        ArrayList<Object> resultValues = new ArrayList<>();

        //System.out.println("GetElementsInSpatialStructure: " + objects);

        /*for (Object o : objects) {
            if (o instanceof IIFCSpatialStructureElement element) {

                if (element.getContainsElements_Inverse() != null) {
                    for (IfcRelContainedInSpatialStructure relContains :
                            element.getContainsElements_Inverse()) {
                        Set<?> elements = relContains.getRelatedElements();
                        resultValues.addAll(elements);
                    }
                }

                if (((IfcSpatialStructureElement.Ifc4) o).getIsDecomposedBy_Inverse() != null) {
                    for (IfcRelAggregates relAggregates :
                            ((IfcSpatialStructureElement.Ifc4) o).getIsDecomposedBy_Inverse()) {
                        Set<?> elements = relAggregates.getRelatedObjects();
                        resultValues.addAll(elements);
                    }
                }

            }
        }*/

        setResult(0, resultValues);

    }

}
