package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Generates a filter mask by number of objects contained in the Map values.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class MapFilterByCount extends AbstractFunction {

    public MapFilterByCount(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Map<?, ?> map = getInput(0, Map.class);
        if (map == null)
            return;

        String counterStr = getInput(1, String.class);
        int counter = 0;
        if (counterStr != null)
            counter = Integer.parseInt(counterStr);

        ArrayList<Boolean> filter = new ArrayList<Boolean>();

        for (Object k : map.keySet()) {
            Object v = map.get(k);

            boolean flag = false;

            if (v instanceof Collection<?>) {
                if (((Collection<?>) v).size() >= counter) {
                    flag = true;
                }
            } else if (v != null) {
                if (1 >= counter) {
                    flag = true;
                }
            } else {
                if (0 >= counter) {
                    flag = true;
                }
            }

            filter.add(flag);

        }

        setResult(0, filter);

    }

}
