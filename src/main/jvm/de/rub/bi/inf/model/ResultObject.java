package de.rub.bi.inf.model;

import de.rub.bi.inf.nativelib.IfcPointer;

/**
 * A concrete singular result produced by the OpenBimRL engine.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class ResultObject extends AbstractResultObject {

    private IfcPointer product;

    public ResultObject() {
        //Do nothing
    }

    public ResultObject(IfcPointer product) {

        this.setName(product.getType());
        this.setProduct(product);
    }

    public ResultObject(String customName, IfcPointer product) {
        this.setName(customName);
        this.setProduct(product);
    }

    public IfcPointer getProduct() {
        return product;
    }

    public void setProduct(IfcPointer product) {
        this.product = product;
    }
}
