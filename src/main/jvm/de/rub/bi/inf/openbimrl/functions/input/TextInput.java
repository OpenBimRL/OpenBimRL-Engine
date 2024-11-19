package de.rub.bi.inf.openbimrl.functions.input;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

/**
 * A simple node holding a text value and displays it as a label.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class TextInput extends AbstractFunction {

    public TextInput(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        Object value = nodeProxy.getNode().getOutputs().getOutput().get(0).getValue();
        setResult(0, value);
    }

}
