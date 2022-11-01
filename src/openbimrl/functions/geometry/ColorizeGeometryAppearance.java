package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.j3d.model.cadobjectmodel.MultiAppearanceShape3D;
import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.javax.media.j3d.Appearance;
import com.apstex.javax.media.j3d.ColoringAttributes;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Colorizes the Appearance of a MultiAppearanceShape3D to a specific color.
 * 
 * @author Marcel Stepien
 *
 */
public class ColorizeGeometryAppearance extends AbstractFunction {

	public ColorizeGeometryAppearance(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		Object input2 = getInput(2);
		Object input3 = getInput(3);
		
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
		
		for(Object o : objects) {
			
			if(o instanceof MultiAppearanceShape3D) {
				MultiAppearanceShape3D shape = ((MultiAppearanceShape3D)o);
				
				shape.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
				
				shape.getAppearance().setColoringAttributes(
					new ColoringAttributes(
						Float.parseFloat(input1.toString()), 
						Float.parseFloat(input2.toString()), 
						Float.parseFloat(input3.toString()), 
						1
					)
				);
				
			}
			
		}
		
		setResult(0, input0);
	}

}
