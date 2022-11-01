package openbimrl.functions.math;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Performes an subtraction operator given two number values.
 * 
 * @author Marcel Stepien
 *
 */
public class Substraction extends AbstractFunction {

	public Substraction(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		Object object0 = getInput(0);
		Object object1 = getInput(1);
		
		Double operand0 = (Double) object0;
		Double operand1 = (Double) object1;
		
		setResult(0, operand0+operand1);
	}

}
