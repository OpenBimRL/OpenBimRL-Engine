package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Lines3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;
import org.apache.commons.numbers.core.Precision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creates a raster of points in an area enclosed in an RegionalBSPTree3D area, producing nodes and edges.
 *
 * @author Marcel Stepien
 */
public class CreatePointGraph extends AbstractFunction {

    public CreatePointGraph(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {
        if (getInput(0) == null)
            return;

        Collection<?> bspTrees = getInputAsCollection(0);

        String input1 = getInput(1);
        if (input1 == null)
            return;

        double stepSize = Double.parseDouble(input1);

        ArrayList<List<Segment3D>> listOfEdges = new ArrayList<>();

        ArrayList<List<Vector3D>> listOfNodes = new ArrayList<>();

        for (Object o : bspTrees) {

            if (o instanceof RegionBSPTree3D bspTree3D) {

                ArrayList<Vector3D> nodes = createGraphNodes(bspTree3D, stepSize);
                listOfNodes.add(nodes);

                ArrayList<Segment3D> edges = createGraphEdge(nodes, stepSize);
                listOfEdges.add(edges);
            }

        }

        if (listOfNodes.size() == 1) {
            setResult(0, listOfNodes.get(0));
        } else {
            setResult(0, listOfNodes);
        }

        if (listOfEdges.size() == 1) {
            setResult(1, listOfEdges.get(0));
        } else {
            setResult(1, listOfEdges);
        }
    }

    private ArrayList<Vector3D> createGraphNodes(RegionBSPTree3D bspTree, double stepSize) {
        Vector3D cA = bspTree.getBounds().getMin();
        Vector3D cB = bspTree.getBounds().getMax();
        Vector3D center = bspTree.getBounds().getCentroid();

        double offsetX = ((cB.getX() - cA.getX() - stepSize) % stepSize) / 2;
        double offsetY = ((cB.getY() - cA.getY() - stepSize) % stepSize) / 2;
        double height = center.getZ();

        ArrayList<Vector3D> linearPointList = new ArrayList<>();

        for (double x = cA.getX() + stepSize / 2 + offsetX; x < cB.getX() - stepSize / 2; x += stepSize) {
            for (double y = cA.getY() + stepSize / 2 + offsetY; y < cB.getY() - stepSize / 2; y += stepSize) {

                Vector3D gridPoint = Vector3D.of(x, y, height);

                if (bspTree.contains(gridPoint)) {
                    linearPointList.add(gridPoint);
                }

            }
        }

        return linearPointList;
    }

    private ArrayList<Segment3D> createGraphEdge(ArrayList<Vector3D> nodes, double stepSize) {
        ArrayList<Segment3D> linearEdgeList = new ArrayList<>();

        double increasedStepSize = stepSize * 1.5;

        for (Vector3D sNode : nodes) {
            for (Vector3D eNode : nodes) {

                if (sNode != eNode) {
                    if (sNode.distance(eNode) <= increasedStepSize) {

                        Segment3D edge = Lines3D.segmentFromPoints(sNode, eNode, Precision.doubleEquivalenceOfEpsilon(1e-9));
                        linearEdgeList.add(edge);

                    }
                }

            }
        }

        return linearEdgeList;
    }

}
