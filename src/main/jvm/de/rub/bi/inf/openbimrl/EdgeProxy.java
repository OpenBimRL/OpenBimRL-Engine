package de.rub.bi.inf.openbimrl;


/**
 * A proxy element for precalculation edge. Holds and manages information flow.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class EdgeProxy {

    private final EdgeType edge;
    private NodeProxy sourceNode;
    private final int sourceHandle;
    private NodeProxy targetNode;

    public EdgeProxy(EdgeType edge) {
        this.edge = edge;
        sourceHandle = edge.getSourceHandle();
    }

    public EdgeType getEdge() {
        return edge;
    }

    public NodeProxy getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(NodeProxy sourceNode) {
        this.sourceNode = sourceNode;
    }

    public int getSourceHandle() {
        return sourceHandle;
    }

    public NodeProxy getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(NodeProxy targetNode) {
        this.targetNode = targetNode;
    }

    public Object getCurrentData() {
        return getSourceNode().getFunction().getResults().get(sourceHandle);
    }

}
