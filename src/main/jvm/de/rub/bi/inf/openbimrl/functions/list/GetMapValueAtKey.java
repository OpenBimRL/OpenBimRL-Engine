package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.Map;

/**
 * Retrieve the value in a map by key.
 *
 * @author Marcel Stepien
 */
public class GetMapValueAtKey extends AbstractFunction {

    public GetMapValueAtKey(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        Map<?, ?> map = getInput(0);

        Object key = getInput(1);

        Object valueAtKey = map.get(key);

        setResult(0, valueAtKey);
    }

}
