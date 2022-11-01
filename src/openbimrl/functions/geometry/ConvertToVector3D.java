package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.geometry.euclidean.threed.Vector3D;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.javax.vecmath.Point3d;
import com.apstex.javax.vecmath.Point3f;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

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
	public void execute(ApplicationModelNode ifcModel) {
	
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
		ArrayList<Object> result = new ArrayList<Object>();
		
		for(Object o : points) {
			
			if(o instanceof Point3d) {
				
				Vector3D adjPoint = Vector3D.of(
						((Point3d)o).getX(), 
						((Point3d)o).getY(), 
						zPos != null ? zPos : ((Point3d)o).getZ()
				);
				
				result.add(
						adjPoint
				);
			}
			
			if(o instanceof Point3f) {
				
				Vector3D adjPoint = Vector3D.of(
						((Point3f)o).getX(), 
						((Point3f)o).getY(), 
						zPos != null ? zPos : ((Point3f)o).getZ()
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
