package de.rub.bi.inf.openbimrl.functions.geometry;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/**
 * Loads the paths information and displays it in the 3D viewer.
 * 
 * @author Marcel Stepien
 *
 */
public class ShowPaths extends AbstractFunction {

	public ShowPaths(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		
		Collection<?> paths = null;
		if(input0 != null) {
			if(input0 instanceof Collection<?>) {
				paths = (Collection<?>) input0;
			}else {
				ArrayList<Object> newList = new ArrayList<>();
				newList.add(input0);
				paths = newList;
			}
		}
		
		//Appearance nodeAppearance = createAppearance(Color.GRAY);
		Appearance edgeAppearance = createAppearance(Color.GREEN);
	
		BranchGroup group = new BranchGroup();
		
		for(Object path : paths) {
			if(path instanceof ArrayList) {
				ArrayList pathList = (ArrayList)path;
				//handleNodes(pathList, group, nodeAppearance);
				showPathEdge(pathList, group, edgeAppearance);
			}
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
	
	private void showPathEdge(ArrayList path, BranchGroup group, Appearance appearance){
								
		int vertexCounts[] = {path.size()};
		LineStripArray lineArr = new LineStripArray(path.size(), GeometryArray.COORDINATES, vertexCounts);
		Point3d[] points = new Point3d[path.size()];
		
		int index = 0;
		for(Object vecObj : path) {
			if(vecObj instanceof Vector3D) {
				Vector3D pointOnLine = (Vector3D)vecObj;	
					
				points[index] = new Point3d(
					pointOnLine.getX(), 
					pointOnLine.getY(), 
					pointOnLine.getZ()
				);

				index++;
			}
		}
		
		lineArr.setCoordinates(0, points);

		Shape3D lineShape = new Shape3D(lineArr, appearance);
		group.addChild(lineShape);
		
	}
	
	private Appearance createAppearance(Color color) {
		Appearance appearance = new Appearance();
    	appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);

		ColoringAttributes coloringAttributes = new ColoringAttributes(
				new Color3f(color), 
				ColoringAttributes.FASTEST
		);
		appearance.setColoringAttributes(coloringAttributes);
		
		LineAttributes lineAttributes = new LineAttributes();
		lineAttributes.setLinePattern(LineAttributes.PATTERN_SOLID);
		lineAttributes.setLineWidth(5.0f);
		lineAttributes.setLineAntialiasingEnable(true);
		appearance.setLineAttributes(lineAttributes);
		
		PolygonAttributes polygonAttributes = new PolygonAttributes();
		polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
		polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		appearance.setPolygonAttributes(polygonAttributes);
		
		return appearance;
	}

}
