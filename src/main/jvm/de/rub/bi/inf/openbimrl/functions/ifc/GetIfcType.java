package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCClass;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Retrieves the class type of an element as String value.
 *
 * @author Marcel Stepien
 */
public class GetIfcType extends AbstractFunction {

    public GetIfcType(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Collection<Object> objects = new ArrayList();
        Object input0 = getInput(0);
        if (input0 instanceof Collection<?>) {
            objects = (Collection<Object>) input0;
        } else {
            objects.add(input0);
        }

        ArrayList<Object> resultValues = new ArrayList<>();

        for (Object o : objects) {

            if (o instanceof IIFCClass ifcObj) {
                resultValues.add(ifcObj.getClassName());
            }

        }

        if (resultValues.size() == 1)
            setResult(0, resultValues.get(0));
        else
            setResult(0, resultValues);

    }


}
