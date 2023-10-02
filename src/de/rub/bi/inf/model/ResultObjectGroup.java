package de.rub.bi.inf.model;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCProduct;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A group containing results of type {@link AbstractResultObject} produced by the OpenBimRL engine.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public class ResultObjectGroup extends AbstractResultObject{
	
	private Object identity = null;
	private final ArrayList<AbstractResultObject> children = new ArrayList<>();
	
	public ResultObjectGroup() {
		//Do nothing
	}
	
	public ResultObjectGroup(Object identity) {
		this.identity = identity;
		setName(identity.toString());
	}
	
	public ResultObjectGroup(Object identity, String name) {
		this.identity = identity;
		setName(name);
	}
	
	public ResultObjectGroup(String groupName, Collection<?> products) {
		products.forEach(r -> {
			ResultObject resultObject = new ResultObject((IIFCProduct) r);
			children.add(resultObject);
		});
		setName(groupName);
	}
	
	public ArrayList<AbstractResultObject> getChildren() {
		return children;
	}
	
	public Object getIdentity() {
		return identity;
	}
}
