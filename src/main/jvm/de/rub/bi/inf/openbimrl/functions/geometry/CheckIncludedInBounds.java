package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

/**
 * Checks if points of a geometry are included in the bounds of a geometric objects.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
@OpenBIMRLFunction
public class CheckIncludedInBounds extends AbstractFunction {

    public CheckIncludedInBounds(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        // TODO redo
    }

}
