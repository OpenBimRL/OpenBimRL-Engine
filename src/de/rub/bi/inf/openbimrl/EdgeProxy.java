package de.rub.bi.inf.openbimrl;


/**
 * A proxy element for precalculation edge. Holds and manages information flow.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public class EdgeProxy {

	private EdgeType edge;
	private NodeProxy sourceNode;
	private int sourceHandle;
	private NodeProxy targetNode;

	public EdgeProxy(EdgeType edge) {
		this.edge = edge;
		sourceHandle = edge.getSourceHandle();
	}
	
	public EdgeType getEdge() {
		return edge;
	}
	
	public void setSourceNode(NodeProxy sourceNode) {
		this.sourceNode = sourceNode;
	}
	
	public NodeProxy getSourceNode() {
		return sourceNode;
	}
	
	public int getSourceHandle() {
		return sourceHandle;
	}
	
	public void setTargetNode(NodeProxy targetNode) {
		this.targetNode = targetNode;
	}
	
	public NodeProxy getTargetNode() {
		return targetNode;
	}
	
	public Object getCurrentData() {			
		return getSourceNode().getFunction().getResults().get(sourceHandle);
	}
	
}
