package openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.ifctoolbox.ifc.IfcElement;
import com.apstex.ifctoolbox.ifc.IfcInternalOrExternalEnum;
import com.apstex.ifctoolbox.ifc.IfcRelSpaceBoundary;
import com.apstex.ifctoolbox.ifc.IfcSpace;
import com.apstex.step.core.ClassInterface;
import com.apstex.step.core.SET;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Checks space bounds of a lists of elements and retrieves connected elements.
 * 
 * @author Marcel Stepien
 *
 */
public class CheckSpaceBounds extends AbstractFunction {

	public CheckSpaceBounds(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null) {
			return;
		}
				
		Collection<?> elements0 = null;
		if(input0 instanceof Collection<?>) {
			elements0 = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input0);
			elements0 = newList;
		}
		

		ArrayList<IfcElement> resultValues = new ArrayList<IfcElement>();
		
		for(Object obj : elements0) {
			if(!(obj instanceof IfcSpace)) {
				continue;
			}
			IfcSpace space = (IfcSpace)obj;
			
			SET<? extends IfcRelSpaceBoundary> boundedby = space.getBoundedBy_Inverse();
			if(boundedby != null) {
				for (IfcRelSpaceBoundary relSpaceBoundary : boundedby) {
				   IfcElement relatedElement = relSpaceBoundary.getRelatedBuildingElement();
				   if(relatedElement == null) {
					   continue;
				   }
				   
				   if(!relSpaceBoundary.getInternalOrExternalBoundary().getValue().equals(
						   IfcInternalOrExternalEnum.Ifc4.IfcInternalOrExternalEnum_internal.INTERNAL)) {
					   continue;
				   }
				   
				   System.out.println(
						   relSpaceBoundary.getInternalOrExternalBoundary().toString() + " " +
						   relSpaceBoundary.getName() + " / " +
						   relSpaceBoundary.getDescription() + " " +
						   relSpaceBoundary.getPhysicalOrVirtualBoundary().getValue()
				   );
					   
				   if(relatedElement instanceof ClassInterface) {			   
					   resultValues.add(relatedElement);
				   }
				}
			}
			
		}
		
		setResult(0, resultValues);
	}

}
