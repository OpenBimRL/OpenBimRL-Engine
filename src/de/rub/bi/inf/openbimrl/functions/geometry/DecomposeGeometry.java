package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import javax.media.j3d.GeometryArray;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import java.util.*;

/**
 * Decomposes a GeometrieArray into its elementary parts.
 *
 * @author Marcel Stepien
 */
public class DecomposeGeometry extends AbstractFunction {

    public DecomposeGeometry(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        Object input0 = getInput(0);

        if (input0 == null)
            return;

        Collection<?> objects = null;
        if (input0 instanceof Collection<?>) {
            objects = (Collection<?>) input0;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            newList.add(input0);
            objects = newList;
        }


        HashMap<String, List> decomposition = new HashMap<String, List>();

        decomposition = new HashMap<>();
        decomposition.put("coordinates", new ArrayList<>());
        decomposition.put("indices", new ArrayList<>());
        decomposition.put("stripCounts", new ArrayList<>());
        decomposition.put("contourCounts", new ArrayList<>());
        decomposition.put("normals", new ArrayList<>());
        decomposition.put("centroid", new ArrayList<>());
        decomposition.put("profile", new ArrayList<>());

        decompose(objects, decomposition);


        if (decomposition.get("coordinates").size() == 1) {
            setResult(0, decomposition.get("coordinates").get(0));
        } else {
            setResult(0, decomposition.get("coordinates"));
        }

        if (decomposition.get("indices").size() == 1) {
            setResult(1, decomposition.get("indices").get(0));
        } else {
            setResult(1, decomposition.get("indices"));
        }

        if (decomposition.get("stripCounts").size() == 1) {
            setResult(2, decomposition.get("stripCounts").get(0));
        } else {
            setResult(2, decomposition.get("stripCounts"));
        }

        if (decomposition.get("contourCounts").size() == 1) {
            setResult(3, decomposition.get("contourCounts").get(0));
        } else {
            setResult(3, decomposition.get("contourCounts"));
        }

        if (decomposition.get("normals").size() == 1) {
            setResult(4, decomposition.get("normals").get(0));
        } else {
            setResult(4, decomposition.get("normals"));
        }

        if (decomposition.get("centroid").size() == 1) {
            setResult(5, decomposition.get("centroid").get(0));
        } else {
            setResult(5, decomposition.get("centroid"));
        }

        if (decomposition.get("profile").size() == 1) {
            setResult(6, decomposition.get("profile").get(0));
        } else {
            setResult(6, decomposition.get("profile"));
        }

    }

    private void decompose(Collection objects, HashMap<String, List> decomposition) {

        for (Object o : objects) {

            if (o instanceof GeometryArray) {

                GeometryInfo info = new GeometryInfo((GeometryArray) o);

                ArrayList<Point3f> hs = convertToPolygon(info);
                decomposition.get("profile").add(hs);
                decomposition.get("coordinates").add(info.getCoordinates());
                decomposition.get("indices").add(info.getCoordinateIndices());
                decomposition.get("stripCounts").add(info.getStripCounts());
                decomposition.get("contourCounts").add(info.getContourCounts());
                decomposition.get("normals").add(info.getNormals());
                decomposition.get("centroid").add(findCenter(info.getCoordinates()));

            } else if (o instanceof Collection) {
                decompose(objects, decomposition);
            } else {
                //Handle consistent size of decomposed elements in correlation to input
                System.err.println("DecomposeGeometry can only handle non-grouped GeometryArrays.");
            }

        }

    }

    private Point3d findCenter(Point3f[] points) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        for (Point3f p : points) {
            x += p.x;
            y += p.y;
            z += p.z;
        }

        return new Point3d(
                x / points.length,
                y / points.length,
                z / points.length
        );
    }


    private ArrayList<Point3f> convertToPolygon(GeometryInfo info) {
        HashMap<Double, HashSet<Point3f>> cornerMap = new HashMap<Double, HashSet<Point3f>>();

        double lowest = 0.0;

        for (int i = 0; i < info.getCoordinateIndices().length; i++) {
            Point3f p = info.getCoordinates()[
                    info.getCoordinateIndices()[i]
                    ];

            double height = p.z;
            if (i == 0 || lowest > height) {
                lowest = height;
            }

            if (cornerMap.get(height) == null) {
                cornerMap.put(height, new HashSet<Point3f>());
            }
            cornerMap.get(height).add(p);

        }

        ArrayList<Point3f> cornerList = new ArrayList<Point3f>();
        cornerList.addAll(cornerMap.get(lowest));

        return cornerList;
    }


}
