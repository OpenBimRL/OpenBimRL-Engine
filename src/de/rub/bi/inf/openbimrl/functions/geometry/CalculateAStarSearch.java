package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;
import org.apache.commons.numbers.core.Precision;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.utils.pathfinding.astar.AStarWithTreeSet;
import de.rub.bi.inf.utils.pathfinding.astar.HeuristicForNodesWithXYCoordinates;
import de.rub.bi.inf.utils.pathfinding.astar.NodeWithXYCoordinates;

/**
 * Performs a shortest path search based on distances from an start node to all reachable end nodes in a graph.
 * 
 * @author Marcel Stepien
 *
 */
public class CalculateAStarSearch extends AbstractFunction {
	
	public CalculateAStarSearch(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
		Object input0 = getInput(0);
		HashMap nodeEdges = null;
		if(input0 instanceof HashMap) {
			nodeEdges = (HashMap)input0;
		}else {
			return;
		}
		
		Object input1 = getInput(1);
		if(input1 == null) { return; }
				
		ArrayList startNodes = null;
		if(input1 instanceof ArrayList) {
			startNodes = (ArrayList) input1;
		}else {
			startNodes = new ArrayList();
			startNodes.add(input1);
		}
		
		Object input2 = getInput(2);
		if(input2 == null) { return; }
				
		ArrayList endNodes = null;
		if(input2 instanceof ArrayList) {
			endNodes = (ArrayList) input2;
		}else {
			endNodes = new ArrayList();
			endNodes.add(input2);
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
		
		HashSet<Vector3D> truncatedEndNodes = new HashSet<Vector3D>();
		for(Object endNode : endNodes) {
			if(endNode instanceof Vector3D) {
				double truncatedX = Precision.round(((Vector3D)endNode).getX(), precision);
				double truncatedY = Precision.round(((Vector3D)endNode).getY(), precision);
				double truncatedZ = Precision.round(((Vector3D)endNode).getZ(), precision);
				Vector3D truncatedEndNode = Vector3D.of(truncatedX, truncatedY, truncatedZ);

				results.put(truncatedEndNode, 0.0); //initial distance set
				truncatedEndNodes.add(truncatedEndNode);
			}
		}//Initialization of zero distances
		
		
		
		HashSet<Vector3D> visited = new HashSet();
		HashMap<Vector3D, NodeWithXYCoordinates> nodeMap = new HashMap();
		
		MutableValueGraph<NodeWithXYCoordinates, Double> graph = ValueGraphBuilder.undirected().build();
		
		for(Object node : nodeEdges.keySet()) {
			Object segList = nodeEdges.get(node);
			
			for(Segment3D segment : ((ArrayList<Segment3D>)segList)) {

				Vector3D start = segment.getStartPoint();
				Vector3D end = segment.getEndPoint();
				
				double truncatedStartX = Precision.round(start.getX(), precision);
				double truncatedStartY = Precision.round(start.getY(), precision);
				double truncatedStartZ = Precision.round(start.getZ(), precision);
				Vector3D truncatedStartNode = Vector3D.of(truncatedStartX, truncatedStartY, truncatedStartZ);
				
				double truncatedEndX = Precision.round(end.getX(), precision);
				double truncatedEndY = Precision.round(end.getY(), precision);
				double truncatedEndZ = Precision.round(end.getZ(), precision);
				Vector3D truncatedEndNode = Vector3D.of(truncatedEndX, truncatedEndY, truncatedEndZ);
				
				double distance = truncatedStartNode.distance(truncatedEndNode);
				
				//boolean flag = !visited.contains(truncatedStartNode) || !visited.contains(truncatedEndNode);
				
				NodeWithXYCoordinates startNode = new NodeWithXYCoordinates(UUID.randomUUID().toString(), start.getX(), start.getY());
				startNode.setHeight(start.getZ());
				if(visited.contains(truncatedStartNode)) {
					startNode = nodeMap.get(truncatedStartNode);
				}else {
					visited.add(truncatedStartNode);					
					nodeMap.put(truncatedStartNode, startNode);
				}
				
				NodeWithXYCoordinates endNode = new NodeWithXYCoordinates(UUID.randomUUID().toString(), end.getX(), end.getY());
				endNode.setHeight(end.getZ());
				if(visited.contains(truncatedEndNode)) {
					endNode = nodeMap.get(truncatedEndNode);
				}else {
					visited.add(truncatedEndNode);					
					nodeMap.put(truncatedEndNode, endNode);
				}
				
				graph.putEdgeValue(startNode, endNode, distance);
				
			}
		}
		
		ArrayList<Object> paths = new ArrayList<Object>();
		
		for(Vector3D startVec : truncatedStartNodes) {
			for(Vector3D endVec : truncatedEndNodes) {
				List<NodeWithXYCoordinates> shortestPath = findAndPrintShortestPath(graph, nodeMap.get(startVec), nodeMap.get(endVec));
				if(shortestPath != null) {
					ArrayList<Vector3D> path = new ArrayList();
					for(NodeWithXYCoordinates nodeXY : shortestPath) {
						path.add(Vector3D.of(nodeXY.getX(), nodeXY.getY(), nodeXY.getHeight()));
					}
					paths.add(path);
				}else {
					System.err.println("A path could not be found from " + startVec.toString() + " to destination " + endVec.toString());
				}
			}
		}
		
		setResult(0, paths);
	}
	
	private List<NodeWithXYCoordinates> findAndPrintShortestPath(
	      ValueGraph<NodeWithXYCoordinates, Double> graph,
	      NodeWithXYCoordinates source,
	      NodeWithXYCoordinates target) {
		
	    Function<NodeWithXYCoordinates, Double> heuristic = new HeuristicForNodesWithXYCoordinates(graph, target);
	    
	    List<NodeWithXYCoordinates> shortestPath = AStarWithTreeSet.findShortestPath(graph, source, target, heuristic);
	    return shortestPath;
	}
}
