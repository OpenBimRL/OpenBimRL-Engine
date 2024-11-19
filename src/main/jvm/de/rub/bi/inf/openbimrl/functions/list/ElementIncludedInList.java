package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks if a list of elements are included in another set individually.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class ElementIncludedInList extends AbstractFunction {

    public ElementIncludedInList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Collection<?> objectInput0 = getInputAsCollection(0);
        Collection<?> objectInput1 = getInputAsCollection(1);

        ArrayList<Boolean> resultValues = new ArrayList<>();

        for (Object o : objectInput0) {
            resultValues.add(
                    objectInput1.contains(o)
            );
        }

        setResult(0, resultValues);

    }

}
