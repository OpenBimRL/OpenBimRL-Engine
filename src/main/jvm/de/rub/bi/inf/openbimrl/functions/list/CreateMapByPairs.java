package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Creates a map of elements, if a key and value list is provided.
 *
 * @author Marcel Stepien
 */
public class CreateMapByPairs extends AbstractFunction {

    public CreateMapByPairs(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        final var keys = new ArrayList<>(getInputAsCollection(0));

        final var values = new ArrayList<>(getInputAsCollection(1));

        final var map = new LinkedHashMap<Object, List<Object>>();

        if (keys.size() != values.size())
            return;

        for (int i = 0; i < keys.size(); i++) {
            Object key = keys.get(i);
            Object value = values.get(i);

            if (key == null) {
                key = "undefined";
            }

            map.computeIfAbsent(key, k -> new ArrayList<>());

            if (value instanceof Collection<?> temp) {
                map.get(key).addAll(temp);
            } else {
                map.get(key).add(value);
            }

        }

        setResult(0, map.keySet());
        setResult(1, map.values());
        setResult(2, map);
    }

}
