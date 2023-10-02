package de.rub.bi.inf.openbimrl.functions.list;

import java.util.Map;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Retrieve the value in a map by key.
 * 
 * @author Marcel Stepien
 *
 */
public class GetMapValueAtKey extends AbstractFunction {

	public GetMapValueAtKey(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
		Map<?, ?> map = getInput(0);
		
		Object key = getInput(1);
		
		Object valueAtKey = map.get(key);
		
		setResult(0, valueAtKey);
	}

}
