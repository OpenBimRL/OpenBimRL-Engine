package de.rub.bi.inf.openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.j3d.model.cadobjectmodel.MultiAppearanceShape3D;
import com.apstex.gui.core.model.applicationmodel.IIFCModel;
import com.apstex.gui.core.model.cadobjectmodel.CadObject;
import com.apstex.gui.core.model.cadobjectmodel.SolidShape;
import com.apstex.javax.media.j3d.Geometry;
import com.apstex.step.core.ClassInterface;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Retrieves the geometry of an IFC element.
 * 
 * @author Marcel Stepien
 *
 */
public class GetGeometry extends AbstractFunction {

	public GetGeometry(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
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
		
		ArrayList<Geometry> resultValues = new ArrayList<Geometry>();
		
		for(Object o : objects) {
			ClassInterface classInterface = (ClassInterface) o;
			CadObject cad = ifcModel.getCadObjectModel().getCadObject(classInterface);
			
			if(cad != null) {
				for(SolidShape shape : cad.getSolidShapes()) {
					MultiAppearanceShape3D sShape = ((MultiAppearanceShape3D)shape);
					resultValues.add(sShape.getGeometry());					
				}
			}else {
				System.err.println("No geometry for: " + classInterface.getClassName() + " [in line " + classInterface.getStepLineNumber() + "]");
			}
			
		}
		
		if(resultValues.size()==1) {
			setResult(0, resultValues.get(0));
		} else {
			setResult(0, resultValues);
		}

	}

}
