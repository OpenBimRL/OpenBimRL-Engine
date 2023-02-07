package utils.pathfinding.astar;

public class AStarNodeWrapper<N extends Comparable<N>> implements Comparable<AStarNodeWrapper<N>> {
	  private final N node;
	  private AStarNodeWrapper<N> predecessor;
	  private double totalCostFromStart;
	  private final double minimumRemainingCostToTarget;
	  private double costSum;
	
	  public AStarNodeWrapper(
	      N node,
	      AStarNodeWrapper<N> predecessor,
	      double totalCostFromStart,
	      double minimumRemainingCostToTarget) {
	    this.node = node;
	    this.predecessor = predecessor;
	    this.totalCostFromStart = totalCostFromStart;
	    this.minimumRemainingCostToTarget = minimumRemainingCostToTarget;
	    calculateCostSum();
	  }
	
	  private void calculateCostSum() {
	    this.costSum = this.totalCostFromStart + this.minimumRemainingCostToTarget;
	  }
	
	  public N getNode() {
	    return node;
	  }
	
	  public void setPredecessor(AStarNodeWrapper<N> predecessor) {
	    this.predecessor = predecessor;
	  }
	
	  public AStarNodeWrapper<N> getPredecessor() {
	    return predecessor;
	  }
	
	  public void setTotalCostFromStart(double totalCostFromStart) {
	    this.totalCostFromStart = totalCostFromStart;
	    calculateCostSum();
	  }
	
	  public double getTotalCostFromStart() {
	    return totalCostFromStart;
	  }
	
	  @Override
	  public int compareTo(AStarNodeWrapper<N> other) {
	    int compare = Double.compare(this.costSum, other.costSum);
	    if (compare == 0) {
	      compare = node.compareTo(other.node);
	    }
	    return compare;
	  }
	
	  @Override
	  public String toString() {
	    return "AStarNodeWrapperForTreeSet{"
	        + "node="
	        + node
	        + ", predecessor="
	        + (predecessor != null ? predecessor.getNode().toString() : "")
	        + ", totalCostFromStart="
	        + totalCostFromStart
	        + ", minimumRemainingCostToTarget="
	        + minimumRemainingCostToTarget
	        + ", costSum="
	        + costSum
	        + '}';
	  }
}