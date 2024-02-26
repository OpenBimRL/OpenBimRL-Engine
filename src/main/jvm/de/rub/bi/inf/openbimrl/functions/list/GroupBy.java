package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Groups a list of items by key and value pairs.
 *
 * @author Marcel Stepien
 */
public class GroupBy extends AbstractFunction {

    public GroupBy(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        ArrayList<?> list = getInput(0);

        ArrayList<?> referenceList = getInput(1);

        if (list.size() != referenceList.size())
            return;

        HashMap<Object, ArrayList<Object>> bins = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Object key = referenceList.get(i);

            final var bin = bins.computeIfAbsent(key, k -> new ArrayList<>());
            bin.add(list.get(i));
        }

        //Sortierung wiederherstellen
        final var values = new ArrayList<>(list.size());
        final var keys = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++) {
            Object key = referenceList.get(i);
            keys.add(key);
            values.add(bins.get(key));
        }


        setResult(0, keys);
        setResult(1, values);
    }

}
