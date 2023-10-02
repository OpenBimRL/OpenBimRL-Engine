package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCLabel;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Filters a list of IFC entities by their property definition. Returns all entities that contain the property information.
 *
 * @author Marcel Stepien
 */
public class FilterByProperty extends AbstractFunction {

    public FilterByProperty(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        Collection<Object> objects = new ArrayList();
        Object input0 = getInput(0);
        if (input0 instanceof Collection<?>) {
            objects = (Collection<Object>) input0;
        } else {
            objects.add(input0);
        }

        String setName = getInput(1);
        String propertyName = getInput(2);
        String stringValue = getInput(3);

        ArrayList<Object> values = null;
        if (stringValue == null) {
            values = null;
        } else {
            values = new ArrayList<Object>();
            for (String value : stringValue.split(";")) {
                if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                    values.add(Boolean.valueOf(value));
                } else {
                    values.add(value);
                }
            }
        }

        ArrayList<Object> resultValues = new ArrayList<>();

        for (Object obj : objects) {

            IfcObject.Ifc4 obj4 = (IfcObject.Ifc4) obj;

            if (obj4.getIsDefinedBy_Inverse() == null) {
                System.out.println("Has no Properties: " + obj4.getStepLine());
                continue;
            }

            for (IfcRelDefines isDefinedBy : obj4.getIsDefinedBy_Inverse()) {
                if (isDefinedBy instanceof IfcRelDefinesByProperties.Ifc4) {
                    IfcRelDefinesByProperties.Ifc4 isDefinedBy4 = (IfcRelDefinesByProperties.Ifc4) isDefinedBy;

                    if (isDefinedBy4.getRelatingPropertyDefinition() instanceof IfcPropertySet.Ifc4) {
                        IfcPropertySet.Ifc4 propertySet4 = (IfcPropertySet.Ifc4) isDefinedBy4
                                .getRelatingPropertyDefinition();
                        if (propertySet4.getName().getDecodedValue().equals(setName)) {
                            for (IfcProperty property : propertySet4.getHasProperties()) {
                                if (property instanceof IfcSimpleProperty) {
                                    IfcSimpleProperty.Ifc4 simpleProperty = (IfcSimpleProperty.Ifc4) property;
                                    if (simpleProperty.getName().getDecodedValue().equals(propertyName)) {

                                        if (values == null)
                                            resultValues.add(obj4);
                                        else if (simpleProperty instanceof IfcPropertySingleValue.Ifc4) {
                                            IfcPropertySingleValue.Ifc4 propertySingleValue =
                                                    (IfcPropertySingleValue.Ifc4) simpleProperty;
                                            IfcValue.Ifc4 ifcValue = propertySingleValue.getNominalValue();

                                            for (Object value : values) {
                                                if (ifcValue instanceof IfcLabel.Ifc4) {
                                                    IfcLabel.Ifc4 label = (IfcLabel.Ifc4) ifcValue;
                                                    if (label.getDecodedValue().equals(value)) {
                                                        resultValues.add(obj4);
                                                        break;
                                                    }
                                                } else if (ifcValue instanceof IfcBoolean.Ifc4) {
                                                    IfcBoolean.Ifc4 ifcBoolean = (IfcBoolean.Ifc4) ifcValue;
                                                    if (value.equals(ifcBoolean.getValue())) {
                                                        resultValues.add(obj4);
                                                        break;
                                                    }
                                                } else if (ifcValue instanceof IfcIdentifier.Ifc4) {
                                                    IfcIdentifier.Ifc4 identifier = (IfcIdentifier.Ifc4) ifcValue;
                                                    if (value.equals(identifier.getValue())) {
                                                        resultValues.add(obj4);
                                                        break;
                                                    }
                                                } else if (ifcValue instanceof IfcText.Ifc4) {
                                                    IfcText.Ifc4 ifcText = (IfcText.Ifc4) ifcValue;
                                                    if (value.equals(ifcText.getValue())) {
                                                        resultValues.add(obj4);
                                                        break;
                                                    }
                                                } else {
                                                    //FIXME Support other comparisons than labels and booleans. Maybe use java reflexion?
                                                    System.err.println("FilterByProperty has tied to parse a Type its not familiar with: " + ifcValue.getClassName());
                                                }
                                            }


                                        } else if (simpleProperty instanceof IfcPropertyEnumeratedValue.Ifc4) {
                                            IfcPropertyEnumeratedValue.Ifc4 enumeratedValue =
                                                    (IfcPropertyEnumeratedValue.Ifc4) simpleProperty;
                                            IfcValue ifcValue = enumeratedValue.getEnumerationValues().get(0);
                                            if (ifcValue instanceof IfcLabel.Ifc4) {
                                                IIFCLabel.Ifc4 label = (IfcLabel.Ifc4) ifcValue;

                                                for (Object value : values) {
                                                    if (label.getDecodedValue().equals(value)) {
                                                        resultValues.add(obj4);
                                                        break;
                                                    }
                                                }

                                            } else {
                                                //FIXME Support other comparisons than labels
                                                System.err.println("FilterByProperty has tied to parse a Type its not familiar with: " + ifcValue.getClassName());
                                            }
                                        } else {
                                            //FIXME Support other property Types than SingleValues and Enumerations
                                            System.err.println("FilterByProperty has tied to parse a Type its not familiar with: " + simpleProperty.getClassName());
                                        }
                                    }

                                } else {
                                    //FIXME Other property types than IfcSimplePropertyValue
                                    System.err.println("FilterByProperty has tied to parse a Type its not familiar with: " + property.getClassName());
                                }
                            }

                        }
                    } // endif property set

                }
            } // foreach isDefined

        } // for obj

        if (resultValues.size() == 1)
            setResult(0, resultValues.get(0));
        else
            setResult(0, resultValues);
    }

}
