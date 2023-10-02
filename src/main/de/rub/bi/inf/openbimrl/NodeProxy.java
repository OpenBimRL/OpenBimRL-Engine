package de.rub.bi.inf.openbimrl;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.FunctionFactory;

import java.util.ArrayList;

/**
 * A proxy element for precalculation nodes. Performes the calculation of a single node function
 * and passes the input and output nodes.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class NodeProxy {

    private NodeType node;
    private ArrayList<EdgeProxy>[] inputEdges;
    private ArrayList<EdgeProxy>[] outputEdges;

    private AbstractFunction function;

    public NodeProxy(NodeType node) {
        this.node = node;
        this.function = FunctionFactory.getFuction(this);

        //initialize lists
        if (node.getInputs() != null && node.getInputs().getInput() != null)
            inputEdges = new ArrayList[node.getInputs().getInput().size()];
        if (node.getOutputs() != null && node.getOutputs().getOutput() != null)
            outputEdges = new ArrayList[node.getOutputs().getOutput().size()];
    }

    public void setInputEdge(EdgeProxy edge, int pos) {

        //TODO catch case if pos > array.length

        ArrayList<EdgeProxy> inputsAtPos = inputEdges[pos];

        if (inputsAtPos == null) {
            inputsAtPos = new ArrayList<>();
            inputEdges[pos] = inputsAtPos;
        }

        inputsAtPos.add(edge);
    }

    public void setOutputEdge(EdgeProxy edge, int pos) {

        //TODO catch case if pos > array.length

        ArrayList<EdgeProxy> outputsAtPos = outputEdges[pos];

        if (outputsAtPos == null) {
            outputsAtPos = new ArrayList<>();
            outputEdges[pos] = outputsAtPos;
        }

        outputsAtPos.add(edge);
    }

    public ArrayList<EdgeProxy> getInputEdges(int pos) {
        if (pos < inputEdges.length)
            return inputEdges[pos];
        return null;
    }

    public EdgeProxy getInputEdge(int pos) {
        ArrayList<EdgeProxy> edges = getInputEdges(pos);
        if (edges != null && edges.size() > 0) {
            return edges.get(0);
        }
        return null;
    }

    public ArrayList<EdgeProxy> getOutputEdges() {
        ArrayList<EdgeProxy> edges = new ArrayList<EdgeProxy>();
        for (ArrayList<EdgeProxy> oE : outputEdges) {
            if (oE != null) {
                edges.addAll(oE);
            }
        }
        return edges;
    }

    public ArrayList<EdgeProxy> getInputEdges() {
        ArrayList<EdgeProxy> edges = new ArrayList<EdgeProxy>();
        for (ArrayList<EdgeProxy> oE : inputEdges) {
            if (oE != null) {
                edges.addAll(oE);
            }
        }
        return edges;
    }

    public ArrayList<EdgeProxy> getOutputEdges(int pos) {
        if (pos < outputEdges.length)
            return outputEdges[pos];
        return null;
    }

    public EdgeProxy getOutputEdge(int pos) {
        ArrayList<EdgeProxy> edges = getOutputEdges(pos);
        if (edges != null && edges.size() > 0) {
            return edges.get(0);
        }
        return null;
    }


    public NodeType getNode() {
        return node;
    }

    public AbstractFunction getFunction() {
        return function;
    }

    public void setFunction(AbstractFunction function) {
        this.function = function;
    }

    public int getNumberOfOutputs() {
        return outputEdges.length;
    }

    public void execute(IIFCModel ifcModel, NodeProxy nodeProxy) {

        try {
            function.execute(ifcModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
