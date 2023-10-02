package de.rub.bi.inf.openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Filters a list of IFC entities by their quantity value. Returns all entities that contain the quantity information.
 * 
 * @author Marcel Stepien
 *
 */
public class FilterByQuantity extends AbstractFunction{

	public FilterByQuantity(NodeProxy nodeProxy) {
		super(nodeProxy);
	}
	
	@Override
	public void execute(IIFCModel ifcModel) {
		
		Collection<Object> objects = new ArrayList();
		Object input0 = getInput(0);
		if(input0 instanceof Collection<?>) {
			objects = (Collection<Object>)input0;
		}else {
			objects.add(input0);
		}
		
		String setName = getInput(1);
		String propertyName = getInput(2);
		String stringValue = getInput(3);
		//System.out.println("Test: " + stringValue);
		
		ArrayList<Object> values = null;
		if(stringValue == null) {
			values = null;
		}else {
			values = new ArrayList<Object>();
			for(String value : stringValue.split(";")) {
				if(value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
					values.add(Boolean.valueOf(value));
				}else {
					values.add(value);
				}
			}
		}
			
		ArrayList<Object> resultValues = new ArrayList<>();
		
		//System.out.println("FilterByProperty: " + objects + " | " + setName + " | " + propertyName + " | " + stringValue);

		System.out.println(objects.size());
		for (Object obj : objects) {

			IfcObject.Ifc4 obj4 = (IfcObject.Ifc4) obj;
			
			if(obj4.getIsDefinedBy_Inverse() == null) {
				System.out.println("Has no Quantities: " + obj4.getStepLine());
				continue;
			}
			
			for (IfcRelDefines isDefinedBy : obj4.getIsDefinedBy_Inverse()) {
				if (isDefinedBy instanceof IfcRelDefinesByProperties.Ifc4) {
					IfcRelDefinesByProperties.Ifc4 isDefinedBy4 = (IfcRelDefinesByProperties.Ifc4) isDefinedBy;

					if (isDefinedBy4.getRelatingPropertyDefinition() instanceof IfcElementQuantity.Ifc4) {
						IfcElementQuantity.Ifc4 elementQuan = (IfcElementQuantity.Ifc4) isDefinedBy4
								.getRelatingPropertyDefinition();
						if (elementQuan.getName().getDecodedValue().equals(setName)) {
							for (IfcPhysicalQuantity.Ifc4 quantity : elementQuan.getQuantities()) {
								
								if (quantity instanceof IfcPhysicalSimpleQuantity) {
									IfcPhysicalSimpleQuantity.Ifc4 simpleQuantity = (IfcPhysicalSimpleQuantity.Ifc4)quantity;
									if (simpleQuantity.getName().getDecodedValue().equals(propertyName)) {
										if (values==null)
											resultValues.add(obj4);
										else {
											
											for(Object value : values) {
												if (simpleQuantity instanceof IfcQuantityLength.Ifc4) {
													IfcQuantityLength.Ifc4 quanValue = (IfcQuantityLength.Ifc4)simpleQuantity;
													if (Double.valueOf(value.toString()) == quanValue.getLengthValue().getValue()) {
														resultValues.add(obj4);
														break;
													}
												} else if (simpleQuantity instanceof IfcQuantityCount.Ifc4){
													IfcQuantityCount.Ifc4 quanValue = (IfcQuantityCount.Ifc4)simpleQuantity;
													if (Double.valueOf(value.toString()) == quanValue.getCountValue().getValue()) {
															resultValues.add(obj4);
														break;
													}
												} else if (simpleQuantity instanceof IfcQuantityArea.Ifc4){
													IfcQuantityArea.Ifc4 quanValue = (IfcQuantityArea.Ifc4)simpleQuantity;
													if (Double.valueOf(value.toString()) == quanValue.getAreaValue().getValue()) {
														resultValues.add(obj4);
														break;
													}
												} else if (simpleQuantity instanceof IfcQuantityVolume.Ifc4){
													IfcQuantityVolume.Ifc4 quanValue = (IfcQuantityVolume.Ifc4)simpleQuantity;
													if (Double.valueOf(value.toString()) == quanValue.getVolumeValue().getValue()) {
														resultValues.add(obj4);
														break;
													}
												} else if (simpleQuantity instanceof IfcQuantityWeight.Ifc4){
													IfcQuantityWeight.Ifc4 quanValue = (IfcQuantityWeight.Ifc4)simpleQuantity;
													if (Double.valueOf(value.toString()) == quanValue.getWeightValue().getValue()) {
														resultValues.add(obj4);
														break;
													}
												} else {
													//FIXME Support other comparisons than labels and booleans. Maybe use java reflexion?
													System.err.println("FilterByQuantity has tied to parse a Type its not familiar with: " + simpleQuantity.getClassName());
												}
											} 
											
										}
									}
									
								}else {
									//FIXME Other property types than IfcSimplePropertyValue
									System.err.println("FilterByQuantity has tied to parse a Type its not familiar with: " + quantity.getClassName());
								}
							}

						}
					} // endif property set

				}
			} // foreach isDefined

		} // for obj
			
		if(resultValues.size()==1)
			setResult(0, resultValues.get(0));
		else
			setResult(0, resultValues);	
	}

}
