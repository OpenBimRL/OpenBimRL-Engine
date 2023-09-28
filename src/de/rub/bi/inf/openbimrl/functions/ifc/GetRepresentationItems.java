package de.rub.bi.inf.openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.IIFCModel;
import com.apstex.ifctoolbox.ifc.IfcProduct;
import com.apstex.ifctoolbox.ifc.IfcProductRepresentation;
import com.apstex.ifctoolbox.ifc.IfcRepresentation;
import com.apstex.ifctoolbox.ifc.IfcRepresentationItem;
import com.apstex.step.core.ClassInterface;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Retrieves the representation items based on the IFC element.
 * 
 * @author Marcel Stepien
 *
 */
public class GetRepresentationItems extends AbstractFunction {

	public GetRepresentationItems(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		Collection<?> objects = null;
		if(input0 instanceof Collection<?>) {
			objects = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input0);
			objects = newList;
		}

		ArrayList<IfcRepresentationItem> resultValues = new ArrayList<IfcRepresentationItem>();
		
		for(Object o : objects) {
			ClassInterface classInterface = (ClassInterface) o;
			
			if(classInterface instanceof IfcProduct) {
				IfcProductRepresentation prodRep = ((IfcProduct)classInterface).getRepresentation();
				if(prodRep != null) {
					for(IfcRepresentation rep : prodRep.getRepresentations()) {
						resultValues.addAll(rep.getItems());
					}
				}
			}
			
		}
		
		if(resultValues.size()==1) {
			setResult(0, resultValues.get(0));
		} else {
			setResult(0, resultValues);
		}
	}

}
