package openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.ifctoolbox.ifc.IfcProductRepresentation;
import com.apstex.ifctoolbox.ifc.IfcRepresentation;
import com.apstex.ifctoolbox.ifc.IfcRepresentationItem;
import com.apstex.ifctoolbox.ifc.IfcShapeRepresentation;

import engine.openbimrl.inf.bi.rub.de.ifc.*;
import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Adds a representation item to the model.
 * 
 * @author Marcel Stepien
 *
 */
public class AddRepresentationItemToEntity extends AbstractFunction {

	private ArrayList<IIFCProductRepresentation> reps = new ArrayList<>();
	private ArrayList<IIFCRepresentation> memory = new ArrayList<>();
	
	public AddRepresentationItemToEntity(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		Object input2 = getInput(2);
		Object input3 = getInput(3);
		
		if(input0 == null)
			return;
				
		Collection<?> elements = null;
		if(input0 instanceof Collection<?>) {
			elements = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input0);
			elements = newList;
		}
		
		
		if(input1 == null)
			return;
		
		Collection<?> polylines = null;
		if(input1 instanceof Collection<?>) {
			polylines = (Collection<?>) input1;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input1);
			polylines = newList;
		}

		
		String identifier = "Body";
		if(input2 instanceof String) {
			identifier = (String)input2;
		}
		
		String type = "Tessellation";
		if(input3 instanceof String) {
			type = (String)input3;
		}
		
		//reset Memory
		this.memReset(ifcModel);

		ArrayList<IIFCRepresentation> resultValues = new ArrayList<>();
		
		for(Object element : elements) {
			IIFCClass classInterface = (IIFCClass) element;
			
			if(classInterface instanceof IIFCProduct) {
				var prodRep = ((IIFCProduct)classInterface).getRepresentation();
				if(prodRep != null) {
					reps.add(prodRep);
					
					for(Object repItemObj : polylines) {
						if(repItemObj instanceof IIFCRepresentationItem) {
							var rep = IIFCShapeRepresentation.IFC4.Instance.create();
							rep.addItems((IfcRepresentationItem)repItemObj);
							rep.setRepresentationIdentifier(new IfcLabel.Ifc4(identifier));
							rep.setRepresentationType(new IfcLabel.Ifc4(type));
							ifcModel.getStepModel().addObject(rep);
							memory.add(rep);
							
							
							prodRep.addRepresentations(rep);
							resultValues.add(rep);
							
							
						}
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

	private void memReset(IIFCModel ifcModel) {
		for(IfcProductRepresentation rep : reps) {
			rep.removeAllRepresentations(memory);
		}
		
		for(ClassInterface obj : memory) {
			ifcModel.removeObject(obj);
		}
		
		memory = new ArrayList<IfcRepresentation>();
		reps = new ArrayList<IfcProductRepresentation>();
	}
	
}
