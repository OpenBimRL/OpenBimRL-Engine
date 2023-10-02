package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Flattens the hierarchy of an Collection by one level.
 *
 * @author Marcel Stepien
 */
public class FlattenCollection extends AbstractFunction {

    public FlattenCollection(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        Object input0 = getInput(0);
        if (input0 == null) {
            return;
        }

        Collection result = flattenListOfListsImperatively((Collection<Collection>) input0);

        setResult(0, result);
    }


    public Collection flattenListOfListsImperatively(Collection<Collection> nestedList) {
        Collection ls = new ArrayList<>();
        nestedList.forEach(ls::addAll);
        return ls;
    }

}
