package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import de.rub.bi.inf.openbimrl.helper.IfcUtilities;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Retrieves the property value of a specific entity as String value.
 *
 * @author Marcel Stepien
 */
public class GetPropertyAsString extends AbstractFunction {

    public GetPropertyAsString(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

   /*     Collection<Object> objects = new ArrayList();
        Object input0 = getInput(0);
        if (input0 instanceof Collection<?>) {
            objects = (Collection<Object>) input0;
        } else {
            objects.add(input0);
        }

        String setName = getInput(1);
        String propertyName = getInput(2);

        ArrayList<Object> resultValues = new ArrayList<>();

        for (Object o : objects) {
            IfcObjectDefinition.Ifc4 ifcObjectDefinition = (IfcObjectDefinition.Ifc4) o;
            IfcSimpleProperty.Ifc4 simpleProperty = IfcUtilities.Ifc4.getElementProperty(
                    ifcObjectDefinition, setName, propertyName);

            if (simpleProperty instanceof IfcPropertySingleValue.Ifc4) {
                IfcPropertySingleValue.Ifc4 singleValue =
                        (IfcPropertySingleValue.Ifc4) simpleProperty;
                resultValues.add(singleValue.getNominalValue().toString());
            } else if (simpleProperty instanceof IfcPropertyEnumeratedValue.Ifc4) {
                IfcPropertyEnumeratedValue.Ifc4 enumeratedValue =
                        (IfcPropertyEnumeratedValue.Ifc4) simpleProperty;
                resultValues.add(enumeratedValue.getEnumerationValues().get(0).toString());
            } else {

                //FIXME other possibilities
                resultValues.add(null);

            }

        }

        if (resultValues.size() == 1) {
            setResult(0, resultValues.get(0));
        } else {
            setResult(0, resultValues);
        }*/

    }

}
