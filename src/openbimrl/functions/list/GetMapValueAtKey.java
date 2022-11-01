package openbimrl.functions.list;

import java.util.Map;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Retrieve the value in a map by key.
 * 
 * @author Marcel Stepien
 *
 */
public class GetMapValueAtKey extends AbstractFunction {

	public GetMapValueAtKey(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		Map<?, ?> map = getInput(0);
		
		Object key = getInput(1);
		
		Object valueAtKey = map.get(key);
		
		setResult(0, valueAtKey);
	}

}
