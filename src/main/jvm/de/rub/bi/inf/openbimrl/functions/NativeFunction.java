package de.rub.bi.inf.openbimrl.functions;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

import de.rub.bi.inf.openbimrl.NodeProxy;

public abstract class NativeFunction extends AbstractFunction {
    public NativeFunction(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    public static class FunctionParams extends Structure {
        public int lenght;
        public boolean isArray;
        public Object[] data;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "lenght", "isArray", "data" });
        }
    }
}
