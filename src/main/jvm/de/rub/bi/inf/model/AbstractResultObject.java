package de.rub.bi.inf.model;

/**
 * The abstract supertype of all results produced by the OpenBimRL engine.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public abstract class AbstractResultObject {

    private String name;
    private String info;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
