package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks if a list of elements are included in another set individually.
 *
 * @author Marcel Stepien
 */
public class ElementIncludedInList extends AbstractFunction {

    public ElementIncludedInList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Collection<Object> objectInput0 = new ArrayList();

        Object input0 = getInput(0);
        if (input0 instanceof Collection<?>) {
            objectInput0 = (Collection<Object>) input0;
        } else {
            objectInput0.add(input0);
        }


        Collection<Object> objectInput1 = new ArrayList();

        Object input1 = getInput(1);
        if (input1 instanceof Collection<?>) {
            objectInput1 = (Collection<Object>) input1;
        } else {
            objectInput1.add(input1);
        }

        ArrayList<Boolean> resultValues = new ArrayList<>();


        for (Object o : objectInput0) {
            resultValues.add(
                    objectInput1.contains(o)
            );
        }

        setResult(0, resultValues);

    }

}
