package de.rub.bi.inf.openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.IIFCModel;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Filters a IFC model and retrieves all elements of a certain type.
 * 
 * @author Marcel Stepien
 *
 */
public class FilterByElement extends AbstractFunction {

	public FilterByElement(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {

		String className = getInput(0);
		Class<?> theClass;
		try {
			
			theClass = Class.forName("com.apstex.ifctoolbox.ifc." + className);
			Collection<?> objects = ifcModel.getStepModel().getCollection(theClass);
			
			setResult(0, new ArrayList(objects));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
