package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Counts the number of objects contained in the Map values and generates a new map based on that.
 *
 * @author Marcel Stepien
 */
public class MapValueCount extends AbstractFunction {

    public MapValueCount(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Map<?, ?> map = getInput(0);
        if (map == null)
            return;


        LinkedHashMap<Object, Object> mapCounter = new LinkedHashMap<Object, Object>();

        for (Object k : map.keySet()) {

            Object v = map.get(k);

            if (v instanceof Collection<?>) {
                mapCounter.put(k, ((Collection<?>) v).size());
            } else {
                mapCounter.put(v, 1);
            }

        }

        setResult(0, mapCounter);

    }

}
