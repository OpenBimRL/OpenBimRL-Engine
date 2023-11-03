package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Reverses the map, making values to keys and keys to values.
 *
 * @author Marcel Stepien
 */
public class MapInvert extends AbstractFunction {

    public MapInvert(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        Map<?, ?> map = getInput(0);
        if (map == null)
            return;


        LinkedHashMap<Object, Object> mapInverted = new LinkedHashMap<Object, Object>();

        for (Object k : map.keySet()) {

            Object v = map.get(k);

            if (v instanceof ArrayList<?>) {

                for (Object vi : (ArrayList<?>) v) {
                    if (mapInverted.get(vi) == null) {
                        mapInverted.put(vi, new ArrayList<Object>());
                    }
                    ((ArrayList<Object>) mapInverted.get(vi)).add(k);
                }

            } else if (v instanceof HashSet<?>) {

                for (Object vi : (HashSet<?>) v) {
                    if (mapInverted.get(vi) == null) {
                        mapInverted.put(vi, new HashSet<Object>());
                    }
                    ((HashSet<Object>) mapInverted.get(vi)).add(k);
                }

            } else {
                mapInverted.put(v, k);
            }

        }

        setResult(0, mapInverted);

    }

}
