package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.core.Precision;

import com.apstex.gui.core.model.applicationmodel.IIFCModel;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Retrieves the centroid of an RegionBSPTree3D.
 * 
 * @author Marcel Stepien
 *
 */
public class GetCentroidOfRegionBSPTree extends AbstractFunction {
	
	public GetCentroidOfRegionBSPTree(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		Collection<?> bspTrees = null;
		if(input0 instanceof Collection<?>) {
			bspTrees = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<>();
			newList.add(input0);
			bspTrees = newList;
		}
		
		double precision = 1e-14;
		ArrayList<Vector3D> listOfNodes = new ArrayList<Vector3D>();
		
		for(Object o : bspTrees) {
			
			if(o instanceof RegionBSPTree3D) {
				
				Vector3D centerPoint = ((RegionBSPTree3D)o).getCentroid();
				if(centerPoint == null) {
					centerPoint = ((RegionBSPTree3D)o).toTriangleMesh(
						Precision.doubleEquivalenceOfEpsilon(precision)
					).getBounds().getCentroid();
				}
				
				Vector3D adjPoint = Vector3D.of(
						centerPoint.getX(), 
						centerPoint.getY(), 
						centerPoint.getZ()
				);
				
				listOfNodes.add(
						adjPoint
				);
			}
			
		}
		
		if(listOfNodes.size() == 1) {
			setResult(0, listOfNodes.get(0));
		} else {
			setResult(0, listOfNodes);
		}

	}

}
