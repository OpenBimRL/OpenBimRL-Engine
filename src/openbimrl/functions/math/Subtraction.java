package openbimrl.functions.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Performes an subtraction operator given two number values.
 * 
 * @author Marcel Stepien
 *
 */
public class Subtraction extends AbstractFunction {

	public Subtraction(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		Object object0 = getInput(0);
		Object object1 = getInput(1);
		
		ArrayList<Double> results = new ArrayList<Double>();
		if( object0 instanceof List && 
			object1 instanceof List && 
			(((Collection)object0).size() == ((Collection)object1).size()) ) {
			
			for(int i = 0; i < ((Collection)object0).size(); i++) {
				results.add(
						Double.valueOf(((List)object0).get(i).toString()) - Double.valueOf(((List)object1).get(i).toString())
				);
			}
			
		}else if(object0 instanceof List && !(object1 instanceof List)) {
			for(int i = 0; i < ((Collection)object0).size(); i++) {
				results.add(
						Double.valueOf(((List)object0).get(i).toString()) - Double.valueOf(object1.toString())
				);
			}
		}else if(object1 instanceof List && !(object0 instanceof List)) {
			for(int i = 0; i < ((Collection)object1).size(); i++) {
				results.add(
						Double.valueOf(object0.toString()) - Double.valueOf(((List)object1).get(i).toString())
				);
			}
		}else {
			Double operand0 = Double.valueOf(object0.toString());
			Double operand1 = Double.valueOf(object1.toString());
			results.add(operand0 - operand1);
		}
		
		if(results.size() == 1) {			
			setResult(0, results.get(0));
		}else {
			setResult(0, results);
		}
	}

}
