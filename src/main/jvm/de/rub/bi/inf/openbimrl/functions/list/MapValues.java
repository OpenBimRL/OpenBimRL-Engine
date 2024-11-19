package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.Map;

/**
 * Retrieves the values of a assigned map.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class MapValues extends AbstractFunction {

    public MapValues(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Map<?, ?> map = getInput(0, Map.class);
        if (map == null)
            return;

        setResult(0, map.values());

    }

}
