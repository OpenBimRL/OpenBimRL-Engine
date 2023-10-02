package de.rub.bi.inf.openbimrl.functions.list;

import com.apstex.gui.core.model.applicationmodel.IIFCModel;
import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Marcel Stepien
 */
public class GetMapValueByKeyList extends AbstractFunction {

    public GetMapValueByKeyList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {
        Map<?, ?> map = getInput(0);

        ArrayList<Object> values = new ArrayList<Object>();
        Object keys = getInput(1);
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
