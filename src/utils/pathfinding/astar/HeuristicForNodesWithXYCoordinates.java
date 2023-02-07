package utils.pathfinding.astar;

import java.util.function.Function;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.ValueGraph;

public class HeuristicForNodesWithXYCoordinates implements Function<NodeWithXYCoordinates, Double> {
	
	  private final double maxSpeed;
	  private final NodeWithXYCoordinates target;

	  public HeuristicForNodesWithXYCoordinates(
	      ValueGraph<NodeWithXYCoordinates, Double> graph, NodeWithXYCoordinates target) {
		  this.maxSpeed = calculateMaxSpeed(graph);
		  this.target = target;
	  }

	  private static double calculateMaxSpeed(ValueGraph<NodeWithXYCoordinates, Double> graph) {
	    return graph.edges().stream()
	        .map(edge -> calculateSpeed(graph, edge))
	        .max(Double::compare)
	        .orElseThrow(() -> new IllegalArgumentException("Graph is empty"));
	  }

	  private static double calculateSpeed(
	      ValueGraph<NodeWithXYCoordinates, Double> graph, EndpointPair<NodeWithXYCoordinates> edge) {
		  double euclideanDistance = calculateEuclideanDistance(edge.nodeU(), edge.nodeV());
		  double cost = graph.edgeValue(edge).orElseThrow(() -> new IllegalArgumentException("Graph is empty"));
		  double speed = euclideanDistance / cost;
		  return speed;
	  }

	  public static double calculateEuclideanDistance(NodeWithXYCoordinates source, NodeWithXYCoordinates target) {
		  double distanceX = target.getX() - source.getX();
		  double distanceY = target.getY() - source.getY();
		  return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
	  }

	  @Override
	  public Double apply(NodeWithXYCoordinates node) {
		  double euclideanDistance = calculateEuclideanDistance(node, target);
		  double minimumCost = euclideanDistance / maxSpeed;
		  return minimumCost;
	  }
}