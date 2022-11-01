package openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.gui.core.model.cadobjectmodel.CadObject;
import com.apstex.javax.vecmath.Point3d;
import com.apstex.step.core.ClassInterface;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Calculates the height given a list of entities.
 * 
 * @author Marcel Stepien
 *
 */
public class GetHeight extends AbstractFunction {

	public GetHeight(NodeProxy nodeProxy) {
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
		
		ArrayList<Object> resultValues0 = new ArrayList<>();
		ArrayList<Object> resultValues1 = new ArrayList<>();
		
		for(Object o : objects) {
			ClassInterface classInterface = (ClassInterface) o;
			CadObject cad = ifcModel.getCadObjectModel().getCadObject(classInterface);
			
			Point3d lower = new Point3d();
			cad.getBoundingBox().getLower(lower);
			
			Point3d upper = new Point3d();
			cad.getBoundingBox().getUpper(upper);
			
			resultValues0.add(lower.getZ());
			resultValues1.add(upper.getZ());
		}
		
		if(resultValues0.size()==1 && resultValues1.size() == 1) {
			setResult(0, resultValues0.get(0));
			setResult(1, resultValues1.get(0));
		} else {
			setResult(0, resultValues0);
			setResult(1, resultValues1);
		}

	}

}
