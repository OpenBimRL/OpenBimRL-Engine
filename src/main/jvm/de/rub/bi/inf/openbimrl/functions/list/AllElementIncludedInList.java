package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks if a list of elements are included in another set holistically.
 *
 * @author Marcel Stepien
 */
public class AllElementIncludedInList extends AbstractFunction {

    public AllElementIncludedInList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Collection<?> objectInput0 = getInputAsCollection(0);


        Collection<?> objectInput1 = getInputAsCollection(1);

        setResult(0, objectInput1.containsAll(objectInput0));
    }

}
