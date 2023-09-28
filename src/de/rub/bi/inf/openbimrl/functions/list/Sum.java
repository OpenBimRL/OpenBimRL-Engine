package de.rub.bi.inf.openbimrl.functions.list;

import java.util.ArrayList;
import java.util.Collection;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Calculates the sum of all numbers contained in a given list.
 *
 * @author Marcel Stepien
 */
public class Sum extends AbstractFunction {

    public Sum(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        ArrayList<Object> objects = new ArrayList();
        Object input0 = getInput(0);
        if (input0 instanceof Collection<?>) {
            objects = (ArrayList<Object>) input0;
        } else {
            objects.add(input0);
        }

        boolean listofLists = true;
        boolean listofSingleValues = true;
        for (int i = 0; i < objects.size(); i++) {
            if ((objects.get(i) instanceof Collection<?>) == false) {
                listofLists = false;
            }
            if ((objects.get(i) instanceof Collection<?>) == true) {
                listofSingleValues = false;
            }
        }

        if (listofSingleValues) {
            double sum = 0;

            for (Object object : objects) {
                Double value = (Double) object;
                sum += value;
            }

            setResult(0, sum);
        } else if (listofLists) {

            ArrayList<Double> sums = new ArrayList<>();
            for (Object object : objects) {
                ArrayList<?> internalList = (ArrayList<?>) object;

                double sum = 0;

                for (Object internalObject : internalList) {
                    Double value = (Double) internalObject;
                    sum += value;
                }

                sums.add(sum);
            }

            setResult(0, sums);

        } else {

            //FIXME throw unsupported mix of singles and lists

            // Annotation: LOL

        }

    }

}
