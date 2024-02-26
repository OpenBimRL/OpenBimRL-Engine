package de.rub.bi.inf.model;

import de.rub.bi.inf.logger.RuleLogger;

import java.util.ArrayList;

/**
 * Defines a super-type for a group of rules of type {@link AbstractRuleDefinition}.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class RuleSet extends AbstractRuleDefinition {

    protected ArrayList<AbstractRuleDefinition> children = new ArrayList<>();

    @Override
    public void check(RuleLogger logger) {
        children.forEach(r -> r.check(logger));
    }

    public void addChild(AbstractRuleDefinition ruleDefinition) {
        this.children.add(ruleDefinition);
    }

    public ArrayList<AbstractRuleDefinition> getChildren() {
        return children;
    }

}
