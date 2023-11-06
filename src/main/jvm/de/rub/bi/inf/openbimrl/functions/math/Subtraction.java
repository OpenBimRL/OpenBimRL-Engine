package de.rub.bi.inf.openbimrl.functions.math;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Performs a subtraction operator given two number values.
 *
 * @author Marcel Stepien
 */
public class Subtraction extends AbstractFunction {

    public Subtraction(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {
        Object object0 = getInput(0);
        Object object1 = getInput(1);

        ArrayList<Double> results = new ArrayList<Double>();
        if (object0 instanceof List &&
                object1 instanceof List &&
                (((Collection<?>) object0).size() == ((Collection<?>) object1).size())) {

            for (int i = 0; i < ((Collection<?>) object0).size(); i++) {
                results.add(
                        Double.parseDouble(((List<?>) object0).get(i).toString()) - Double.parseDouble(((List<?>) object1).get(i).toString())
                );
            }

        } else if (object0 instanceof List && !(object1 instanceof List)) {
            for (int i = 0; i < ((Collection<?>) object0).size(); i++) {
                results.add(
                        Double.parseDouble(((List<?>) object0).get(i).toString()) - Double.parseDouble(object1.toString())
                );
            }
        } else if (object1 instanceof List && !(object0 instanceof List)) {
            for (int i = 0; i < ((Collection<?>) object1).size(); i++) {
                results.add(
                        Double.parseDouble(object0.toString()) - Double.parseDouble(((List<?>) object1).get(i).toString())
                );
            }
        } else {
            Double operand0 = Double.valueOf(object0.toString());
            Double operand1 = Double.valueOf(object1.toString());
            results.add(operand0 - operand1);
        }

        if (results.size() == 1) {
            setResult(0, results.get(0));
        } else {
            setResult(0, results);
        }
    }

}
