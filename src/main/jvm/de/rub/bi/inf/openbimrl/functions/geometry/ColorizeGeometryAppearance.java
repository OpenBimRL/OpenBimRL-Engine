package de.rub.bi.inf.openbimrl.functions.geometry;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

/**
 * Colorizes the Appearance of a MultiAppearanceShape3D to a specific color.
 *
 * @author Marcel Stepien
 */
public class ColorizeGeometryAppearance extends AbstractFunction {

    public ColorizeGeometryAppearance(NodeProxy nodeProxy) {
        super(nodeProxy);
    }

    @Override
    public void execute() {

        final var objects = getInputAsCollection(0);
        if (objects.isEmpty()) return;

        Object input1 = getInput(1);
        Object input2 = getInput(2);
        Object input3 = getInput(3);

/*        for (Object o : objects) {

            if (o instanceof MultiAppearanceShape3D shape) {

                shape.getAppearance().setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

                shape.getAppearance().setColoringAttributes(
                        new ColoringAttributes(
                                Float.parseFloat(input1.toString()),
                                Float.parseFloat(input2.toString()),
                                Float.parseFloat(input3.toString()),
                                1
                        )
                );

            }

        }*/

        setResult(0, objects.iterator().next());
    }

}
