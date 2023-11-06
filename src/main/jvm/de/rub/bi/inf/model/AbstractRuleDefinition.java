package de.rub.bi.inf.model;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;

import java.util.ArrayList;

/**
 * The abstract super-type of an {@link SimpleRule} or {@link RuleSet}.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public abstract class AbstractRuleDefinition {

    protected String name;
    protected String value;
    protected String comment;
    protected CheckedStatus checkedStatus = CheckedStatus.UNCHECKED;
    protected ArrayList<ResultObjectGroup> resultObjects = new ArrayList<>();
    protected ArrayList<String> checkingProtocol = new ArrayList<>();
    protected IIFCModel modelNode;

    public AbstractRuleDefinition() {
        this.name = "Unknown Rule";
        this.value = "";
        this.comment = "";
    }

    public IIFCModel getModelNode() {
        return modelNode;
    }

    public String getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Template method for defining a function or rule for execution.
     */
    public abstract void check(IIFCModel ifcModel);

    public String getCheckingProtocol() {
        final var text = new StringBuilder();
        for (String s : checkingProtocol)
            text.append(s).append("\n");

        return text.toString();
    }

    public ArrayList<ResultObjectGroup> getResultObjects() {
        return resultObjects;
    }

    public CheckedStatus getCheckedStatus() {
        return checkedStatus;
    }

    public enum CheckedStatus {
        SUCCESS, FAILED, WARNING, UNCHECKED
    }

}
