package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import javax.media.j3d.Bounds;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Checks if the bounds of geometic objects are intersecting.
 *
 * @author Marcel Stepien
 */
public class CheckIntersectionByBounds extends AbstractFunction {

    public CheckIntersectionByBounds(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute(IIFCModel ifcModel) {

        Object input0 = getInput(0);
        Object input1 = getInput(1);

        if (input0 == null || input1 == null)
            return;

        Collection<?> geometryGroupA = null;
        if (input0 instanceof Collection<?>) {
            geometryGroupA = (Collection<?>) input0;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            newList.add(input0);
            geometryGroupA = newList;
        }

        Collection<?> geometryGroupB = null;
        if (input1 instanceof Collection<?>) {
            geometryGroupB = (Collection<?>) input1;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            newList.add(input1);
            geometryGroupB = newList;
        }

        ArrayList<List<?>> resultValues = new ArrayList<List<?>>();
        for (Object og1 : geometryGroupA) {
            if (og1 instanceof ArrayList<?>) {
                for (Object o1 : (ArrayList<?>) og1) {
                    if (o1 instanceof GeometryArray) {

                        GeometryInfo geoA = new GeometryInfo((GeometryArray) o1);
                        geoA.recomputeIndices();

                        Bounds geoBoundsA = new Shape3D(geoA.getIndexedGeometryArray()).getBounds();

                        Point3d centerA = new Point3d();
                        geoBoundsA.getCenter(centerA);

                        double[] translateArrA = {0.0, 0.0, 0.0};
                        centerA.get(translateArrA);

                        Transform3D translateA = new Transform3D();
                        translateA.setTranslation(new Vector3d(translateArrA));
                        translateA.invert();

                        Transform3D scaleA = new Transform3D();
                        scaleA.setScale(1.0);

                        Transform3D translateBackA = new Transform3D();
                        translateBackA.setTranslation(new Vector3d(translateArrA));

                        geoBoundsA.transform(translateA);
                        geoBoundsA.transform(scaleA);
                        geoBoundsA.transform(translateBackA);

                        ArrayList<Boolean> mask = new ArrayList<Boolean>();

                        for (Object og2 : geometryGroupB) {
                            if (og2 instanceof ArrayList<?>) {

                                boolean flag = false;

                                for (Object o2 : (ArrayList<?>) og2) {
                                    if (o2 instanceof GeometryArray) {
                                        GeometryInfo geoB = new GeometryInfo((GeometryArray) o2);
                                        geoB.recomputeIndices();

                                        Bounds geoBoundsB = new Shape3D(geoB.getIndexedGeometryArray()).getBounds();

                                        Point3d center = new Point3d();
                                        geoBoundsB.getCenter(center);

                                        double[] translateArr = {0.0, 0.0, 0.0};
                                        center.get(translateArr);

                                        Transform3D translate = new Transform3D();
                                        translate.setTranslation(new Vector3d(translateArr));
                                        translate.invert();

                                        Transform3D scale = new Transform3D();
                                        scale.setScale(1.0);

                                        Transform3D translateBack = new Transform3D();
                                        translateBack.setTranslation(new Vector3d(translateArr));

                                        geoBoundsB.transform(translate);
                                        geoBoundsB.transform(scale);
                                        geoBoundsB.transform(translateBack);

                                        if (geoBoundsA.intersect(geoBoundsB)) {
                                            flag = true;
                                        }

                                    }

                                }

                                mask.add(flag);

                            }
                        }
                        resultValues.add(mask);
                    }


                }
            }
        }

        setResult(0, resultValues);
    }

}
