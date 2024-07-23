package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Removes elements from list B in the list A.
 *
 * @author Marcel Stepien
 */
public class RemoveFromList extends AbstractFunction {

    public RemoveFromList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        final var input0 = getInputAsCollection(0);
        final var input1 = getInputAsCollection(1);

        ArrayList<Object> temp = new ArrayList<>(input0);
        temp.removeAll(input1);

        setResult(0, temp);
    }

}
