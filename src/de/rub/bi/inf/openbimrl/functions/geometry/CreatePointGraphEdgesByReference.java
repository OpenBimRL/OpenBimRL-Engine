package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Lines3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;
import org.apache.commons.numbers.core.Precision;

/**
 * Connects nodes of an graph by inserting edges of a certain stepsize, provided the nodes are already known. Priorisation on reference nodes.
 * 
 * @author Marcel Stepien
 *
 */
public class CreatePointGraphEdgesByReference extends AbstractFunction {
	
	public CreatePointGraphEdgesByReference(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		Collection<?> nodes = null;
		if(input0 instanceof Collection<?>) {
			nodes = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<>();
			newList.add(input0);
			nodes = newList;
		}
		
		Object input1 = getInput(1);
		
		if(input1 == null)
			return;
				
		Collection<?> nodesReference = null;
		if(input1 instanceof Collection<?>) {
			nodesReference = (Collection<?>) input1;
		}else {
			ArrayList<Object> newList = new ArrayList<>();
			newList.add(input1);
			nodesReference = newList;
		}
		
		String input2 = getInput(2);
		if(input2 == null)
			return;
		
		double stepSize = Double.parseDouble(input2);
		
		ArrayList<Object> edges = createGraphEdge(nodes, nodesReference, stepSize);
		
		setResult(0, edges);
	}
	
	private ArrayList<Object> createGraphEdge(Collection<?> nodes, Collection<?> nodesReference, double stepSize){
		ArrayList<Object> linearEdgeList = new ArrayList<Object>();
		
		double increasedStepSize = stepSize; //* Math.sqrt(2) + 0.001;
		
		for(Object sNode : nodes) {
			
			if(sNode instanceof Collection) {
				linearEdgeList.add(
					createGraphEdge((Collection)sNode, nodesReference, stepSize)
				);
			}
			
			if(sNode instanceof Vector3D) {
				
				for(Object eNode : nodesReference) {
					if(eNode instanceof Vector3D) {
						
						if(!sNode.equals(eNode)) {
							double distance = ((Vector3D)sNode).distance((Vector3D)eNode);
							if(distance <= increasedStepSize && distance > 0.0) {
								
								Segment3D edge = Lines3D.segmentFromPoints(
										(Vector3D)sNode, 
										(Vector3D)eNode, 
										Precision.doubleEquivalenceOfEpsilon(1e-6)
								);
								
								Segment3D edgeInverse = Lines3D.segmentFromPoints(
										(Vector3D)eNode, 
										(Vector3D)sNode, 
										Precision.doubleEquivalenceOfEpsilon(1e-6)
								);
								
								linearEdgeList.add(edge);
								linearEdgeList.add(edgeInverse);
												
							}
						}
						
					}
				}
				
			}
		}
		
		return linearEdgeList;
	}

}
