package openbimrl.functions.list;

import java.util.ArrayList;
import java.util.HashMap;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Groups a list of items by key and value pairs.
 * 
 * @author Marcel Stepien
 *
 */
public class GroupBy extends AbstractFunction {

	public GroupBy(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		ArrayList<?> list = getInput(0);
		
		ArrayList<?> referenceList = getInput(1);
		
		if(list.size() != referenceList.size())
			return;
	
		HashMap<Object, ArrayList<Object>> bins = new HashMap<>();
		
		for(int i=0; i<list.size(); i++) {
			Object key = referenceList.get(i);
			
			ArrayList<Object> bin = bins.get(key);
			if(bin==null) {
				bin = new ArrayList<>();
				bins.put(key, bin);
			}
			bin.add(list.get(i));
		}
		
		//Sortierung wiederherstellen
		ArrayList<Object> values = new ArrayList<>(list.size());
		ArrayList<Object> keys = new ArrayList<>(list.size());
		
		for (int i=0; i<list.size(); i++) {
			Object key = referenceList.get(i);
			keys.add(key);
			values.add(bins.get(key));
		}
		
	
		setResult(0, keys);
		setResult(1, values);
	}

}
