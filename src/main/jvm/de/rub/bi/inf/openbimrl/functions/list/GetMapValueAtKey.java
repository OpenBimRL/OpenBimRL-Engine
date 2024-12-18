package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.Map;

/**
 * Retrieve the value in a map by key.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class GetMapValueAtKey extends AbstractFunction {

    public GetMapValueAtKey(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        Map<?, ?> map = getInput(0, Map.class);

        Object key = getInput(1, Object.class);

        if (map == null) return;

        Object valueAtKey = map.get(key);

        setResult(0, valueAtKey);
    }

}
