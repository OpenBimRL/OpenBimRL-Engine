package de.rub.bi.inf.openbimrl.functions.geometry;

import java.util.ArrayList;
import java.util.Collection;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.functions.AbstractFunction;
import org.apache.commons.geometry.euclidean.threed.Vector3D;

/**
 * Decomposes a Vector3D into its elementary parts.
 * 
 * @author Marcel Stepien
 *
 */
public class DecomposeVector3D extends AbstractFunction {
	
	public DecomposeVector3D(NodeProxy nodeProxy) {
		super(nodeProxy);
	}

	@Override
	public void execute(IIFCModel ifcModel) {
	
		Object input0 = getInput(0);
		
		if(input0 == null)
			return;
				
		Collection<?> vecs = null;
		if(input0 instanceof Collection<?>) {
			vecs = (Collection<?>) input0;
		}else {
			ArrayList<Object> newList = new ArrayList<>();
			newList.add(input0);
			vecs = newList;
		}
		
		ArrayList<String> xVecs = new ArrayList<String>();
		ArrayList<String> yVecs = new ArrayList<String>();
		ArrayList<String> zVecs = new ArrayList<String>();
		
		for(Object o : vecs) {
			if(o instanceof Vector3D) {
				xVecs.add(Double.toString(((Vector3D)o).getX()));
				yVecs.add(Double.toString(((Vector3D)o).getY()));
				zVecs.add(Double.toString(((Vector3D)o).getZ()));
			}
		}
		
		if(xVecs.size() == 1) {			
			setResult(0, xVecs.get(0));
		}else {
			setResult(0, xVecs);	
		}
		
		if(yVecs.size() == 1) {			
			setResult(1, yVecs.get(0));
		}else {
			setResult(1, yVecs);	
		}
		
		if(zVecs.size() == 1) {			
			setResult(2, zVecs.get(0));
		}else {
			setResult(2, zVecs);	
		}
	}

}
