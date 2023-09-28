package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;
import org.apache.commons.geometry.euclidean.threed.line.LinecastPoint3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Checks if a ray passing through the scene has an intersection with a geometric object.
 * 
 * @author Marcel Stepien
 *
 */
public class CheckLinecasts extends AbstractFunction {
	
	public CheckLinecasts(NodeProxy nodeProxy) {
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
		
		ArrayList<Boolean> filter = new ArrayList<Boolean>();
		for(Object ele1 : elements1) {
			if(ele1 instanceof Segment3D) {
				
				filter.add(
					handleRegionBSPTrees((Segment3D)ele1, elements0)
				);
				
			}
		}
		
		
		setResult(0, filter);
		
	}
	
	private boolean handleRegionBSPTrees(Segment3D segment, Collection<?> elements) {
		boolean flag = false;
		for(Object ele : elements) {
			
			if(ele instanceof Collection<?>) {
				flag = flag || handleRegionBSPTrees(segment, (Collection)ele);
			}
			
			if(ele instanceof RegionBSPTree3D) {
				flag = flag || check(segment, ((RegionBSPTree3D)ele));
			}
		}
		return flag;
	}
	
	private boolean check(Segment3D segment, RegionBSPTree3D region) {
		RegionBSPTree3D regionM1 = region.copy();
		Segment3D line = segment;
		List<LinecastPoint3D> casts = regionM1.linecast(line);
		return !casts.isEmpty();
	}
}
