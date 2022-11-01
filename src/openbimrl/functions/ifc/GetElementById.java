package openbimrl.functions.ifc;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.ifctoolbox.ifcmodel.IfcModel;
import com.apstex.step.core.ClassInterface;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

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
	public void execute(ApplicationModelNode ifcModel) {
		String guid = getInput(0);
		
		ClassInterface object = ((IfcModel)ifcModel.getStepModel()).getObjectByID(guid);
		
		setResult(0, object);
	}

}
