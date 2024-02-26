package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCClass;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Converts IFC elements into RegionalBSPTree3D objects.
 *
 * @author Marcel Stepien
 */
public class ConvertToRegionBSPTree extends AbstractFunction {

    public ConvertToRegionBSPTree(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        Object input0 = getInput(0);

        if (input0 == null)
            return;

        Collection<?> elements0 = null;
        if (input0 instanceof Collection<?>) {
            elements0 = (Collection<?>) input0;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            newList.add(input0);
            elements0 = newList;
        }

        double precision = 1e-14;

        final var results = new ArrayList<RegionBSPTree3D>();


        for (Object ele0 : elements0) {
            if (ele0 instanceof IIFCClass classInterface0) {

                // final var geomShapesA = getSolidShapes(ifcModel, classInterface0);

                try {
                    final RegionBSPTree3D converted = null;
                    //RegionBSPTree3D converted = convertToSTM(geomShapesA, precision);
                    results.add(converted);
                } catch (Exception e) {
                    // System.err.println("Could not convert entity to RegionBSPTree3D. Please check: " + classInterface0.getStepLine());
                    results.add(RegionBSPTree3D.empty());
                }

            }
        }

        if (results.size() == 1) {
            setResult(0, results.get(0));
        } else {
            setResult(0, results);
        }

    }
/*

    private ArrayList<SolidShape> getSolidShapes(IIFCModel ifcModel, IIFCClass ci) {
        ArrayList<SolidShape> geomShapes = new ArrayList<SolidShape>();

        CadObject cad = ifcModel.getCadObjectModel().getCadObject(ci);

        List l = ((CadObjectJ3D) cad).getSolidShapes();

        geomShapes.addAll(((CadObjectJ3D) cad).getSolidShapes());

        //Resolve decompositions
        if (geomShapes.isEmpty()) {
            if (ci instanceof IfcObjectDefinition.Ifc4) {
                for (ClassInterface ifcObj : getDecomposedElements((IfcObjectDefinition.Ifc4) ci)) {
                    geomShapes.addAll(getSolidShapes(ifcModel, ifcObj));
                }
            }
        }


        return geomShapes;
    }

    private RegionBSPTree3D convertToSTM(ArrayList<SolidShape> geomShapes, double precision) {
        RegionBSPTree3D tree3d = RegionBSPTree3D.empty();

        for (SolidShape shape : geomShapes) {
            MultiAppearanceShape3D sShape = ((MultiAppearanceShape3D) shape);

            if (sShape.getGeometry() instanceof GeometryArray) {
                GeometryInfo geoInfo = new GeometryInfo((GeometryArray) sShape.getGeometry());


                for (int i = 2; i < geoInfo.getCoordinateIndices().length; i += 3) {

                    int[] triangles = new int[3];
                    triangles[0] = geoInfo.getCoordinateIndices()[i - 2];
                    triangles[1] = geoInfo.getCoordinateIndices()[i - 1];
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
            }
        }
        return tree3d;
    }

    private ArrayList<IfcObjectDefinition> getDecomposedElements(IfcObjectDefinition.Ifc4 product) {
        ArrayList<IfcObjectDefinition> decompositionSet = new ArrayList<IfcObjectDefinition>();

        SET<IfcRelAggregates.Ifc4> decompRelObjs = product.getIsDecomposedBy_Inverse();

        if (decompRelObjs != null) {
            for (IfcRelAggregates.Ifc4 decompRelObj : decompRelObjs) {
                IfcRelAggregates.Ifc4 decompRel = (IfcRelAggregates.Ifc4) decompRelObj;
                decompositionSet.addAll(decompRel.getRelatedObjects());

                for (IfcObjectDefinition.Ifc4 decompObj : decompRel.getRelatedObjects()) {
                    decompositionSet.addAll(getDecomposedElements(decompObj));
                }
            }
        }

        return decompositionSet;
    }
*/

}
