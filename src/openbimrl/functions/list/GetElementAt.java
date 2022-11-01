package openbimrl.functions.list;

import java.util.ArrayList;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Retrieves an item of a specific index contained in a list of elements.
 * 
 * @author Marcel Stepien
 *
 */
public class GetElementAt extends AbstractFunction {

	public GetElementAt(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		
		ArrayList<?> list = getInput(0);
		
		String str_position = getInput(1);
		int position = Integer.valueOf(str_position);
		
		Object elementAt = list.get(position);
		
		setResult(0, elementAt);
	}

}
