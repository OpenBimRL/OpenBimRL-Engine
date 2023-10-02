package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCClass;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCProduct;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCRepresentationItem;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;

/**
 * Retrieves the representation items based on the IFC element.
 *
 * @author Marcel Stepien
 */
public class GetRepresentationItems extends AbstractFunction {

    public GetRepresentationItems(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        final var objects = getInputAsCollection(0);
        if (objects.isEmpty()) return;

        final var resultValues = new ArrayList<IIFCRepresentationItem>();

        for (Object o : objects) {
            final var classInterface = (IIFCClass) o;

            if (classInterface instanceof IIFCProduct prod) {
                final var prodRep = prod.getRepresentation();
                if (prodRep != null) {
                    for (var rep : prodRep.getRepresentations()) {
                        resultValues.addAll(rep.getItems());
                    }
                }
            }

        }

        if (resultValues.size() == 1) {
            setResult(0, resultValues.get(0));
        } else {
            setResult(0, resultValues);
        }
    }

}
