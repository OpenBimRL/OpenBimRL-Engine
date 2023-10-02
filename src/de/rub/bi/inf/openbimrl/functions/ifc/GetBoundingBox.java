package de.rub.bi.inf.openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import javax.media.j3d.Bounds;
import javax.vecmath.Point3d;

/**
 * Retrieves the BoundingBox of an IFC element.
 * 
 * @author Marcel Stepien
 *
 */
public class GetBoundingBox extends AbstractFunction {

	public GetBoundingBox(NodeProxy nodeProxy) {
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
		
		ArrayList<Bounds> resultValues = new ArrayList<Bounds>();
		ArrayList<Point3d> centerValues = new ArrayList<Point3d>();
		
		for(Object o : objects) {
			ClassInterface classInterface = (ClassInterface) o;
			CadObject cad = ifcModel.getCadObjectModel().getCadObject(classInterface);
			
			Point3d center = new Point3d();
			Bounds b = cad.getBoundingBox();
			b.getCenter(center);
			
			resultValues.add(b);
			centerValues.add(center);
		}
		
		if(resultValues.size()==1) {
			setResult(0, resultValues.get(0));
		} else {
			setResult(0, resultValues);
		}

		if(centerValues.size()==1) {
			setResult(1, centerValues.get(0));
		} else {
			setResult(1, centerValues);
		}
	}

}
