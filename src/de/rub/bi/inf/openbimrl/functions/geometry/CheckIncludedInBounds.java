package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Checks if points of a geometry are included in the the bounds of a geometic objects.
 * 
 * @author Marcel Stepien
 *
 */
public class CheckIncludedInBounds extends AbstractFunction {

	public CheckIncludedInBounds(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		
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
		
		ArrayList<List<?>> resultValues = new ArrayList<List<?>>();
		for(Object og1 : geometryGroupA) {
			if(og1 instanceof ArrayList<?>) {
				for(Object o1 : (ArrayList<?>)og1) {
					if(o1 instanceof GeometryArray) {
												
						GeometryInfo geoA = new GeometryInfo((GeometryArray)o1);
						geoA.recomputeIndices();
						
						Bounds geoBoundsA = new Shape3D(geoA.getIndexedGeometryArray()).getBounds();
						
						Point3d centerA = new Point3d();
						geoBoundsA.getCenter(centerA);
						
						double[] translateArrA = { 0.0, 0.0, 0.0 };
						centerA.get(translateArrA);
						
						Transform3D translateA = new Transform3D();
						translateA.setTranslation(new Vector3d(translateArrA));
						translateA.invert();
						
						Transform3D scaleA = new Transform3D();
						scaleA.setScale(1.0);
						
						Transform3D translateBackA = new Transform3D();
						translateBackA.setTranslation(new Vector3d(translateArrA));
						
						geoBoundsA.transform(translateA);
						geoBoundsA.transform(scaleA);
						geoBoundsA.transform(translateBackA);
						
						ArrayList<Boolean> mask = new ArrayList<Boolean>();
						
						for(Object og2 : geometryGroupB) {
							if(og2 instanceof ArrayList<?>) {
								
								boolean inclusionCheck = true;
								
								for(Object o2 : (ArrayList<?>)og2) {									
									if(o2 instanceof GeometryArray) {
										GeometryInfo geoB = new GeometryInfo((GeometryArray)o2);
										geoB.recomputeIndices();
										
										for(Point3f coodinate : geoB.getCoordinates()) {
											if(!geoBoundsA.intersect(new Point3d(coodinate))) {											
												inclusionCheck = false;
											}
										}
										
									}
									
								}
								
								mask.add(inclusionCheck);
								
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
