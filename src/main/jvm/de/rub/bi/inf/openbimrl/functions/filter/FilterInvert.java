package de.rub.bi.inf.openbimrl.functions.filter;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Inverts the result of a filter. Basically, performing a boolean flip on all values of a list.
 *
 * @author Marcel Stepien
 */
public class FilterInvert extends AbstractFunction {

    public FilterInvert(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        Object input0 = getInput(0);

        LinkedHashMap<Object, ArrayList<Boolean>> filterMap = new LinkedHashMap<Object, ArrayList<Boolean>>();

        if (input0 instanceof ArrayList) {
            ArrayList<Boolean> mask = (ArrayList<Boolean>) input0;
            filterMap.put("0", (ArrayList<Boolean>) mask);
        }

        if (input0 instanceof LinkedHashMap) {
            filterMap = (LinkedHashMap<Object, ArrayList<Boolean>>) input0;
        }

        if (filterMap == null)
            return;

        LinkedHashMap<Object, ArrayList<?>> resultValues = new LinkedHashMap<Object, ArrayList<?>>();

        for (Object maskKey : filterMap.keySet()) {
            ArrayList<?> mask = filterMap.get(maskKey);

            ArrayList filteredList = new ArrayList();
            for (int i = 0; i < mask.size(); i++) {
                boolean flag = mask.get(i) != null ? Boolean.parseBoolean(mask.get(i).toString()) : false;
                filteredList.add(!flag);
            }

            resultValues.put(maskKey, filteredList);
        }

        if (resultValues.keySet().size() == 1) {
            Object firstKey = resultValues.keySet().toArray()[0];
            setResult(0, resultValues.get(firstKey));
        } else {
            setResult(0, resultValues);
        }
    }

}
