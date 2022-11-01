package openbimrl.functions.list;

import java.util.Map;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Retrieves the values of a assigned map.
 * 
 * @author Marcel Stepien
 *
 */
public class MapValues extends AbstractFunction {

	public MapValues(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		
		Map<?,?> map = getInput(0);
		if(map == null)
			return;
		
		setResult(0, map.values());
		
	}

}
