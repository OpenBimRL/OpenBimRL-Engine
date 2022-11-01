package openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Flattens the hierarchy of an Collection by one level.
 * 
 * @author Marcel Stepien
 *
 */
public class FlattenCollection extends AbstractFunction {

	public FlattenCollection(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		
		Object input0 = getInput(0);
		if(input0 == null) {
			return;
		}
		
		Collection result = flattenListOfListsImperatively((Collection<Collection>)input0);
		
		setResult(0, result);
	}
	
	
	public Collection flattenListOfListsImperatively(Collection<Collection> nestedList) {
		Collection ls = new ArrayList<>();
	    nestedList.forEach(ls::addAll);
	    return ls;
	}

}
