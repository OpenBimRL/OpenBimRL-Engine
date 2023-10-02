package de.rub.bi.inf.openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Retrieves an item of a specific index contained in a list of elements.
 * 
 * @author Marcel Stepien
 *
 */
public class GetElementAt extends AbstractFunction {

	public GetElementAt(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {

		ArrayList<?> list = getInput(0);
		
		ArrayList positions = new ArrayList();
		if(getInput(1) instanceof Collection) {
			positions.addAll((Collection)getInput(1));
		}else {
			positions.add(getInput(1));
		}
		

		ArrayList<Object> elementsAt = new ArrayList<Object>();
		if(positions instanceof Collection) {
			for(int index = 0; index < positions.size(); index++) {
				
				String strPosition = positions.get(index).toString();
				int position = Integer.parseInt(strPosition);
				
				if(positions.size() == 1) {
					Object value = list.get(position);					
					elementsAt.add(value);
				}else {
					Object values = list.get(index);
					if(values instanceof List) {
						elementsAt.add(((List)values).get(position));
					}
				}
				
			}
		}
		
		if(elementsAt.size() == 1) {			
			setResult(0, elementsAt.get(0));
		}else {
			setResult(0, elementsAt);
		}
	}

}
