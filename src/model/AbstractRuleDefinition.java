package model;

import java.util.ArrayList;

import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;

/**
 * The abstract super-type of an {@link SimpleRule} or {@link RuleSet}.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public abstract class AbstractRuleDefinition {
	
	protected String name;
	protected String value;
	protected String comment;
	protected CheckedStatus checkedStatus = CheckedStatus.UNCHECKED;
	protected ArrayList<ResultObjectGroup> resultObjects = new ArrayList<>();
	protected ArrayList<String> checkingProtocol = new ArrayList<>();
	protected ApplicationModelNode modelNode;
	
	public ApplicationModelNode getModelNode() {
		return modelNode;
	}
	
	public AbstractRuleDefinition() {
		this.name = "Unknown Rule";
		this.value = "";
		this.comment = "";
	}

	public String getValue() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}

	public String getComment() {
		return this.comment;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Template method for defining a funktion or rule for execution.
	 *  
	 * @param ifcModel
	 */
	public abstract void check(ApplicationModelNode ifcModel);
	
	public String getCheckingProtocol() {
		String text = "";
		for (String s : checkingProtocol) {
			text+=s+"\n";
		}
		return text;
	}
	
	public ArrayList<ResultObjectGroup> getResultObjects() {
		return resultObjects;
	}
	
	public CheckedStatus getCheckedStatus() {
		return checkedStatus;
	}
	
	public enum CheckedStatus{
		SUCCESS, FAILED, WARNING, UNCHECKED; 
	}

}
