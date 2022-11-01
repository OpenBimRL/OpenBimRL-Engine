package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;
import org.apache.commons.numbers.core.Precision;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Groups edges thogether with its nodes, providing a map of assigned node to edge list pairs.
 * 
 * @author Marcel Stepien
 *
 */
public class GroupGraphEdges extends AbstractFunction {
	
	public GroupGraphEdges(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		Collection<?> edges = null;
		if(input0 instanceof Collection<?>) {
			edges = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<>();
			newList.add(input0);
			edges = newList;
		}

		HashMap<Vector3D, ArrayList<Object>> edgeMap = groupEdges(edges);
		
		setResult(0, edgeMap);
	}
	
	private HashMap<Vector3D, ArrayList<Object>> groupEdges(Collection<?> edges){
		HashMap<Vector3D, ArrayList<Object>> linearEdgeList = new HashMap<Vector3D, ArrayList<Object>>();
		
		for(Object edge : edges) {
			if(edge instanceof Segment3D) {
				
				Segment3D segement = (Segment3D)edge;
				
				double truncatedX = Precision.round(segement.getStartPoint().getX(), 6);
				double truncatedY = Precision.round(segement.getStartPoint().getY(), 6);
				double truncatedZ = Precision.round(segement.getStartPoint().getZ(), 6);
				Vector3D truncatedVector = Vector3D.of(truncatedX, truncatedY, truncatedZ);
				
				if(linearEdgeList.get(truncatedVector) == null) {
					linearEdgeList.put(truncatedVector, new ArrayList<Object>());
				}
				
				linearEdgeList.get(truncatedVector).add(segement);
				
				
			}
		}
		
		return linearEdgeList;
	}
	
}
