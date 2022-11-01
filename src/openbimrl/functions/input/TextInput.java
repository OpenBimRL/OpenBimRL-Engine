package openbimrl.functions.input;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * An simple node holding a text value and displays it as a label.
 * 
 * @author Marcel Stepien
 *
 */
public class TextInput extends AbstractFunction {

	public TextInput(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		Object value = nodeProxy.getNode().getOutputs().getOutput().get(0).getValue();
		setResult(0, value);
	}

}
