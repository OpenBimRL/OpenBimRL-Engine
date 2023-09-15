package openbimrl.functions;

import java.util.ArrayList;

import engine.openbimrl.inf.bi.rub.de.ifc.IIFCModel;
import openbimrl.EdgeProxy;
import openbimrl.NodeProxy;

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
	
	private ArrayList<Object> results;
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
		EdgeProxy inputEdge = nodeProxy.getInputEdge(pos);
		return inputEdge != null ? (T)inputEdge.getCurrentData() : null;
	}
	
	protected void setResult(int pos, Object result) {
		results.set(pos, result);			
	}
	
	public ArrayList<Object> getResults() {
		return results;
	}

}
