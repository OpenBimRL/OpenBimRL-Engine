package openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Sorts a list of items in ASC or DESC order.
 * 
 * @author Marcel Stepien
 *
 */
public class OrderBy extends AbstractFunction {

	public OrderBy(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		
		Collection<?> list = getInput(0);
		
		Collection<?> referenceList = getInput(1);
		
		if(list.size()!=referenceList.size())
			return;
		
		ArrayList<Object> _list = new ArrayList<>(list.size());
		_list.addAll(list);
		
		ArrayList<Object> resultValues1 = new ArrayList<>(referenceList.size());
		resultValues1.addAll(referenceList);
		
		TreeMap<Object, Integer> oldPositions = new TreeMap<>();
		for(int i=0; i<resultValues1.size(); i++) {
			oldPositions.put(resultValues1.get(i), i);
		}
		
		String order = getInput(2);
		if(order!=null && order.toLowerCase().equals("desc")) {
			resultValues1 = resultValues1.stream().sorted(Collections.reverseOrder()).collect(Collectors.toCollection(ArrayList::new)); 
		}else {
			resultValues1 = resultValues1.stream().sorted().collect(Collectors.toCollection(ArrayList::new));
		}
		
		ArrayList<Object> resultValues0 = new ArrayList<>(list);
		for (int i = 0; i<resultValues1.size(); i++) {
			Object o = resultValues1.get(i);
			int oldPos = oldPositions.get(o);
			resultValues0.set(i, _list.get(oldPos));
		}
		
		setResult(0, resultValues0);
		setResult(1, resultValues1);
		
	}

}
