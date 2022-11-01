package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.j3d.utils.geometry.GeometryInfo;
import com.apstex.javax.media.j3d.GeometryArray;
import com.apstex.javax.vecmath.Point3f;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Checks if geometic object are close to each other based on their geometric definitions.
 * 
 * @author Marcel Stepien
 *
 */
public class CheckNeighborhoodByDistance extends AbstractFunction {

	public CheckNeighborhoodByDistance(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		Object input2 = getInput(2);
		
		if(input0 == null || input1 == null)
			return;
				
		Collection<?> geometryGroupA = null;
		if(input0 instanceof Collection<?>) {
			geometryGroupA = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input0);
			geometryGroupA = newList;
		}
		
		Collection<?> geometryGroupB = null;
		if(input1 instanceof Collection<?>) {
			geometryGroupB = (Collection<?>) input1;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input1);
			geometryGroupB = newList;
		}
		
		double distanceThreshold = 1.0;
		if(input2 != null) {
			distanceThreshold = Double.parseDouble(input2.toString());
		}
		
		
		ArrayList<List<?>> resultValues = new ArrayList<List<?>>();
		
		for(Object og1 : geometryGroupA) {
			if(og1 instanceof ArrayList<?>) {
				for(Object o1 : (ArrayList<?>)og1) {
					if(o1 instanceof GeometryArray) {
						
						GeometryInfo geoA = new GeometryInfo((GeometryArray)o1);
						geoA.recomputeIndices();
						
						ArrayList<Boolean> mask = new ArrayList<Boolean>();
						
						for(Object og2 : geometryGroupB) {
							if(og2 instanceof ArrayList<?>) {
								
								boolean flag = false;
								
								for(Object o2 : (ArrayList<?>)og2) {									
									if(o2 instanceof GeometryArray) {
										GeometryInfo geoB = new GeometryInfo((GeometryArray)o2);
										geoB.recomputeIndices();
										
										for(Point3f pA : geoA.getCoordinates()) {
											for(Point3f pB : geoB.getCoordinates()) {
												if(pA.distance(pB) <= distanceThreshold) {
													
													flag = true;
													
												}
											}
										}
										
									}
									
								}
								
								mask.add(flag);
								
							}
						}
						resultValues.add(mask);
					}
					
					
				}
			}
		}
		
		setResult(0, resultValues);
	}

}
