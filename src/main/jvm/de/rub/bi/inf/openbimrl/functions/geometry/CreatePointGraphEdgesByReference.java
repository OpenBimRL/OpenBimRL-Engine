package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

/**
 * Connects nodes of a graph by inserting edges of a certain stepsize, provided the nodes are already known.
 * Prioritization on reference nodes.
 *
 * @author Marcel Stepien (reworked by Florian Becker)
 */
@OpenBIMRLFunction
public class CreatePointGraphEdgesByReference extends AbstractFunction {

    public CreatePointGraphEdgesByReference(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        // TODO redo
    }

}
