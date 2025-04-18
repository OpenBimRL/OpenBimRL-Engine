package de.rub.bi.inf.openbimrl.functions.list;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Marcel Stepien
 */
@OpenBIMRLFunction
public class AsList extends AbstractFunction {

    public AsList(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        String delimiterDim1 = ",";
        String delimiterDim2 = ";";

        ArrayList<ArrayList<Object>> dim2Results = new ArrayList<>();
        for (String dim1Str : Objects.requireNonNull(getInput(0, Object.class)).toString().split(delimiterDim2)) {
            ArrayList<Object> dim1Results = new ArrayList<>();
            if (dim1Str != null) {
                dim1Results.addAll(
                        Arrays.asList(dim1Str.split(delimiterDim1))
                );
            }
            dim2Results.add(dim1Results);
        }

        if (dim2Results.size() == 1) {
            setResult(0, dim2Results.get(0));
        } else {
            setResult(0, dim2Results);
        }
    }

}
