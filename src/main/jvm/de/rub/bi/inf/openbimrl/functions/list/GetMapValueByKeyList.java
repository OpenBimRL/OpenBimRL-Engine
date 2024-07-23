package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marcel Stepien
 */
public class GetMapValueByKeyList extends AbstractFunction {

    public GetMapValueByKeyList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        Map<?, ?> map = getInput(0, Map.class);

        ArrayList<Object> values = new ArrayList<Object>();
        Object keys = getInput(1, Object.class);
        if (keys instanceof Collection) {
            for (Object k : (Collection) keys) {
                values.add(map.get(k.toString()));
            }
        }

        //System.out.println("A: " + values.toString());
        if (values.size() == 1) {
            setResult(0, values.get(0));
        } else {
            setResult(0, values);
        }
    }

}
