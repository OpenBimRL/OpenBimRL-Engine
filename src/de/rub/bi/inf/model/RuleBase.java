package de.rub.bi.inf.model;

import java.util.ArrayList;

/**
 * This class functions as a container for holding loaded rulesets and applies listener. 
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public class RuleBase {

	private ArrayList<AbstractRuleDefinition> rules = new ArrayList<>();
	private ArrayList<RuleBaseChangedListener> changedListeners = new ArrayList<>();
	
	private static RuleBase instance = new RuleBase();
	
	/**
	 * Accessible as Singleton.
	 * 
	 * @return RuleBase
	 */
	public static RuleBase getInstance() {
		if(instance==null)
			instance = new RuleBase();
		return instance;
	}
	
	private RuleBase() { }
	
	public void registerChangedListener(RuleBaseChangedListener changedListener) {
		changedListeners.add(changedListener);
	}
	
	public void addRule(AbstractRuleDefinition rule) {
		rules.add(rule);
		changedListeners.forEach(cl->cl.onRulesAdded(rule));
	}
	
	public AbstractRuleDefinition[] getRules() {
		return rules.toArray(new AbstractRuleDefinition[rules.size()]);
	}
	
	public void resetAllRules() {
		rules = new ArrayList<AbstractRuleDefinition>();
	}
}
