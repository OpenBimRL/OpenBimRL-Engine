package de.rub.bi.inf.openbimrl.functions.input;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

/**
 * A simple node holding a text value and displays it as a label.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction(type = "inputType")
public class TextInput extends AbstractFunction {

    public TextInput(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @FunctionOutput(position = 0)
    private String string;

    @Override
    public void execute() {
        string = nodeProxy.getNode().getOutputs().getOutput().get(0).getValue();
    }

}
