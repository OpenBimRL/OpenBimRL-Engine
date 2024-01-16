package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.NativeFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Filters an IFC model and retrieves all elements of a certain type.
 *
 * @author Marcel Stepien
 */
public class FilterByElement extends NativeFunction {

    public FilterByElement(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void executeNative() {
        getNativeLib().filterByElement();
    }
}
