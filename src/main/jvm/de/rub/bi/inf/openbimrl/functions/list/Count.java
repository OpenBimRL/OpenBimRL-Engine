package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.Collection;

/**
 * Counts the items in a list and returns the number.
 *
 * @author Marcel Stepien
 */
public class Count extends AbstractFunction {

    public Count(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        final Collection<?> list = getInput(0);
        setResult(0, list.size());
    }

}
