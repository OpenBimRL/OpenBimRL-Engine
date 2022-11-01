package model;

import java.util.ArrayList;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

/**
 * Defines an super-type for a group of rules of type {@link AbstractRuleDefinition}.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public class RuleSet extends AbstractRuleDefinition {
	
	protected ArrayList<AbstractRuleDefinition> children = new ArrayList<AbstractRuleDefinition>();
	
	@Override
	public void check(ApplicationModelNode ifcModel) {
		children.forEach(r -> r.check(ifcModel));
	}
	
	public void addChild(AbstractRuleDefinition ruleDefinition) {
		this.children.add(ruleDefinition);
	}
	
	public ArrayList<AbstractRuleDefinition> getChildren() {
		return children;
	}
	
}
