package de.rub.bi.inf.openbimrl.functions.ifc;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Creates a polyline based on a list of Vector3D.
 *
 * @author Marcel Stepien
 */
public class CreatePolyline extends AbstractFunction {

    // private final ArrayList<IIFCClass> memory = new ArrayList<>();

    public CreatePolyline(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
/*
        var objects = getInputAsCollection(0);

        //reset Memory
        this.memReset();

        final var points = new ArrayList<IIFCCartesianPoint>();

        for (Object obj : objects) {

            Vector3D point = null;

            if (obj instanceof Vector3D) {
                point = (Vector3D) obj;
            } else {
                continue;
            }

            final var measures = new ArrayList<IIFCLengthMeasure>(3);
            measures.add(adapter.getIFC4().create(IIFCLengthMeasure.class, point.getX()));
            measures.add(adapter.getIFC4().create(IIFCLengthMeasure.class, point.getY()));
            measures.add(adapter.getIFC4().create(IIFCLengthMeasure.class, point.getZ()));

            IIFCCartesianPoint cPoint = adapter.getIFC4().create(IIFCCartesianPoint.class, measures);
            //ifcModel.addObject(cPoint);
            memory.add(cPoint);

            points.add(cPoint);
        }

        IIFCPolyline polyline = Adapter.getInstance().getIFC4().create(IIFCPolyline.class, points);
        //ifcModel.addObject(polyline);
        memory.add(polyline);

        setResult(0, polyline);*/

    }

/*    private void memReset() {
        for (var obj : memory) {
            //ifcModel.removeObject(obj);
        }
        memory.clear();
    }*/

}
