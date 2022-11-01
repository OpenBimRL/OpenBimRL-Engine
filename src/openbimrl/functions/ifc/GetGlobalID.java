package openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.gui.core.model.cadobjectmodel.CadObject;
import com.apstex.ifctoolbox.ifc.IfcObject;
import com.apstex.javax.vecmath.Point3d;
import com.apstex.step.core.ClassInterface;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Retrives the GUID of an IFC element.
 * 
 * @author Marcel Stepien
 *
 */
public class GetGlobalID extends AbstractFunction {

	public GetGlobalID(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		Collection<?> objects = null;
		if(input0 instanceof Collection<?>) {
			objects = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input0);
			objects = newList;
		}
		
		ArrayList<String> resultValues = new ArrayList<>();
		
		for(Object o : objects) {
			if(o instanceof IfcObject) {
				IfcObject ifcObj = (IfcObject) o;
				
				resultValues.add(
						ifcObj.getGlobalId().getDecodedValue()
				);
				
			}
		}
		
		if(resultValues.size()==1) {
			setResult(0, resultValues.get(0));
		} else {
			setResult(0, resultValues);
		}

	}

}
