package openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Removes elements from list B in the list A.
 * 
 * @author Marcel Stepien
 *
 */
public class RemoveFromList extends AbstractFunction {

	public RemoveFromList(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		
		Collection<?> elements0 = null;
		if(input0 instanceof Collection<?>) {
			elements0 = (Collection<?>) input0;
		}else {
			ArrayList<Object> i0 = new ArrayList<Object>();
			i0.add(input0);
			elements0 = i0;
		}
		
		Collection<?> elements1 = null;
		if(input1 instanceof Collection<?>) {
			elements1 = (Collection<?>) input1;
		}else {
			ArrayList<Object> i1 = new ArrayList<Object>();
			i1.add(input1);
			elements1 = i1;
		}
		
		ArrayList<Object> temp = new ArrayList<Object>(elements0);
		temp.removeAll(elements1);
		
		setResult(0, temp);
	}

}
