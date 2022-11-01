package openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.geometry.euclidean.threed.Planes;
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;
import org.apache.commons.geometry.euclidean.threed.Triangle3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
//import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.core.Precision;

import com.apstex.gui.core.j3d.model.cadobjectmodel.CadObjectJ3D;
import com.apstex.gui.core.j3d.model.cadobjectmodel.MultiAppearanceShape3D;
import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.gui.core.model.cadobjectmodel.CadObject;
import com.apstex.gui.core.model.cadobjectmodel.SolidShape;
import com.apstex.ifctoolbox.ifc.IfcObjectDefinition;
import com.apstex.ifctoolbox.ifc.IfcRelAggregates;
import com.apstex.j3d.utils.geometry.GeometryInfo;
import com.apstex.javax.media.j3d.GeometryArray;
import com.apstex.step.core.ClassInterface;
import com.apstex.step.core.SET;

import openbimrl.NodeProxy;
import openbimrl.functions.AbstractFunction;

/**
 * Checks the intersection between lists of elements.
 * 
 * @author Marcel Stepien
 *
 */
public class CheckElementIntersection extends AbstractFunction {
	
	public CheckElementIntersection(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(ApplicationModelNode ifcModel) {
	
		Object input0 = getInput(0);
		Object input1 = getInput(1);
		String input2 = getInput(2);
		
		if(input0 == null || input1 == null)
			return;
		
		double scaleFactor = 1.0;
		if(input2 != null) {
			scaleFactor = Double.parseDouble(input2);
		}
		
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
		
		int [] stripCount = {3};
		
		LinkedHashMap<Object, Set<ClassInterface>> resultValues = new LinkedHashMap<Object, Set<ClassInterface>>();
		double precision = 1e-6;
		
		for(Object ele0 : elements0) {
			if(ele0 instanceof ClassInterface) {
				
				HashSet<ClassInterface> subList = new HashSet<ClassInterface>();

				ClassInterface classInterface0 = (ClassInterface) ele0;

				ArrayList<SolidShape> geomShapesA = getSolidShapes(ifcModel, classInterface0);
				ArrayList<RegionBSPTree3D> geomsA = null;
				
				try {
					 geomsA = convertToSTM(geomShapesA, precision);
				} catch (Exception e) {
					System.out.println("Caused by: " + classInterface0.getStepLine());
				}
				
				for(Object ele1 : elements1) {
					if(ele1 instanceof ClassInterface) {
						
						ClassInterface classInterface1 = (ClassInterface) ele1;

						//System.out.println(classInterface1.getStepLine());
						ArrayList<SolidShape> geomShapesB = getSolidShapes(ifcModel, classInterface1);
						ArrayList<RegionBSPTree3D> geomsB = null;
						
						try {
							geomsB = convertToSTM(geomShapesB, precision);
						} catch (Exception e) {
							System.out.println("Caused by: " + classInterface1.getStepLine());
						}
						
						for(RegionBSPTree3D m1 : geomsA) {
							for(RegionBSPTree3D m2 : geomsB) {
								try {
									RegionBSPTree3D regionM1 = m1.copy();
									RegionBSPTree3D regionM2 = m2.copy();
									
									regionM1.intersection(regionM2);
									if(regionM1.getSize() > 0.0) {
										//System.out.println(test + " | " +  regionM1.getSize());
										subList.add(classInterface1);
									}
									
									/*
									if(m1.getBounds().intersects(m2.getBounds())) {
										subList.add(classInterface1);									
									}
									*/
									
								} catch (Exception e) {
									System.err.println("Some geometry could not be converted to BSPTree.");
									//System.err.println(ele1.toString());
									e.printStackTrace();
									//System.out.println(m2.getVertexCount() + " - " + m2.getFaceCount());
									//System.out.println(m2.getBounds().toString());
								}
								
							}
						}
						
						
					}
				}
				resultValues.put(classInterface0, subList);
				
			}
		}
		
		setResult(0, resultValues);
		
	}
	
	private ArrayList<SolidShape> getSolidShapes(ApplicationModelNode ifcModel, ClassInterface ci) {
		ArrayList<SolidShape> geomShapes = new ArrayList<SolidShape>();
		
		CadObject cad = ifcModel.getCadObjectModel().getCadObject(ci);
			
		List l = ((CadObjectJ3D)cad).getSolidShapes();
		
		geomShapes.addAll(((CadObjectJ3D)cad).getSolidShapes());
		
		//Resolve decompositions
		if(geomShapes.isEmpty()) {
			if(ci instanceof IfcObjectDefinition.Ifc4) {
				for(ClassInterface ifcObj : getDecomposedElements((IfcObjectDefinition.Ifc4)ci)) {
					geomShapes.addAll(getSolidShapes(ifcModel, ifcObj));
				}
			}
		}
		
		
		return geomShapes;
	}

	private ArrayList<RegionBSPTree3D> convertToSTM(ArrayList<SolidShape> geomShapes, double precision){
		ArrayList<RegionBSPTree3D> geoms = new ArrayList<RegionBSPTree3D>();
		
		for(SolidShape shape : geomShapes) {
			MultiAppearanceShape3D sShape = ((MultiAppearanceShape3D)shape);
			
			if(sShape.getGeometry() instanceof GeometryArray) {
				GeometryInfo geoInfo = new GeometryInfo((GeometryArray)sShape.getGeometry());
				//geoInfo.setStripCounts(new int[]{3});
			    
				//System.out.println(Arrays.toString(geoInfo.getCoordinateIndices()));
				//System.out.println("T: " + Arrays.toString(geoInfo.getContourCounts()));
				//System.out.println("T: " + Arrays.toString(geoInfo.getStripCounts()));
				
				/*
				int rest = geoInfo.getCoordinateIndices().length % 3;
				if(rest != 0) {
					System.err.println("Some geometries are not properly triangulated and can not be converted."); 
					System.err.println("[" +geoInfo.getCoordinateIndices().length + " % 3 = " + rest + "]");
					continue;
				}
				*/

				//ALTERNATIVE C
				//===================================================================
			
				/*
				NormalGenerator ng = new NormalGenerator();
				ng.generateNormals(geoInfo);
				
				Stripifier st = new Stripifier();
				st.stripify(geoInfo);
				
				//geoInfo.compact();
				//geoInfo.indexify();
				//geoInfo.recomputeIndices();
				
				//geoInfo.convertToIndexedTriangles();
				//new Triangulator().triangulate(geoInfo);
				
				IndexedTriangleStripArray tri = ((IndexedTriangleStripArray)geoInfo.getIndexedGeometryArray());
				
				int[] strips = new int[tri.getNumStrips()]; 
				tri.getStripIndexCounts(strips);


				ArrayList<Vector3D> vecs = new ArrayList<Vector3D>();
				ArrayList<int []> triCoordIdx = new ArrayList<int []>();
				
				int [] indices = geoInfo.getCoordinateIndices();
				System.out.println(Arrays.toString(indices));
				
				int sum = 0;
				for(int strip : strips) {

					System.out.print(tri.getCoordinateIndex(sum) + ", ");
					int firstPointIndex = indices[sum];
					for(int i = sum; i + 2 < (sum + strip); i += 1) {
						System.out.print(tri.getCoordinateIndex(i + 1) + ", ");
						System.out.print(tri.getCoordinateIndex(i + 2) + ", ");
						int secondPointIndex = indices[i];
						int thirdPointIndex = indices[i+1];
						
						double [] coordPA = new double[3];
						tri.getCoordinate(firstPointIndex, coordPA);
						Vector3D vecA = Vector3D.of(coordPA);
						vecs.add(vecA);
								
						double [] coordPB = new double[3];
						tri.getCoordinate(secondPointIndex, coordPB);
						Vector3D vecB = Vector3D.of(coordPB);
						vecs.add(vecB);
						
						double [] coordPC = new double[3];
						tri.getCoordinate(thirdPointIndex, coordPC);
						Vector3D vecC = Vector3D.of(coordPC);
						vecs.add(vecC);
						
						int [] triangle = {vecs.indexOf(vecC), vecs.indexOf(vecB), vecs.indexOf(vecA)};
						triCoordIdx.add(triangle);
						
					}
					sum += strip;
					//System.out.println();
					//System.out.println(strip);
				}
				
				System.out.println(sum + " | " + indices.length);
				
				SimpleTriangleMesh mesh = SimpleTriangleMesh.from(
						vecs, 
						triCoordIdx, 
						Precision.doubleEquivalenceOfEpsilon(precision)
				);
				
				geoms.add(mesh.toTree());
				*/
				
				
				
				/*
				Vector3D[] vecs = new Vector3D[tri.getVertexCount()];
				
				
				int[][] triCoordIdx = new int[tri.getIndexCount()/3][3];
				int triangleCount = 0;
				int indexCount = tri.getIndexCount();
				for (int i = 2; i < indexCount; i += 3) {

					int indexTriPA = tri.getCoordinateIndex(i-2);
					double [] coordPA = new double[3];
					tri.getCoordinate(indexTriPA, coordPA);
					vecs[indexTriPA] = Vector3D.of(coordPA);
							
					int indexTriPB = tri.getCoordinateIndex(i-1);
					double [] coordPB = new double[3];
					tri.getCoordinate(indexTriPB, coordPB);
					vecs[indexTriPB] = Vector3D.of(coordPB);
					
					int indexTriPC = tri.getCoordinateIndex(i);
					double [] coordPC = new double[3];
					tri.getCoordinate(indexTriPC, coordPC);
					vecs[indexTriPC] = Vector3D.of(coordPC);
					
					triCoordIdx[triangleCount][0] = indexTriPA;
					triCoordIdx[triangleCount][1] = indexTriPB;
					triCoordIdx[triangleCount][2] = indexTriPC;
					triangleCount++;
				}
				
				//System.out.println(Arrays.toString());
				
				SimpleTriangleMesh mesh = SimpleTriangleMesh.from(
						vecs, 
						triCoordIdx, 
						Precision.doubleEquivalenceOfEpsilon(precision)
				);
				
				geoms.add(mesh.toTree());
				*/
			
				
				//ALTERNATIVE A
				//===================================================================
				
				
				//System.out.println(Arrays.toString(geoInfo.getCoordinateIndices()));

				
				RegionBSPTree3D tree3d = RegionBSPTree3D.empty();
				
				for(int i = 2; i < geoInfo.getCoordinateIndices().length; i += 3) {
					
					int[] triangles = new int[3];
					triangles[0] = geoInfo.getCoordinateIndices()[i-2];
					triangles[1] = geoInfo.getCoordinateIndices()[i-1];
					triangles[2] = geoInfo.getCoordinateIndices()[i];
					
					Vector3D v0 = Vector3D.of(
							geoInfo.getCoordinates()[triangles[0]].getX(), 
							geoInfo.getCoordinates()[triangles[0]].getY(), 
							geoInfo.getCoordinates()[triangles[0]].getZ()
					);
					
					Vector3D v1 = Vector3D.of(
							geoInfo.getCoordinates()[triangles[1]].getX(), 
							geoInfo.getCoordinates()[triangles[1]].getY(), 
							geoInfo.getCoordinates()[triangles[1]].getZ()
					);
					
					Vector3D v2 = Vector3D.of(
							geoInfo.getCoordinates()[triangles[2]].getX(), 
							geoInfo.getCoordinates()[triangles[2]].getY(), 
							geoInfo.getCoordinates()[triangles[2]].getZ()
					);
					
					Triangle3D triangle = Planes.triangleFromVertices(v0, v1, v2, Precision.doubleEquivalenceOfEpsilon(precision));

					tree3d.insert(
						triangle
					);
					
					
					//System.out.println("[" +v0.toString() + ", " + v1.toString() + ", " + v2.toString() + "]");
					//System.out.println(v0.eq(v1, Precision.doubleEquivalenceOfEpsilon(precision)));
					//System.out.println(v1.eq(v2, Precision.doubleEquivalenceOfEpsilon(precision)));
					//System.out.println(v2.eq(v0, Precision.doubleEquivalenceOfEpsilon(precision)));
					
					//builder.addFaceAndVertices(v0, v1, v2);
					
				}
				
				geoms.add(tree3d);
				
				

				//ALTERNATIVE B
				//===================================================================
				
				/*
				ArrayList<Vector3D> vector3ds = new ArrayList<Vector3D>();
				for(Point3f p : geoInfo.getCoordinates()) {
					vector3ds.add(
							Vector3D.of(
									p.getX(), 
									p.getY(), 
									p.getZ()
							)
					);
				}

				ArrayList<int[]> faces = new ArrayList<int[]>();
				for(int i = 2; i < geoInfo.getCoordinateIndices().length; i += 3) {
					
					int[] triangles = new int[3];
					triangles[0] = geoInfo.getCoordinateIndices()[i-2];
					triangles[1] = geoInfo.getCoordinateIndices()[i-1];
					triangles[2] = geoInfo.getCoordinateIndices()[i];
					
					faces.add(triangles);
				}
				
				
				//SimpleTriangleMesh mesh = builder.build();
				SimpleTriangleMesh mesh = SimpleTriangleMesh.from(
						vector3ds, 
						faces, 
						Precision.doubleEquivalenceOfEpsilon(precision)
				);
				
				
				geoms.add(mesh.toTree());
				*/
				
			}
		}
		return geoms;
	}
	
	private ArrayList<IfcObjectDefinition> getDecomposedElements(IfcObjectDefinition.Ifc4 product){
		ArrayList<IfcObjectDefinition> decompositionSet = new ArrayList<IfcObjectDefinition>();
		
		SET<IfcRelAggregates.Ifc4> decompRelObjs = product.getIsDecomposedBy_Inverse();
		
		if(decompRelObjs != null) {
			for(IfcRelAggregates.Ifc4 decompRelObj : decompRelObjs) {
				IfcRelAggregates.Ifc4 decompRel = (IfcRelAggregates.Ifc4)decompRelObj;
				decompositionSet.addAll(decompRel.getRelatedObjects());
				
				for(IfcObjectDefinition.Ifc4 decompObj : decompRel.getRelatedObjects()) {
					decompositionSet.addAll(getDecomposedElements(decompObj));
				}
			}
		}
		
		return decompositionSet;
	}
	
}
