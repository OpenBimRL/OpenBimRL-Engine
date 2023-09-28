package de.rub.bi.inf.openbimrl.functions.list;

import java.util.ArrayList;

import com.apstex.gui.core.model.applicationmodel.IIFCModel;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * 
 * @author Marcel Stepien
 *
 */
public class RepeatAsList extends AbstractFunction {

	public RepeatAsList(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {

		Object object0 = getInput(0);
		Object object1 = getInput(1);
		
		int counter = Integer.parseInt(object1.toString());
		
		ArrayList<Object> results = new ArrayList();
		for(int i = 0; i < counter; i++) {
			results.add(object0);	
		}
		
		setResult(0, results);
		
	}

}
