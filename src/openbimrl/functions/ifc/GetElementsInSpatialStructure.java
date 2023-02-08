package openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.ifctoolbox.ifc.IfcRelAggregates;
import com.apstex.ifctoolbox.ifc.IfcRelContainedInSpatialStructure;
import com.apstex.ifctoolbox.ifc.IfcSpatialStructureElement;
import com.apstex.step.core.SET;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Find the elements that are included in a spatial structure element.
 * 
 * @author Marcel Stepien
 *
 */
public class GetElementsInSpatialStructure extends AbstractFunction {

	public GetElementsInSpatialStructure(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
		
		Collection<Object> objects = new ArrayList();
		Object input0 = getInput(0);
		if(input0 instanceof Collection<?>) {
			objects = (Collection<Object>)input0;
		}else {
			objects.add(input0);
		}
		
		ArrayList<Object> resultValues = new ArrayList<>();
		
		//System.out.println("GetElementsInSpatialStructure: " + objects);
		
		for(Object o : objects) {
			if(o instanceof IfcSpatialStructureElement) {
				
				if(((IfcSpatialStructureElement)o).getContainsElements_Inverse() != null) {
					for(IfcRelContainedInSpatialStructure relContains : 
						((IfcSpatialStructureElement)o).getContainsElements_Inverse()) {
						SET<?> elements = relContains.getRelatedElements();
						resultValues.addAll(elements);				
					}
				}
				
				if(((IfcSpatialStructureElement.Ifc4)o).getIsDecomposedBy_Inverse() != null) {
					for(IfcRelAggregates relAggregates : 
						((IfcSpatialStructureElement.Ifc4)o).getIsDecomposedBy_Inverse()) {
						SET<?> elements = relAggregates.getRelatedObjects();
						resultValues.addAll(elements);				
					}
				}
				
			}
		}

		setResult(0, resultValues);
		
	}

}
