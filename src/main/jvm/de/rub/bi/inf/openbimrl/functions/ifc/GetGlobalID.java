package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.ArrayList;

/**
 * Retrives the GUID of an IFC element.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class GetGlobalID extends AbstractFunction {

    public GetGlobalID(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        final var objects = getInputAsCollection(0);
        if (objects.isEmpty()) return;


        final var resultValues = new ArrayList<String>();

        /*for (Object o : objects) {
            if (o instanceof IIFCObject ifcObj) {

                resultValues.add(
                        ifcObj.getGlobalId().getDecodedValue()
                );

            }
        }
*/
        if (resultValues.size() == 1) {
            setResult(0, resultValues.get(0));
        } else {
            setResult(0, resultValues);
        }

    }

}
