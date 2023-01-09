package openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Arrays;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * 
 * @author Marcel Stepien
 *
 */
public class AsList extends AbstractFunction {

	public AsList(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		String delimiterDim1 = ",";
		String delimiterDim2 = ";";
		
		ArrayList<ArrayList<Object>> dim2Results = new ArrayList();
		for(String dim1Str : getInput(0).toString().split(delimiterDim2)) {
			ArrayList<Object> dim1Results = new ArrayList();
			if(dim1Str != null) {
				dim1Results.addAll(
					Arrays.asList(dim1Str.split(delimiterDim1))
				);
			}
			dim2Results.add(dim1Results);
		}

		if(dim2Results.size() == 1) {			
			setResult(0, dim2Results.get(0));
		}else {
			setResult(0, dim2Results);
		}
	}

}
