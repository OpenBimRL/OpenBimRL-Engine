package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.NativeFunction;

/**
 * Filters an IFC model and retrieves the element of a certain GUID.
 *
 * @author Marcel Stepien
 */
public class FilterByGUID extends NativeFunction {

    public FilterByGUID(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void executeNative() {
        nativeLib.filterByGUID();
    }

}
