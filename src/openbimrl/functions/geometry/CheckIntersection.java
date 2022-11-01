package openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.geometry.core.internal.HyperplaneSubsets;
import org.apache.commons.geometry.euclidean.threed.PlaneConvexSubset;
import org.apache.commons.geometry.euclidean.threed.Planes;
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D.PartitionedRegionBuilder3D;
import org.apache.commons.geometry.euclidean.threed.Triangle3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Line3D;
import org.apache.commons.geometry.euclidean.threed.line.LineConvexSubset3D;
import org.apache.commons.geometry.euclidean.threed.line.Lines3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;
import org.apache.commons.geometry.euclidean.threed.mesh.SimpleTriangleMesh;
import org.apache.commons.math3.geometry.euclidean.threed.SubLine;
//import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.core.Precision;

import com.apstex.gui.core.j3d.model.cadobjectmodel.CadObjectJ3D;
import com.apstex.gui.core.j3d.model.cadobjectmodel.MultiAppearanceShape3D;
import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.gui.core.model.cadobjectmodel.CadObject;
import com.apstex.gui.core.model.cadobjectmodel.SolidShape;
import com.apstex.ifctoolbox.ifc.IfcBuildingElementPart;
import com.apstex.ifctoolbox.ifc.IfcObject;
import com.apstex.ifctoolbox.ifc.IfcObjectDefinition;
import com.apstex.ifctoolbox.ifc.IfcProduct;
import com.apstex.ifctoolbox.ifc.IfcRelAggregates;
import com.apstex.ifctoolbox.ifc.IfcRelDecomposes;
import com.apstex.ifctoolbox.ifcmodel.IfcModel;
import com.apstex.j3d.utils.geometry.GeometryInfo;
import com.apstex.j3d.utils.geometry.NormalGenerator;
import com.apstex.j3d.utils.geometry.Stripifier;
import com.apstex.j3d.utils.geometry.Triangulator;
import com.apstex.javax.media.j3d.Behavior;
import com.apstex.javax.media.j3d.BoundingBox;
import com.apstex.javax.media.j3d.BoundingPolytope;
import com.apstex.javax.media.j3d.Bounds;
import com.apstex.javax.media.j3d.GeometryArray;
import com.apstex.javax.media.j3d.IndexedGeometryArray;
import com.apstex.javax.media.j3d.IndexedTriangleArray;
import com.apstex.javax.media.j3d.IndexedTriangleStripArray;
import com.apstex.javax.media.j3d.Node;
import com.apstex.javax.media.j3d.Shape3D;
import com.apstex.javax.media.j3d.Transform3D;
import com.apstex.javax.media.j3d.WakeupCriterion;
import com.apstex.javax.media.j3d.WakeupOnCollisionEntry;
import com.apstex.javax.media.j3d.WakeupOnCollisionExit;
import com.apstex.javax.media.j3d.WakeupOnCollisionMovement;
import com.apstex.javax.media.j3d.WakeupOr;
import com.apstex.javax.vecmath.Point3d;
import com.apstex.javax.vecmath.Point3f;
import com.apstex.step.core.ClassInterface;
import com.apstex.step.core.SET;
import com.jogamp.graph.curve.Region;

import jogamp.graph.font.typecast.ot.table.CffTable.Index;
import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;


/**
 * Checks if geometies of two RegionalBSPTree3D are intersecting.
 * 
 * @author Marcel Stepien
 *
 */
public class CheckIntersection extends AbstractFunction {
	
	public CheckIntersection(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		
		if(input0 == null || input1 == null)
			return;
		
		Collection<?> elements0 = null;
		if(input0 instanceof Collection<?>) {
			elements0 = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input0);
			elements0 = newList;
		}
		
		Collection<?> elements1 = null;
		if(input1 instanceof Collection<?>) {
			elements1 = (Collection<?>) input1;
		}else {
			ArrayList<Object> newList = new ArrayList<Object>();
			newList.add(input1);
			elements1 = newList;
		}
		
		LinkedHashMap<Object, ArrayList<Boolean>> resultValues = new LinkedHashMap<Object, ArrayList<Boolean>>();
		
		for(Object ele0 : elements0) {
			if(ele0 instanceof RegionBSPTree3D) {

				ArrayList<Boolean> filter = new ArrayList<Boolean>();
				
				for(Object ele1 : elements1) {
					if(ele1 instanceof RegionBSPTree3D) {
						
						try {
							RegionBSPTree3D regionM1 = ((RegionBSPTree3D)ele0).copy();
							RegionBSPTree3D regionM2 = ((RegionBSPTree3D)ele1).copy();
							
							regionM1.intersection(regionM2);
							if(regionM1.getSize() > 0.0) {
								filter.add(true);
							}else {
								filter.add(false);
							}
							
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
				
				resultValues.put(ele0, filter);
				
			}
		}
		
		setResult(0, resultValues);
		
	}
	
}
