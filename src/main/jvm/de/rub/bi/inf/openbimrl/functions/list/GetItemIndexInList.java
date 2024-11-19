package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class GetItemIndexInList extends AbstractFunction {

    public GetItemIndexInList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        final var collection = new ArrayList<>(getInputAsCollection(0));

        ArrayList<Object> values = new ArrayList<>();
        if (getInput(1, Object.class) instanceof Collection<?> c) {
            for (Object i : c) {
                int index = findIndex(collection, i);
                values.add(index);
            }
        }

        //System.out.println("B: " + values.toString());
        if (values.size() == 1) {
            setResult(0, values.get(0));
        } else {
            setResult(0, values);
        }
    }

    // Linear-search function to find the index of an element
    public int findIndex(ArrayList<?> list, Object item) {
        // if array is Null
        if (list == null) {
            return -1;
        }


        int len = list.size();
        int i = 0;

        while (i < len) {
            if (list.get(i).toString().contentEquals(item.toString())) {
                return i;
            } else {
                i = i + 1;
            }
        }
        return -1;
    }
}
