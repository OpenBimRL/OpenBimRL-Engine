package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

/**
 * Checks if the bounds of geometric objects are intersecting.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
@OpenBIMRLFunction
public class CheckIntersectionByBounds extends AbstractFunction {

    public CheckIntersectionByBounds(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        // TODO redo
    }

}
