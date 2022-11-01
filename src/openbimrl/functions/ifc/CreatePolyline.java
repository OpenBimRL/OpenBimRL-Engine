package openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.geometry.euclidean.threed.Vector3D;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.ifctoolbox.ifc.IfcCartesianPoint;
import com.apstex.ifctoolbox.ifc.IfcLengthMeasure;
import com.apstex.ifctoolbox.ifc.IfcPolyline;
import com.apstex.step.core.ClassInterface;
import com.apstex.step.core.LIST;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Creates a polyline based on a list of Vector3D.
 * 
 * @author Marcel Stepien
 *
 */
public class CreatePolyline extends AbstractFunction {
	
	private ArrayList<ClassInterface> memory = new ArrayList<ClassInterface>();
	
	public CreatePolyline(NodeProxy nodeProxy) {
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
		
		//reset Memory
		this.memReset(ifcModel);

		LIST<IfcCartesianPoint.Ifc4> points = new LIST<IfcCartesianPoint.Ifc4>();

		for(Object obj : objects) {
			
			Vector3D point = null;
			
			if(obj instanceof Vector3D) {
				point = (Vector3D)obj;
			}else {
				continue;
			}
			
			LIST<IfcLengthMeasure.Ifc4> measures = new LIST<IfcLengthMeasure.Ifc4>();
			measures.add(new IfcLengthMeasure.Ifc4(point.getX()));
			measures.add(new IfcLengthMeasure.Ifc4(point.getY()));
			measures.add(new IfcLengthMeasure.Ifc4(point.getZ()));
			
			IfcCartesianPoint.Ifc4 cPoint = new IfcCartesianPoint.Ifc4.Instance(measures);
			ifcModel.getStepModel().addObject(cPoint);
			memory.add(cPoint);
			
			points.add(cPoint);
		}
		
		IfcPolyline polyline = new IfcPolyline.Ifc4.Instance(points);
		ifcModel.getStepModel().addObject(polyline);
		memory.add(polyline);
		
		setResult(0, polyline);
		
	}
	
	private void memReset(ApplicationModelNode ifcModel) {
		for(ClassInterface obj : memory) {
			ifcModel.getStepModel().removeObject(obj);
		}
		memory = new ArrayList<ClassInterface>();
	}
	
}
