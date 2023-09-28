package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;


/**
 * Checks if geometies of two RegionalBSPTree3D are intersecting.
 * 
 * @author Marcel Stepien
 *
 */
public class CheckIntersection extends AbstractFunction {
	
	public CheckIntersection(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		
		if(input0 == null || input1 == null)
			return;
		
		Collection<?> elements0 = null;
		if(input0 instanceof Collection<?>) {
			elements0 = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input0);
			elements0 = newList;
		}
		
		Collection<?> elements1 = null;
		if(input1 instanceof Collection<?>) {
			elements1 = (Collection<?>) input1;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input1);
			elements1 = newList;
		}
		
		LinkedHashMap<Object, ArrayList<Boolean>> resultValues = new LinkedHashMap<Object, ArrayList<Boolean>>();
		
		for(Object ele0 : elements0) {
			if(ele0 instanceof RegionBSPTree3D) {

				ArrayList<Boolean> filter = new ArrayList<Boolean>();
				
				for(Object ele1 : elements1) {
					if(ele1 instanceof RegionBSPTree3D) {
						
						try {
							RegionBSPTree3D regionM1 = ((RegionBSPTree3D)ele0).copy();
							RegionBSPTree3D regionM2 = ((RegionBSPTree3D)ele1).copy();
							
							regionM1.intersection(regionM2);
							if(regionM1.getSize() > 0.0) {
								filter.add(true);
							}else {
								filter.add(false);
							}
							
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
				
				resultValues.put(ele0, filter);
				
			}
		}
		
		setResult(0, resultValues);
		
	}
	
}
