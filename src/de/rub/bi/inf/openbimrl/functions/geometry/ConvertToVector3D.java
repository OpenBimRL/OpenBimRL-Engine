package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Coverts a Point3d to Vector3D. An z-offset can be specified, if necessary.
 * 
 * @author Marcel Stepien
 *
 */
public class ConvertToVector3D extends AbstractFunction {
	
	public ConvertToVector3D(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		Collection<?> points = null;
		if(input0 instanceof Collection<?>) {
			points = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<>();
			newList.add(input0);
			points = newList;
		}
		
		String input1 = getInput(1);
		Double zPos = null;
		if(input1 != null)
			zPos = Double.parseDouble(input1);
		
		ArrayList<Object> listOfNodes = convert(points, zPos);
		
		if(listOfNodes.size() == 1) {
			setResult(0, listOfNodes.get(0));
		} else {
			setResult(0, listOfNodes);
		}

	}
	
	public ArrayList<Object> convert(Collection<?> points, Double zPos){
		ArrayList<Object> result = new ArrayList<>();
		
		for(Object o : points) {
			
			if(o instanceof Point3d) {
				
				Vector3D adjPoint = Vector3D.of(
						((Point3d) o).x,
						((Point3d) o).y,
						zPos != null ? zPos : ((Point3d) o).z
				);
				
				result.add(
						adjPoint
				);
			}
			
			if(o instanceof Point3f) {
				
				Vector3D adjPoint = Vector3D.of(
						((Point3f) o).x,
						((Point3f) o).y,
						zPos != null ? zPos : ((Point3f) o).z
				);
				
				result.add(
						adjPoint
				);
			}
			
			if(o instanceof Collection) {
				result.add(
					convert((Collection) o, 
					zPos
				));
			}
		}
		return result;
	}

}
