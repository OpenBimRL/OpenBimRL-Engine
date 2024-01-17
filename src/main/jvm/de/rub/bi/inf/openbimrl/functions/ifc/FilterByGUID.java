package de.rub.bi.inf.openbimrl.functions.ifc;

import com.sun.jna.Pointer;
import de.rub.bi.inf.nativelib.IfcPointer;
import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.NativeFunction;
import org.jetbrains.annotations.NotNull;

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
        getNativeLib().filterByGUID();
    }

    @Override
    protected void handlePointerOutput(int at, @NotNull Pointer pointer) {
        super.handlePointerOutput(at, new IfcPointer(pointer));
    }
}
