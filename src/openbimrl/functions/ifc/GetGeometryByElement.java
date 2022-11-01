package openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.apstex.gui.core.j3d.model.cadobjectmodel.MultiAppearanceShape3D;
import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.gui.core.model.cadobjectmodel.CadObject;
import com.apstex.gui.core.model.cadobjectmodel.SolidShape;
import com.apstex.javax.media.j3d.Geometry;
import com.apstex.step.core.ClassInterface;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Retrieves the geometry and groups it, based on the IFC element.
 * 
 * @author Marcel Stepien
 *
 */
public class GetGeometryByElement extends AbstractFunction {

	public GetGeometryByElement(NodeProxy nodeProxy) {
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
		
		ArrayList<List<Geometry>> resultValues = new ArrayList<List<Geometry>>();
		
		for(Object o : objects) {
			ClassInterface classInterface = (ClassInterface) o;
			CadObject cad = ifcModel.getCadObjectModel().getCadObject(classInterface);
			
			ArrayList<Geometry> geoGroup = new ArrayList<Geometry>();
			for(SolidShape shape : cad.getSolidShapes()) {
				MultiAppearanceShape3D sShape = ((MultiAppearanceShape3D)shape);
				geoGroup.add(sShape.getGeometry());
			}
			resultValues.add(geoGroup);
			
		}
		
		if(resultValues.size()==1) {
			setResult(0, resultValues.get(0));
		} else {
			setResult(0, resultValues);
		}

	}

}
