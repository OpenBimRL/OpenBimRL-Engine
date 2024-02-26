package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import java.awt.*;

/**
 * Loads an representation item´newly created into the model context, and therfore into the viewer.
 *
 * @author Marcel Stepien
 */
public class LoadRepresentationItemGeometry extends AbstractFunction {

/*    private CadObjectJ3D branchgroup = null;
    private ArrayList<CadObjectJ3D> memory = new ArrayList<CadObjectJ3D>();*/

    public LoadRepresentationItemGeometry(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    public static Material createMatrial(Color color) {
        Material material = new Material();
        material.setDiffuseColor(new Color3f(color));
        material.setAmbientColor(new Color3f(color));
        return material;
    }

    @Override
    public void execute() {

/*
        Object input0 = getInput(0);

        if (input0 == null)
            return;

        Collection<?> elements = null;
        if (input0 instanceof Collection<?>) {
            elements = (Collection<?>) input0;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            newList.add(input0);
            elements = newList;
        }

        //reset Memory
        this.memReset(ifcModel);


        for (Object element : elements) {
            final var classInterface = (IIFCClass) element;

            if (classInterface instanceof IIFCShapeRepresentation shapeRepresentation) {

                ArrayList<CadObjectJ3D> fluchtwegGeometries = generateAnnotationCurve3D(shapeRepresentation);

                //Add to viewer and shows geometry
                for (CadObjectJ3D geom : fluchtwegGeometries) {
                    //node.getCadObjectModel().addCadObject(geom);
                    branchgroup.addChild(geom);
                    memory.add(geom);
                }

            }

        }

        if (memory.size() == 1) {
            setResult(0, memory.get(0));
        } else {
            setResult(0, memory);
        }
*/

    }

/*    public ArrayList<CadObjectJ3D> generateAnnotationCurve3D(IIFCShapeRepresentation shape) {
        ArrayList<CadObjectJ3D> results = new ArrayList<CadObjectJ3D>();
        String identifier = shape.getRepresentationIdentifier().getDecodedValue();
        String type = shape.getRepresentationType().getDecodedValue();
        if (identifier.equals("Annotation") && type.equals("Curve3D")) {
            //Start to generate geometry

            for (IfcRepresentationItem item : shape.getItems()) {
                if (item instanceof IfcPolyline) {
                    Appearance appearance = createAppearance(Color.ORANGE);

                    BranchGroup group = new BranchGroup();

                    int vertexCounts[] = {((IfcPolyline) item).getPoints().size()};
                    LineStripArray lineArr = new LineStripArray(((IfcPolyline) item).getPoints().size(), GeometryArray.COORDINATES, vertexCounts);
                    Point3d[] points = new Point3d[((IfcPolyline) item).getPoints().size()];

                    int index = 0;
                    for (IfcCartesianPoint point : ((IfcPolyline) item).getPoints()) {

                        double xPos = point.getCoordinates().get(0).getValue();
                        double yPos = point.getCoordinates().get(1).getValue();
                        double zPos = point.getCoordinates().get(2).getValue();

                        Sphere pointShape = new Sphere(0.1f, appearance);

                        TransformGroup tg = new TransformGroup();
                        Transform3D transform = new Transform3D();
                        Vector3f vector = new Vector3f(
                                (float) xPos,
                                (float) yPos,
                                (float) zPos
                        );
                        transform.setTranslation(vector);
                        tg.setTransform(transform);
                        tg.addChild(pointShape);

                        points[index] = new Point3d(
                                xPos,
                                yPos,
                                zPos
                        );
                        index++;

                        group.addChild(tg);
                    }

                    lineArr.setCoordinates(0, points);
                    Shape3D lineShape = new Shape3D(lineArr, appearance);
                    group.addChild(lineShape);

                    CadObjectJ3D objectJ3D = new CadObjectJ3D();
                    objectJ3D.addChild(group);

                    results.add(objectJ3D);
                }
            }
        }
        return results;
    }

    public Appearance createAppearance(Color color) {
        Appearance appearance = new Appearance();
        appearance.setMaterial(createMatrial(color));
        appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);

        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setLinePattern(LineAttributes.PATTERN_SOLID);
        lineAttributes.setLineWidth(5.0f);
        lineAttributes.setLineAntialiasingEnable(true);
        appearance.setLineAttributes(lineAttributes);

        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        appearance.setPolygonAttributes(polygonAttributes);

        ColoringAttributes coloringAttributes = new ColoringAttributes();
        coloringAttributes.setColor(new Color3f(color));
        appearance.setColoringAttributes(coloringAttributes);

        return appearance;
    }

    private void memReset(IIFCModel ifcModel) {
        if (branchgroup == null) {
            branchgroup = ((CadObjectJ3D) ifcModel.getCadObjectModel().getRootBranchGroup());
        }

        for (CadObjectJ3D obj : memory) {
            branchgroup.removeChild(obj);
        }
        memory = new ArrayList<CadObjectJ3D>();
    }*/
}
