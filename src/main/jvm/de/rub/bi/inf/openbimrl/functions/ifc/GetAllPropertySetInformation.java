package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Retrieves the property value of a specific entity.
 *
 * @author Marcel Stepien
 */
public class GetAllPropertySetInformation extends AbstractFunction {

    public GetAllPropertySetInformation(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Collection<Object> objects = new ArrayList<>();
        Object input0 = getInputAsCollection(0);

        final var resultValues = new LinkedHashMap<Object, List<String>>();
        //System.out.println("GetProperty: " + objects + " | " + setName + " | " + propertyName);

/*        for (Object o : objects) {
            IfcObjectDefinition.Ifc4 ifcObjectDefinition = (IfcObjectDefinition.Ifc4) o;

            //IfcSimpleProperty.Ifc4 simpleProperty = IfcUtilities.Ifc4.getElementProperty(ifcObjectDefinition, setName, propertyName);

            ArrayList<String> propInfos = new ArrayList<String>();


            SET<? extends IfcRelDefines> relDefines = null;
            if (ifcObjectDefinition instanceof IfcObject.Ifc4) {
                relDefines = ((IfcObject.Ifc4) ifcObjectDefinition).getIsDefinedBy_Inverse();
            } else if (ifcObjectDefinition instanceof IfcContext.Ifc4) {
                relDefines = ((IfcContext.Ifc4) ifcObjectDefinition).getIsDefinedBy_Inverse();
            }

            if (relDefines != null) {
                for (IfcRelDefines isDefinedBy : relDefines) {
                    if (isDefinedBy instanceof IfcRelDefinesByProperties.Ifc4) {
                        IfcRelDefinesByProperties.Ifc4 isDefinedBy4 = (IfcRelDefinesByProperties.Ifc4) isDefinedBy;

                        if (isDefinedBy4.getRelatingPropertyDefinition() instanceof IfcPropertySet.Ifc4) {
                            IfcPropertySet.Ifc4 propertySet4 = (IfcPropertySet.Ifc4) isDefinedBy4.getRelatingPropertyDefinition();

                            for (IfcProperty property : propertySet4.getHasProperties()) {
                                IfcSimpleProperty.Ifc4 simpleProperty = (IfcSimpleProperty.Ifc4) property;

                                String temp = propertySet4.getName().getDecodedValue() + ":" +
                                        simpleProperty.getName().getDecodedValue();

                                if (simpleProperty instanceof IfcPropertySingleValue) {
                                    temp += ":" + ((IfcPropertySingleValue) simpleProperty).getNominalValue().toString();
                                } else if (simpleProperty instanceof IfcPropertyEnumeratedValue) {
                                    temp += ":" + ((IfcPropertyEnumeratedValue) simpleProperty).getEnumerationValues().toString();
                                }

                                propInfos.add(temp);
                            }

                        } // endif propertyset

                    }
                } // foreach isDefined

            }

            resultValues.put(ifcObjectDefinition, propInfos);
        }*/

        setResult(0, resultValues);

    }

}
