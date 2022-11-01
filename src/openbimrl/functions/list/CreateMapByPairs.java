package openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Creates a map of elements, if a key and value list is provided.
 * 
 * @author Marcel Stepien
 *
 */
public class CreateMapByPairs extends AbstractFunction {

	public CreateMapByPairs(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		ArrayList<?> keys = new ArrayList();
		keys.addAll((Collection)getInput(0));
		
		ArrayList<?> values = new ArrayList();
		values.addAll((Collection)getInput(1));
		
		LinkedHashMap<Object, List<Object>> map = new LinkedHashMap<Object, List<Object>>();
		
		if(keys.size() != values.size())
			return;

		for(int i = 0; i < keys.size(); i++) {
			Object key = keys.get(i);
			Object value = values.get(i);
			
			if(key == null) {
				key = "undefined";
			}
			
			if(map.get(key) == null) {
				map.put(key, new ArrayList<Object>());
			}
			
			if(value instanceof Collection) {
				map.get(key).addAll((Collection)value);
			}else {				
				map.get(key).add(value);
			}
			
		}
		
		setResult(0, map.keySet());
		setResult(1, map.values());
		setResult(2, map);
	}

}
