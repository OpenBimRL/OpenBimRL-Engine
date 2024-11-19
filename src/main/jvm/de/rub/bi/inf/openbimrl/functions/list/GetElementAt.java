package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Retrieves an item of a specific index contained in a list of elements.
 *
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class GetElementAt extends AbstractFunction {

    public GetElementAt(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        final var list = new ArrayList<>(getInputAsCollection(0));

        final var positions = new ArrayList<>(getInputAsCollection(1));


        ArrayList<Object> elementsAt = new ArrayList<Object>();
        for (int index = 0; index < positions.size(); index++) {

            String strPosition = positions.get(index).toString();
            int position = Integer.parseInt(strPosition);

            if (positions.size() == 1) {
                Object value = list.get(position);
                elementsAt.add(value);
            } else {
                Object values = list.get(index);
                if (values instanceof List) {
                    elementsAt.add(((List<?>) values).get(position));
                }
            }

        }

        if (elementsAt.size() == 1) {
            setResult(0, elementsAt.get(0));
        } else {
            setResult(0, elementsAt);
        }
    }

}
