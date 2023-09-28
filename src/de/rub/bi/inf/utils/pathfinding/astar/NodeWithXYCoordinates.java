package de.rub.bi.inf.utils.pathfinding.astar;

import java.util.Objects;

public class NodeWithXYCoordinates implements Comparable<NodeWithXYCoordinates> {
	private final String name;
	private final double x;
	private final double y;
	private double height;

	public NodeWithXYCoordinates(String name, double x, double y) {
	    this.name = name;
	    this.x = x;
	    this.y = y;
	}
	
	public String getName() {
	    return name;
	}
	
	public double getX() {
	    return x;
	}
	
	public double getY() {
	    return y;
	}
	
	@Override
	public boolean equals(Object other) {
	    if (this == other) {
	      return true;
	    }
	    if (other == null || getClass() != other.getClass()) {
	      return false;
	    }
	
	    NodeWithXYCoordinates aStarNode = (NodeWithXYCoordinates) other;
	
	    return Objects.equals(name, aStarNode.name);
	}
	
	@Override
    public int hashCode() {
	    return name != null ? name.hashCode() : 0;
	}
	
	@Override
	public int compareTo(NodeWithXYCoordinates other) {
	    return name.compareTo(other.name);
	}
	
	@Override
	public String toString() {
	    return name;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
}
