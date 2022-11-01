package model;

import java.util.ArrayList;

import com.apstex.gui.core.model.cadobjectmodel.CadObject;
import com.apstex.ifctoolbox.ifc.IfcProduct;

/**
 * A concrete singular result produced by the OpenBimRL engine.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public class ResultObject extends AbstractResultObject {
	
	private IfcProduct product;
	private ArrayList<CadObject> additionalGeometries = new ArrayList<>();
	
	public ResultObject() {
		//Do nothing
	}
	
	public ResultObject(IfcProduct product) {
		if(product.getName() != null) {			
			this.setName(product.getName().getDecodedValue());
		}
		this.setProduct(product);
	}
	
	public ResultObject(String customName, IfcProduct product) {
		this.setName(customName);
		this.setProduct(product);
	}
	
	public IfcProduct getProduct() {
		return product;
	}
	
	public void setProduct(IfcProduct product) {
		this.product = product;
	}
	
	public void addAdditionalGeometry(CadObject cadObject) {
		this.additionalGeometries.add(cadObject);
	}
	
	public ArrayList<CadObject> getAdditionalGeometries() {
		return additionalGeometries;
	}

}
