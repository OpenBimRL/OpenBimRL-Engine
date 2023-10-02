package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Filters a IFC model and retrieves all elements of a certain type.
 *
 * @author Marcel Stepien
 */
public class FilterByElement extends AbstractFunction {

    public FilterByElement(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        String className = getInput(0);
        Class<?> theClass;
        try {
            // TODO fix
            theClass = Class.forName("com.apstex.ifctoolbox.ifc." + className);
            Collection<?> objects = ifcModel.getCollection(theClass);

            setResult(0, new ArrayList(objects));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
