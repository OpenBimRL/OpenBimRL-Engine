package de.rub.bi.inf.openbimrl.functions;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;

import com.sun.jna.Pointer;

import de.rub.bi.inf.nativelib.FunctionsLibrary;
import de.rub.bi.inf.nativelib.FunctionsNative;
import de.rub.bi.inf.nativelib.IfcPointer;

public abstract class NativeFunction extends AbstractFunction {
    protected static FunctionsLibrary nativeLib;

    public NativeFunction(NodeProxy nodeProxy) {
        super(nodeProxy);
        if (nativeLib == null)
            nativeLib = FunctionsNative.getInstance();
    }

    public abstract void executeNative();

    public <T> T getInputAs(int at, Class<T> clazz) {
        final var output = getInput(at);
        try {
            return clazz.isInstance(output) ? clazz.cast(output) : null;
        } catch (ClassCastException e) { // ignore error
            return null;
        }
    }

    @Override
    public final void execute(IIFCModel ifcModel) {
        nativeLib.init_function(
                at -> getInputAs(at, Pointer.class),
                at -> getInputAs(at, double.class),
                at -> getInputAs(at, int.class),
                at -> getInputAs(at, String.class),
                (at, pointer) -> setResult(at, new IfcPointer(Pointer.nativeValue(pointer))),
                this::setResult,
                this::setResult,
                this::setResult);

        executeNative();
    }
}
