package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.Map;

/**
 * Retrieves the keyset of a assigned map.
 *
 * @author Marcel Stepien
 */
public class MapKeySet extends AbstractFunction {

    public MapKeySet(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        Map<?, ?> map = getInput(0);
        if (map == null)
            return;

        setResult(0, map.keySet());

    }

}
