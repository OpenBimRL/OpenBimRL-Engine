package de.rub.bi.inf.openbimrl.functions.math;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class Maximum extends AbstractFunction {

    public Maximum(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        Object object0 = getInput(0, Object.class);

        ArrayList<Object> results = calculateMaximun(object0);

        if (results.size() == 1) {
            setResult(0, results.get(0));
        } else {
            setResult(0, results);
        }

    }

    private ArrayList<Object> calculateMaximun(Object values) {

        ArrayList<Object> results = new ArrayList<Object>();
        if (values instanceof List) {
            Double result = Double.NaN;
            int size = ((List) values).size();
            for (int i = 0; i < size; i++) {
                Object value = ((List) values).get(i);

                if (value instanceof Collection) {
                    ArrayList<Object> objs = calculateMaximun(value);
                    if (objs.size() == 1) {
                        results.add(objs.get(0));
                    } else {
                        results.add(objs);
                    }
                } else {
                    Double temp = Double.valueOf(value.toString());
                    if (result.isNaN()) {
                        result = temp;
                    } else {
                        if (result.compareTo(temp) < 0) {
                            result = temp;
                        }
                    }
                }

            }

            if (!result.isNaN()) {
                results.add(result);
            }
        }

        return results;
    }


}
