package openbimrl.functions.list;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Counts the number of objects contained in the Map values and generates a new map based on that.
 * 
 * @author Marcel Stepien
 *
 */
public class MapValueCount extends AbstractFunction {

	public MapValueCount(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		
		Map<?,?> map = getInput(0);
		if(map == null)
			return;

		
		LinkedHashMap<Object, Object> mapCounter = new LinkedHashMap<Object, Object>();
		
		for(Object k : map.keySet()) {
			
			Object v = map.get(k);
			
			if(v instanceof Collection<?>) {
				mapCounter.put(k, ((Collection<?>)v).size());
			}else {
				mapCounter.put(v, 1);
			}
			
		}
		
		setResult(0, mapCounter);
		
	}

}
