package de.rub.bi.inf.model;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCProduct;

/**
 * A concrete singular result produced by the OpenBimRL engine.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class ResultObject extends AbstractResultObject {

    private IIFCProduct product;
    // private ArrayList<CadObject> additionalGeometries = new ArrayList<>();

    public ResultObject() {
        //Do nothing
    }

    public ResultObject(IIFCProduct product) {
        if (product.getName() != null) {
            this.setName(product.getName());
        }
        this.setProduct(product);
    }

    public ResultObject(String customName, IIFCProduct product) {
        this.setName(customName);
        this.setProduct(product);
    }

    public IIFCProduct getProduct() {
        return product;
    }

    public void setProduct(IIFCProduct product) {
        this.product = product;
    }

/*    public void addAdditionalGeometry(CadObject cadObject) {
        this.additionalGeometries.add(cadObject);
    }

    public ArrayList<CadObject> getAdditionalGeometries() {
        return additionalGeometries;
    }*/

}
