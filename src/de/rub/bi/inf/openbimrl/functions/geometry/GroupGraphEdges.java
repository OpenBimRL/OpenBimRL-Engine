package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;
import org.apache.commons.numbers.core.Precision;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

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
	public void execute(IIFCModel ifcModel) {
	
		final var edges = getInputAsCollection(0);
		if (edges.isEmpty()) return;

		final var edgeMap = groupEdges(edges);
		
		setResult(0, edgeMap);
	}
	
	private HashMap<Vector3D, ArrayList<Object>> groupEdges(Collection<?> edges){
		HashMap<Vector3D, ArrayList<Object>> linearEdgeList = new HashMap<Vector3D, ArrayList<Object>>();
		
		for(Object edge : edges) {
			if(edge instanceof Segment3D segment) {

				double truncatedX = Precision.round(segment.getStartPoint().getX(), 6);
				double truncatedY = Precision.round(segment.getStartPoint().getY(), 6);
				double truncatedZ = Precision.round(segment.getStartPoint().getZ(), 6);
				final var truncatedVector = Vector3D.of(truncatedX, truncatedY, truncatedZ);

                linearEdgeList.computeIfAbsent(truncatedVector, k -> new ArrayList<>());
				
				linearEdgeList.get(truncatedVector).add(segment);
				
				
			}
		}
		
		return linearEdgeList;
	}
	
}
