package openbimrl.functions.list;

import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Counts the items in a list and returns the number.
 * 
 * @author Marcel Stepien
 *
 */
public class Count extends AbstractFunction {

	public Count(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		Collection<?> list = (Collection<?>) getInput(0);
		setResult(0, list.size());
	}

}
