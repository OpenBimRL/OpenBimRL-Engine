package de.rub.bi.inf.model;

import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;

import java.util.ArrayList;

/**
 * Defines a super-type for a group of rules of type {@link AbstractRuleDefinition}.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class RuleSet extends AbstractRuleDefinition {

    protected ArrayList<AbstractRuleDefinition> children = new ArrayList<>();

    @Override
    public void check(IIFCModel ifcModel) {
        children.forEach(r -> r.check(ifcModel));
    }

    public void addChild(AbstractRuleDefinition ruleDefinition) {
        this.children.add(ruleDefinition);
    }

    public ArrayList<AbstractRuleDefinition> getChildren() {
        return children;
    }

}
