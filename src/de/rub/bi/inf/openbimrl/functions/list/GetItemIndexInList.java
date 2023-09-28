package de.rub.bi.inf.openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.IIFCModel;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * 
 * @author Marcel Stepien
 *
 */
public class GetItemIndexInList extends AbstractFunction {

	public GetItemIndexInList(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
		
		ArrayList collection = new ArrayList();
		if(getInput(0) instanceof Collection) {
			collection.addAll((Collection)getInput(0));
		}else {
			return;
		}
		
		ArrayList<Object> values = new ArrayList<Object>();
		if(getInput(1) instanceof Collection) {
			for(Object i : (Collection)getInput(1)) {
				//System.out.println(collection);
				//System.out.println(i);
				
				int index = findIndex(collection, i);
				values.add(index);
			}
		}

		//System.out.println("B: " + values.toString());
		if(values.size() == 1) {			
			setResult(0, values.get(0));
		}else {
			setResult(0, values);
		}
	}
	
	// Linear-search function to find the index of an element
	public int findIndex(ArrayList list, Object item){
		// if array is Null
		if (list == null) {
			return -1;
		}
		
		
		int len = list.size();
		int i = 0;
		
		while (i < len) {
			if (list.get(i).toString().contentEquals(item.toString())) {
				return i;
			}else {
				i = i + 1;
			}
		}
		return -1;
	}
}
