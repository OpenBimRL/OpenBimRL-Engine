package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;
import org.apache.commons.numbers.core.Precision;

/**
 * Performs a deepsearch based on distances from an start node to all reachable points in a graph.
 * 
 * @author Marcel Stepien
 *
 */
public class CalculateDistancesByOrderedGraphEdges extends AbstractFunction {
	
	public CalculateDistancesByOrderedGraphEdges(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		HashMap nodeEdges = null;
		if(input0 instanceof HashMap) {
			nodeEdges = (HashMap)input0;
		}else {
			return;
		}
		
		Object input1 = getInput(1);
		
		if(input1 == null)
			return;
				
		ArrayList startNodes = null;
		if(input1 instanceof ArrayList) {
			startNodes = (ArrayList) input1;
		}else {
			startNodes = new ArrayList();
			startNodes.add(input1);
		}
		
		int precision = 6;
		
		HashMap<Vector3D, Double> results = new HashMap<Vector3D, Double>();
		HashSet<Vector3D> truncatedStartNodes = new HashSet<Vector3D>();
		
		for(Object startNode : startNodes) {
			if(startNode instanceof Vector3D) {
				double truncatedX = Precision.round(((Vector3D)startNode).getX(), precision);
				double truncatedY = Precision.round(((Vector3D)startNode).getY(), precision);
				double truncatedZ = Precision.round(((Vector3D)startNode).getZ(), precision);
				Vector3D truncatedStartNode = Vector3D.of(truncatedX, truncatedY, truncatedZ);

				results.put(truncatedStartNode, 0.0); //initial distance set
				truncatedStartNodes.add(truncatedStartNode);
			}
		}//Initialization of zero distances
		
		deepSearch(results, truncatedStartNodes, nodeEdges, new HashSet<Vector3D>(), precision);
		
		setResult(0, results);
	}
	
	
	private void deepSearch(
			HashMap<Vector3D, Double> results, 
			Collection<Vector3D> startNodes, 
			HashMap nodeEdges, 
			HashSet<Vector3D> visited,
			int precision) {
		
		HashSet<Vector3D> nextStartNodes = new HashSet<Vector3D>();
		
		for(Object startNode : startNodes) {
			if(startNode instanceof Vector3D) {
				
				Object segList = nodeEdges.get(startNode);
				if(segList instanceof ArrayList && !visited.contains((Vector3D)startNode)) {
					
					visited.add((Vector3D)startNode);
					
					double distToStart = results.get(startNode);
					
					for(Object seg : (ArrayList)segList) {
						if(seg instanceof Segment3D) {
							Segment3D seg3D = (Segment3D)seg;

							Vector3D start = seg3D.getStartPoint();
							Vector3D end = seg3D.getEndPoint();
							
							double truncatedStartX = Precision.round(start.getX(), precision);
							double truncatedStartY = Precision.round(start.getY(), precision);
							double truncatedStartZ = Precision.round(start.getZ(), precision);
							Vector3D truncatedStartNode = Vector3D.of(truncatedStartX, truncatedStartY, truncatedStartZ);
							
							double truncatedEndX = Precision.round(end.getX(), precision);
							double truncatedEndY = Precision.round(end.getY(), precision);
							double truncatedEndZ = Precision.round(end.getZ(), precision);
							Vector3D truncatedEndNode = Vector3D.of(truncatedEndX, truncatedEndY, truncatedEndZ);
							
							if(!startNodes.contains(truncatedEndNode) && !visited.contains(truncatedEndNode)) {

								double distance = truncatedStartNode.distance(truncatedEndNode);
								
								distance = distance * (1.0/1.234); //FIXME: Model constant for scaling
								
								results.put(truncatedEndNode, (distance + distToStart));

								//Appearance edgeAppearance = createAppearance(Color.BLUE, 3.0f);
								//showGraphEdge(seg3D, group, edgeAppearance);
								
								nextStartNodes.add(truncatedEndNode);
							}else {

								//Appearance edgeAppearance = createAppearance(Color.RED, 1.0f);
								//showGraphEdge(seg3D, group, edgeAppearance);
							}
							
							
						}
					}
					
				}
				
			}
		}
		
		if(!nextStartNodes.isEmpty()) {			
			deepSearch(results, nextStartNodes, nodeEdges, visited, precision);
		}
		
	}
}
