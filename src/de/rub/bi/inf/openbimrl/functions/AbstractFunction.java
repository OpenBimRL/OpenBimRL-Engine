package de.rub.bi.inf.openbimrl.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;

/**
 * An abstact super-type of all funktions supported by the OpenBimRL engine.
 * Instances of this class need to be registred in {@link FunctionFactory}. 
 * Furthermore, an implemented function can be adressed in an OpenBimRL file as
 * and elementary node of the precalculation graph.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public abstract class AbstractFunction {

	private final ArrayList<Object> results;
	protected NodeProxy nodeProxy;
	
	public AbstractFunction(NodeProxy nodeProxy) {
		this.nodeProxy = nodeProxy;
		results = new ArrayList<>(nodeProxy.getNode().getOutputs().getOutput().size());
		for(int i = 0; i < nodeProxy.getNode().getOutputs().getOutput().size(); i++) {
			results.add(null); //initializing for set(i, element) method
		}
	}
	
	/**
	 * Executes the function on a given model context.
	 * Incoming inputs are provided by getInput-method.
	 * Caclucated an valid results are then stored by 
	 * setting them via the setResult-method.
	 * 
	 * @param ifcModel
	 */
	public abstract void execute(IIFCModel ifcModel);

	@SuppressWarnings("unchecked")
	protected <T> T getInput(int pos) {
		final var inputEdge = nodeProxy.getInputEdge(pos);
		return inputEdge != null ? (T)inputEdge.getCurrentData() : null;
	}

	protected Collection<?> getInputAsCollection(int pos) {
		final var in = getInput(pos);
		if (in instanceof Collection<?> out)
			return out;

		return List.of(in);
	}
	
	protected void setResult(int pos, Object result) {
		results.set(pos, result);			
	}
	
	public ArrayList<Object> getResults() {
		return results;
	}

}
