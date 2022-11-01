package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Computes nodes for a graph containing with certain parametric behavior, such a for creating triangles or quad patches.
 * 
 * @author Marcel Stepien
 *
 */
public class CreatePointGraphNodes extends AbstractFunction {
	
	public CreatePointGraphNodes(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		Collection<?> bspTrees = null;
		if(input0 instanceof Collection<?>) {
			bspTrees = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<>();
			newList.add(input0);
			bspTrees = newList;
		}
		
		String input1 = getInput(1);
		if(input1 == null)
			return;
		
		double stepSize = Double.parseDouble(input1);
		
		String input2 = getInput(2);
		if(input2 == null)
			return;
		
		double height = Double.parseDouble(input2);
		
		String input3 = getInput(3);
		if(input3 == null)
			input3 = "0.0";
		
		double jitter = Double.parseDouble(input3);
		
		
		ArrayList<ArrayList<Vector3D>> listOfNodes = new ArrayList<ArrayList<Vector3D>>();
		
		for(Object o : bspTrees) {
			
			if(o instanceof RegionBSPTree3D) {
				ArrayList<Vector3D> nodes = createGraphNodes((RegionBSPTree3D)o, stepSize, height, jitter);
				listOfNodes.add(nodes);
			}
			
		}
		
		setResult(0, listOfNodes);
	}
	
	private ArrayList<Vector3D> createGraphNodes(RegionBSPTree3D bspTree, double stepSize, double height, double jitter){
		Vector3D cA = bspTree.getBounds().getMin();
		Vector3D cB = bspTree.getBounds().getMax();
		
		double margin = 0.2;
		
		double offsetX = (cB.getX() - cA.getX() - margin*2) % stepSize;
		double offsetXPerPoint = (int)((cB.getX() - cA.getX() - margin*2) / stepSize);
		offsetX = offsetX / offsetXPerPoint;
		
		double offsetY = (cB.getY() - cA.getY() - margin*2) % stepSize;
		double offsetYPerPoint = (int)((cB.getY() - cA.getY() - margin*2) / stepSize);
		offsetY = offsetY / offsetYPerPoint;
		
		ArrayList<Vector3D> linearPointList = new ArrayList<Vector3D>();
		
		int xStep = 0;
		for(double x = cA.getX() + margin; x <= cB.getX(); x += (stepSize + offsetX)) {
			
			double jitterOffset = 0.0;
			if((xStep % 2) != 0) {
				jitterOffset = jitter;
			}
			
			for(double y = cA.getY() + jitterOffset + margin; y <= cB.getY(); y += (stepSize + offsetY)) {
				
				Vector3D gridPoint = Vector3D.of(x, y, height);
				
				if(bspTree.contains(gridPoint)) {
					linearPointList.add(gridPoint);
				}
				
			}
			
			xStep++;
		}
		
		return linearPointList;
	}
	
}
