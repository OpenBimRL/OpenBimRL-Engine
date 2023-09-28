package de.rub.bi.inf.openbimrl.functions.ifc;

import com.apstex.gui.core.model.applicationmodel.IIFCModel;
import com.apstex.ifctoolbox.ifcmodel.IfcModel;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Filters a IFC model and retrieves the element of a certain GUID.
 * 
 * @author Marcel Stepien
 *
 */
public class FilterByGUID extends AbstractFunction {

	public FilterByGUID(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {

		String guid = getInput(0);
		
		Object object = ((IfcModel)ifcModel.getStepModel()).getObjectByID(guid);
				
		setResult(0, object);
	}

}
