package de.rub.bi.inf.model;

/**
 * A listener used by {@link RuleBase}.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public interface RuleBaseChangedListener {
	
	public void onRulesAdded(AbstractRuleDefinition addedRule);

}
