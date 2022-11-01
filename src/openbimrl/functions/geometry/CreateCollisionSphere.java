package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.shape.Sphere;
import org.apache.commons.numbers.core.Precision;

import com.apstex.gui.core.j3d.model.cadobjectmodel.MultiAppearanceShape3D;
import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.gui.core.model.cadobjectmodel.CadObject;
import com.apstex.gui.core.model.cadobjectmodel.SolidShape;
import com.apstex.javax.media.j3d.GeometryArray;
import com.apstex.javax.vecmath.Point3d;
import com.apstex.step.core.ClassInterface;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Creates a spherical geometrie as RegionalBSPTree3D in provided precision for collision detection purposes.
 * 
 * @author Marcel Stepien
 *
 */
public class CreateCollisionSphere extends AbstractFunction {

	public CreateCollisionSphere(NodeProxy nodeProxy) {
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
		
		String input1 = getInput(1);
		
		if(input1 == null)
			return;
		
		double radius = Double.parseDouble(input1);
		
		String input2 = getInput(2);
		
		if(input2 == null)
			return;
		
		int subdivisions = Integer.parseInt(input2);
		
		double precision = 1e-14;
		ArrayList<RegionBSPTree3D> resultValues = new ArrayList<RegionBSPTree3D>();
		
		for(Object o : objects) {
			ClassInterface classInterface = (ClassInterface) o;
			CadObject cad = ifcModel.getCadObjectModel().getCadObject(classInterface);
			
			for(SolidShape shape : cad.getSolidShapes()) {
				MultiAppearanceShape3D sShape = ((MultiAppearanceShape3D)shape);
				
				if(sShape.getGeometry() instanceof GeometryArray) {
					//GeometryInfo gInfo = new GeometryInfo((GeometryArray)sShape.getGeometry());
					
					Point3d center = new Point3d();
					sShape.getBounds().getCenter(center);
					
					double[] centerCoords = new double[3];
					center.get(centerCoords);
					
					resultValues.add(
							Sphere.from(Vector3D.of(centerCoords), 
							radius, 
							Precision.doubleEquivalenceOfEpsilon(precision)
						).toTree(subdivisions));
					
				}
				
			}
			
		}
		
		if(resultValues.size()==1) {
			setResult(0, resultValues.get(0));
		} else {
			setResult(0, resultValues);
		}

	}

}
