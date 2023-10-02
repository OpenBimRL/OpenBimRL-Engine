package de.rub.bi.inf.openbimrl.functions.ifc;

import java.util.ArrayList;
import java.util.List;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;

import javax.media.j3d.Geometry;

/**
 * Retrieves the geometry and groups it, based on the IFC element.
 * 
 * @author Marcel Stepien
 *
 */
public class GetGeometryByElement extends AbstractFunction {

	public GetGeometryByElement(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {

		final var objects = getInputAsCollection(0);
		if (objects.isEmpty()) return;


		final var resultValues = new ArrayList<List<Geometry>>();
		
		for(Object o : objects) {
			ClassInterface classInterface = (ClassInterface) o;
			CadObject cad = ifcModel.getCadObjectModel().getCadObject(classInterface);
			
			final var geoGroup = new ArrayList<Geometry>();
			for(SolidShape shape : cad.getSolidShapes()) {
				MultiAppearanceShape3D sShape = ((MultiAppearanceShape3D)shape);
				geoGroup.add(sShape.getGeometry());
			}
			resultValues.add(geoGroup);
			
		}
		
		setResult(0, resultValues);
	}

}
