package de.rub.bi.inf.openbimrl.functions.ifc;

import com.apstex.gui.core.model.applicationmodel.IIFCModel;
import com.apstex.ifctoolbox.ifcmodel.IfcModel;
import com.apstex.step.core.ClassInterface;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Retrieves the IFC entity of a specific id from the model.
 * 
 * @author Marcel Stepien
 *
 */
public class GetElementById extends AbstractFunction {

	public GetElementById(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
		String guid = getInput(0);
		
		ClassInterface object = ((IfcModel)ifcModel.getStepModel()).getObjectByID(guid);
		
		setResult(0, object);
	}

}
