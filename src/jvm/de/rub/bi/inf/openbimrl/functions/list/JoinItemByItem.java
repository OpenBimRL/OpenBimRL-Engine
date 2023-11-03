package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Joins two collections of same size item by item to one ArrayList.
 *
 * @author Marcel Stepien
 */
public class JoinItemByItem extends AbstractFunction {

    public JoinItemByItem(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        Object input0 = getInput(0);
        Object input1 = getInput(1);

        ArrayList<?> elements0 = null;
        if (input0 instanceof ArrayList<?>) {
            elements0 = (ArrayList<?>) input0;
        } else {
            return;
        }

        ArrayList<?> elements1 = null;
        if (input1 instanceof ArrayList<?>) {
            elements1 = (ArrayList<?>) input1;
        } else {
            return;
        }

        if (elements0.size() != elements1.size()) {
            return;
        }

        ArrayList<Object> result = new ArrayList<Object>();
        for (int index = 0; index < elements0.size(); index++) {
            ArrayList<Object> temp = new ArrayList<Object>();

            if (elements0.get(index) instanceof Collection) {
                temp.addAll((Collection) elements0.get(index));
            } else {
                temp.add(elements0.get(index));
            }

            if (elements1.get(index) instanceof Collection) {
                temp.addAll((Collection) elements1.get(index));
            } else {
                temp.add(elements1.get(index));
            }

            result.add(temp);

        }

        setResult(0, result);
    }

}
