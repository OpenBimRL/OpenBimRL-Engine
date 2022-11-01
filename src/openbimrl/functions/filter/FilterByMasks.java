package openbimrl.functions.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Filters a list of elements by a list of of masking boolean values.
 * 
 * @author Marcel Stepien
 *
 */
public class FilterByMasks extends AbstractFunction {

	public FilterByMasks(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		
		if(input0 == null || input1 == null)
			return;
		
		ArrayList<?> elements = null;
		if(input0 instanceof Collection<?>) {
			elements = new ArrayList((Collection<?>) input0);
		}

		LinkedHashMap<Object, ArrayList<Boolean>> filterMap = new LinkedHashMap<Object, ArrayList<Boolean>>();
		
		if(input1 instanceof ArrayList) {
			ArrayList<Boolean> mask = (ArrayList<Boolean>) input1;
			filterMap.put("0", (ArrayList<Boolean>)mask);
		}
		
		if(input1 instanceof LinkedHashMap) {
			filterMap = (LinkedHashMap<Object, ArrayList<Boolean>>)input1;
		}
		

		if(elements == null || filterMap == null)
			return;
		
		LinkedHashMap<Object, ArrayList<?>> resultValues = new LinkedHashMap<Object, ArrayList<?>>();
		
		for(Object maskKey : filterMap.keySet()) {
			ArrayList<?> mask = filterMap.get(maskKey);
			
			ArrayList filteredList = new ArrayList();
			for(int i = 0; i < mask.size(); i++) {
				boolean flag = mask.get(i) != null ? Boolean.parseBoolean(mask.get(i).toString()) : false;
				if(flag) {
					filteredList.add(elements.get(i));
				}
			}
			
			resultValues.put(maskKey, filteredList);
		}
		
		if(resultValues.keySet().size() == 1) {
			Object firstKey = resultValues.keySet().toArray()[0];
			setResult(0, resultValues.get(firstKey));	
		}else {			
			setResult(0, resultValues);
		}
	}

}
