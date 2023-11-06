package de.rub.bi.inf.openbimrl;

import java.util.*;

/**
 * A container holding all necessary information for traversing and executing the precalculation graph.
 * The context preforms a topological sort on the incoming data graph to enable an execution that is not
 * dependent on the occuence of nodes and edges in the document.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class PrecalculationContext {

    private PrecalculationsType precalculations = null;
    private HashMap<String, NodeProxy> nodeProxies = new HashMap<>(); //node.id, nodeProxy
    private HashMap<String, EdgeProxy> edgeProxies = new HashMap<>(); //edge.id, edgeProxy

    private ArrayList<NodeType> nodes = null;
    private ArrayList<EdgeType> edges = null;

    public PrecalculationContext(PrecalculationsType precalculations) {
        this.precalculations = precalculations;

        nodes = new ArrayList<NodeType>();
        edges = new ArrayList<EdgeType>();

        for (Object entry : precalculations.getNodeOrEdge()) {

            if (entry instanceof NodeType) {
                nodes.add((NodeType) entry);
            }

            if (entry instanceof EdgeType) {
                edges.add((EdgeType) entry);
            }
        }

        //create NodeProxies from Nodes
        for (NodeType node : nodes) {
            NodeProxy nodeProxy = new NodeProxy(node);
            nodeProxies.put(node.getId(), nodeProxy);
        }

        //Create EdgeProxies and associate NodeProxies
        for (EdgeType edge : edges) {
            EdgeProxy edgeProxy = new EdgeProxy(edge);
            edgeProxies.put(edgeProxy.getEdge().getId(), edgeProxy);

            //Set edge targets and node inputs
            if (edge.getTarget() != null && edge.getTargetHandle() != null) {
                NodeProxy nodeProxy = nodeProxies.get(edge.getTarget());
                nodeProxy.setInputEdge(edgeProxy, edge.getTargetHandle());
                edgeProxy.setTargetNode(nodeProxy);
            }

            //Set edge sources and node outputs
            if (edge.getSource() != null && edge.getSourceHandle() != null) {
                NodeProxy nodeProxy = nodeProxies.get(edge.getSource());
                nodeProxy.setOutputEdge(edgeProxy, edge.getSourceHandle());
                edgeProxy.setSourceNode(nodeProxy);
            }
        }

    }

    public NodeProxy getNodeProxy(NodeType node) {
        return nodeProxies.get(node.getId());
    }

    public NodeProxy getNodeProxy(String nodeId) {
        return nodeProxies.get(nodeId);
    }

    public ArrayList<NodeType> getGraphNodes() {
        return nodes;
    }

    public List<NodeType> getGraphSortedNodes() {

        // List where we'll be storing the topological order
        List<NodeType> order = new ArrayList<>();

        // Map which indicates if a node is visited (has been processed by the algorithm)
        Map<String, Boolean> visited = new HashMap<>();
        for (NodeProxy tmp : nodeProxies.values()) {
            visited.put(tmp.getNode().getId(), false);
        }

        // We go through the nodes
        for (NodeProxy tmp : nodeProxies.values()) {
            if (!visited.get(tmp.getNode().getId()))
                topoSort(tmp.getNode().getId(), visited, order);
        }

        // We reverse the order we constructed to get the proper toposorting
        Collections.reverse(order);

        return order;
    }

    private void topoSort(String v, Map<String, Boolean> visited, List<NodeType> order) {
        // Mark the current node as visited
        visited.replace(v, true);

        // We reuse the algorithm on all adjacent nodes to the current node
        for (EdgeProxy edge : nodeProxies.get(v).getOutputEdges()) {
            String neighborId = edge.getEdge().getTarget();

            if (!visited.get(neighborId))
                topoSort(neighborId, visited, order);
        }

        // Put the current node in the array
        order.add(nodeProxies.get(v).getNode());
    }

}
