package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCElement;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;

/**
 * Checks space bounds of a lists of elements and retrieves connected elements.
 *
 * @author Marcel Stepien
 */
public class CheckSpaceBounds extends AbstractFunction {

    public CheckSpaceBounds(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        final var elements0 = getInputAsCollection(0);

        if (elements0.isEmpty()) return;


        final var resultValues = new ArrayList<IIFCElement>();

/*        elements0.stream().filter(element -> element instanceof IIFCSpace).forEach(space -> {

            final var boundedby = space.getBoundedBy_Inverse();
            if (boundedby == null) return;
            for (var relSpaceBoundary : boundedby) {
                final var relatedElement = relSpaceBoundary.getRelatedBuildingElement();
                if (relatedElement == null) {
                    continue;
                }

                if (!relSpaceBoundary.getInternalOrExternalBoundary().getValue().equals(
                        IfcInternalOrExternalEnum.Ifc4.IfcInternalOrExternalEnum_internal.INTERNAL)) {
                    continue;
                }

                System.out.println(
                        relSpaceBoundary.getInternalOrExternalBoundary().toString() + " " +
                                relSpaceBoundary.getName() + " / " +
                                relSpaceBoundary.getDescription() + " " +
                                relSpaceBoundary.getPhysicalOrVirtualBoundary().getValue()
                );

                if (relatedElement instanceof IIFCClass) {
                    resultValues.add(relatedElement);
                }
            }


        });*/

        setResult(0, resultValues);
    }

}
