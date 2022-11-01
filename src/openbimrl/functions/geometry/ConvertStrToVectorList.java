package openbimrl.functions.geometry;

import java.util.ArrayList;

import org.apache.commons.geometry.euclidean.threed.Vector3D;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Converts a String to a Vector3D list. (Example: x,y,z;x,y,z;... )
 * 
 * @author Marcel Stepien
 *
 */
public class ConvertStrToVectorList extends AbstractFunction {
	
	public ConvertStrToVectorList(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		
		String str = null;
		if(input0 instanceof String) {
			str = (String)input0;
		}else {
			return;
		}
		
		ArrayList<Vector3D> points = new ArrayList<>();
		
		String[] strPoints = str.split(";");

		for(String strPoint : strPoints) {
			
			String[] strPointCoords = strPoint.split(",");
			
			double xCoord = Double.parseDouble(strPointCoords[0]);
			double yCoord = Double.parseDouble(strPointCoords[1]);
			double zCoord = Double.parseDouble(strPointCoords[2]);
			
			points.add(Vector3D.of(xCoord, yCoord, zCoord));
		}
		
		setResult(0, points);
		
	}
	
}
