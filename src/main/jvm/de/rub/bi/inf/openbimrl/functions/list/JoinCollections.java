package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Joins two collections to one ArrayList.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class JoinCollections extends AbstractFunction {

    public JoinCollections(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        final Collection<?> elements0 = getInputAsCollection(0),
                elements1 = getInputAsCollection(1);

        final var temp = new ArrayList<>();
        temp.addAll(elements0);
        temp.addAll(elements1);

        setResult(0, temp);
    }

}
