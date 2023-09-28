package de.rub.bi.inf.openbimrl.functions.geometry;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;

/**
 * Loads the graph information and displays it in the 3D viewer.
 * 
 * @author Marcel Stepien
 *
 */
public class ShowGraph extends AbstractFunction {

	public ShowGraph(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		
		Collection<?> nodes = null;
		if(input0 != null) {
			if(input0 instanceof Collection<?>) {
				nodes = (Collection<?>) input0;
			}else {
				ArrayList<Object> newList = new ArrayList<>();
				newList.add(input0);
				nodes = newList;
			}
		}
		
		Object input1 = getInput(1);
		
		Collection<?> edges = null;
		if(input1 != null) {
			if(input1 instanceof Collection<?>) {
				edges = (Collection<?>) input1;
			}else {
				ArrayList<Object> newList = new ArrayList<>();
				newList.add(input1);
				edges = newList;
			}
		}
		
		Appearance nodeAppearance = createAppearance(Color.GRAY);
		Appearance edgeAppearance = createAppearance(Color.BLUE);
	
		BranchGroup group = new BranchGroup();
		
		if(nodes != null) {			
			handleNodes(nodes, group, nodeAppearance);
		}
		
		if(edges != null) {			
			handleEdges(edges, group, edgeAppearance);
		}
		
		CadObjectJ3D objectJ3D = new CadObjectJ3D();
		objectJ3D.addChild(group);
		
		//Add to viewer and shows geometry
		((CadObjectJ3D)ifcModel.getCadObjectModel().getRootBranchGroup()).addChild(objectJ3D);

		setResult(0, true);
	}
	
	private void handleNodes(Collection nodes, BranchGroup group, Appearance appearance) {
		for(Object o : nodes) {
			if(o instanceof Collection) {
				handleNodes((Collection)o, group, appearance);
			}else if(o instanceof Vector3D) {
				showGraphNodes((Vector3D)o, group, appearance);
			}
			
		}
	}
	
	private void handleEdges(Collection edges, BranchGroup group, Appearance appearance) {
		for(Object o : edges) {
			if(o instanceof Collection) {
				handleEdges((Collection)o, group, appearance);
			}else if(o instanceof Segment3D) {
				showGraphEdge((Segment3D)o, group, appearance);
			}
			
		}
	}
	
	private void showGraphNodes(Vector3D node, BranchGroup group, Appearance appearance){
		Sphere pointShape = new Sphere(0.1f, appearance);
		
		TransformGroup tg = new TransformGroup();
		Transform3D transform = new Transform3D();
		Vector3f vector = new Vector3f(
				(float)node.getX(), 
				(float)node.getY(), 
				(float)node.getZ()
		);
		transform.setTranslation(vector);
		tg.setTransform(transform);
		tg.addChild(pointShape);
		
		group.addChild(tg);
				
	}
	
	private void showGraphEdge(Segment3D edge, BranchGroup group, Appearance appearance){
								
		LineArray lineArr = new LineArray(2, LineArray.COORDINATES);
		lineArr.setCoordinate(0, new Point3d(
				edge.getStartPoint().getX(), 
				edge.getStartPoint().getY(), 
				edge.getStartPoint().getZ())
		);
		lineArr.setCoordinate(1, new Point3d(
				edge.getEndPoint().getX(), 
				edge.getEndPoint().getY(), 
				edge.getEndPoint().getZ())
		);
		
		Shape3D lineShape = new Shape3D(lineArr, appearance);
		
		group.addChild(lineShape);
	}
	
	private Appearance createAppearance(Color color) {
		Appearance appearance = new Appearance();
		ColoringAttributes coloringAttributes = new ColoringAttributes(
				new Color3f(color), 
				ColoringAttributes.FASTEST
		);
		appearance.setColoringAttributes(coloringAttributes);
		return appearance;
	}

}
