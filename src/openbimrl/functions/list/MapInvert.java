package openbimrl.functions.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;


/**
 * Reverses the map, making values to keys and keys to values.
 * 
 * @author Marcel Stepien
 *
 */
public class MapInvert extends AbstractFunction {

	public MapInvert(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		
		Map<?,?> map = getInput(0);
		if(map == null)
			return;

		
		LinkedHashMap<Object, Object> mapInverted = new LinkedHashMap<Object, Object>();
		
		for(Object k : map.keySet()) {
			
			Object v = map.get(k);
			
			if(v instanceof ArrayList<?>) {
				
				for(Object vi : (ArrayList<?>)v) {
					if(mapInverted.get(vi) == null) {
						mapInverted.put(vi, new ArrayList<Object>());
					}
					((ArrayList<Object>)mapInverted.get(vi)).add(k);
				}
				
			}else if(v instanceof HashSet<?>) {
				
				for(Object vi : (HashSet<?>)v) {
					if(mapInverted.get(vi) == null) {
						mapInverted.put(vi, new HashSet<Object>());
					}
					((HashSet<Object>)mapInverted.get(vi)).add(k);
				}
				
			}else {
				mapInverted.put(v, k);
			}
			
		}
		
		setResult(0, mapInverted);
		
	}

}
