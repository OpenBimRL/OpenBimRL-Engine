package de.rub.bi.inf.openbimrl;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.FunctionFactory;

import java.util.*;

/**
 * A proxy element for precalculation nodes. Performes the calculation of a single node function
 * and passes the input and output nodes.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class NodeProxy {

    private final Optional<List<ArrayList<EdgeProxy>>> inputEdges;
    private final Optional<List<ArrayList<EdgeProxy>>> outputEdges;
    private final NodeType node;
    private AbstractFunction function;

    public NodeProxy(NodeType node) {
        this.node = node;
        this.function = FunctionFactory.getFunction(this);

        //initialize lists
        if (node.getInputs() != null && node.getInputs().getInput() != null)
            inputEdges = Optional.of(List.of(new ArrayList<>(node.getInputs().getInput().size())));
        else inputEdges = Optional.empty();
        if (node.getOutputs() != null && node.getOutputs().getOutput() != null)
            outputEdges = Optional.of(List.of(new ArrayList<>(node.getOutputs().getOutput().size())));
        else outputEdges = Optional.empty();
    }

    public void setInputEdge(EdgeProxy edge, int pos) {

        //TODO catch case if pos > array.length

        // omit is present check cause Marcel didn't care either
        ArrayList<EdgeProxy> inputsAtPos = inputEdges.get().get(pos);

        if (inputsAtPos == null) {
            inputsAtPos = new ArrayList<>();
            inputEdges.get().set(pos, inputsAtPos);
        }

        inputsAtPos.add(edge);
    }

    public void setOutputEdge(EdgeProxy edge, int pos) {

        //TODO catch case if pos > array.length

        // omit is present check cause Marcel didn't care either
        ArrayList<EdgeProxy> outputsAtPos = outputEdges.get().get(pos);

        if (outputsAtPos == null) {
            outputsAtPos = new ArrayList<>();
            outputEdges.get().set(pos, outputsAtPos);
        }

        outputsAtPos.add(edge);
    }

    public ArrayList<EdgeProxy> getInputEdges(int pos) {
        if (inputEdges.isEmpty()) return null;
        if (pos >= inputEdges.get().size()) return null; // out of scope

        return inputEdges.get().get(pos);

    }

    public EdgeProxy getInputEdge(int pos) {
        ArrayList<EdgeProxy> edges = getInputEdges(pos);
        if (edges != null && !edges.isEmpty()) {
            return edges.get(0);
        }
        return null;
    }

    public ArrayList<EdgeProxy> getOutputEdges() {
        return outputEdges.map(
                arrayLists -> arrayLists.stream()
                        .filter(Objects::nonNull) // filter null values
                        .reduce(new ArrayList<>(arrayLists.size()), (acc, val) -> {
                            acc.addAll(val);
                            return acc;
                        })
        ).orElseGet(() -> new ArrayList<>(0)); // return empty list
    }

    public ArrayList<EdgeProxy> getInputEdges() {
        return inputEdges.map(
                arrayLists -> arrayLists.stream()
                        .filter(Objects::nonNull) // filter null values
                        .reduce(new ArrayList<>(arrayLists.size()), (acc, val) -> {
                            acc.addAll(val);
                            return acc;
                        })
        ).orElseGet(() -> new ArrayList<>(0)); // return empty list
    }

    public ArrayList<EdgeProxy> getOutputEdges(int pos) {
        if (pos < outputEdges.get().size())
            return outputEdges.get().get(pos);
        return null;
    }

    public EdgeProxy getOutputEdge(int pos) {
        ArrayList<EdgeProxy> edges = getOutputEdges(pos);
        if (edges != null && !edges.isEmpty()) {
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
        return outputEdges.get().size();
    }

    public void execute(IIFCModel ifcModel, NodeProxy nodeProxy) {

        try {
            function.execute(ifcModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
