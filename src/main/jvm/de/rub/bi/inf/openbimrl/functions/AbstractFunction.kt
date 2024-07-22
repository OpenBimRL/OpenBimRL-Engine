package de.rub.bi.inf.openbimrl.functions;

import de.rub.bi.inf.openbimrl.NodeProxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An abstract super-type of all functions supported by the OpenBimRL engine.
 * Instances of this class need to be registered in {@link FunctionFactory}.
 * Furthermore, an implemented function can be addressed in an OpenBimRL file as
 * and elementary node of the pre-calculation graph.
 *
 * @author Marcel Stepien, Andre Vonthron (reworked by Florian Becker)
 */
public abstract class AbstractFunction {

    private final ArrayList<Object> results;
    protected NodeProxy nodeProxy;

    public AbstractFunction(NodeProxy nodeProxy) {
        this.nodeProxy = nodeProxy;
        results = new ArrayList<>(nodeProxy.getNode().getOutputs().getOutput().size());
        for (int i = 0; i < nodeProxy.getNode().getOutputs().getOutput().size(); i++) {
            results.add(null); //initializing for set(i, element) method
        }
    }

    public abstract void execute();

    @SuppressWarnings("unchecked")
    protected <T> T getInput(int pos) {
        final var inputEdge = nodeProxy.getInputEdge(pos);
        return inputEdge != null ? (T) inputEdge.getCurrentData() : null;
    }

    protected Collection<?> getInputAsCollection(int pos) {
        final var in = getInput(pos);
        if (in instanceof Collection<?> out)
            return out;

        if (in == null) return new ArrayList<>();
        return List.of(in);
    }

    protected void setResult(int pos, Object result) {
        results.set(pos, result);
    }

    public ArrayList<Object> getResults() {
        return results;
    }

}
