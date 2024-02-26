package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.line.Lines3D;
import org.apache.commons.geometry.euclidean.threed.line.Segment3D;
import org.apache.commons.numbers.core.Precision;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Connects nodes of an graph by inserting edges of a certain stepsize, provided the nodes are already known. Priorisation on reference nodes.
 *
 * @author Marcel Stepien
 */
public class CreatePointGraphEdgesByReference extends AbstractFunction {

    public CreatePointGraphEdgesByReference(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        if (getInput(0) == null)
            return;

        Collection<?> nodes = getInputAsCollection(0);

        if (getInput(1) == null)
            return;

        Collection<?> nodesReference = getInputAsCollection(1);

        String input2 = getInput(2);
        if (input2 == null)
            return;

        final var stepSize = Double.parseDouble(input2);

        final var edges = createGraphEdge(nodes, nodesReference, stepSize);

        setResult(0, edges);
    }

    private ArrayList<Object> createGraphEdge(Collection<?> nodes, Collection<?> nodesReference, double stepSize) {
        final var linearEdgeList = new ArrayList<>();

        double increasedStepSize = stepSize; //* Math.sqrt(2) + 0.001;

        for (Object sNode : nodes) {

            if (sNode instanceof Collection<?> sNodeCollection) {
                linearEdgeList.add(
                        createGraphEdge(sNodeCollection, nodesReference, stepSize)
                );
            }

            // no clue what that actually does ~ Florian

            if (sNode instanceof Vector3D sVector3D) {

                for (Object eNode : nodesReference) {
                    if (eNode instanceof Vector3D eVector3D) {

                        if (!sNode.equals(eNode)) {
                            double distance = sVector3D.distance(eVector3D);
                            if (distance <= increasedStepSize && distance > 0.0) {

                                Segment3D edge = Lines3D.segmentFromPoints(
                                        sVector3D,
                                        eVector3D,
                                        Precision.doubleEquivalenceOfEpsilon(1e-6)
                                );

                                Segment3D edgeInverse = Lines3D.segmentFromPoints(
                                        eVector3D,
                                        sVector3D,
                                        Precision.doubleEquivalenceOfEpsilon(1e-6)
                                );

                                linearEdgeList.add(edge);
                                linearEdgeList.add(edgeInverse);

                            }
                        }

                    }
                }

            }
        }

        // ^ this is cursed TODO FIXME

        return linearEdgeList;
    }

}
