package openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Joins two collections to one ArrayList.
 * 
 * @author Marcel Stepien
 *
 */
public class JoinCollections extends AbstractFunction {

	public JoinCollections(NodeProxy nodeProxy) {
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
			ArrayList<Object> temp = new ArrayList<>();
			temp.add(input0);
			elements0 = temp;
		}
		
		Collection<?> elements1 = null;
		if(input1 instanceof Collection<?>) {
			elements1 = (Collection<?>) input1;
		}else {
			ArrayList<Object> temp = new ArrayList<>();
			temp.add(input1);
			elements1 = temp;
		}
		
		ArrayList<Object> temp = new ArrayList<>();
		temp.addAll(elements0);
		temp.addAll(elements1);
		
		setResult(0, temp);
	}

}
