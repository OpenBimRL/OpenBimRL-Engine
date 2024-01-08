package de.rub.bi.inf.openbimrl;

import de.rub.bi.inf.logger.RuleLogger;
import de.rub.bi.inf.model.ResultObject;
import de.rub.bi.inf.model.ResultObjectGroup;
import de.rub.bi.inf.model.SimpleRule;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCProduct;
import de.rub.bi.inf.openbimrl.helper.FilterInterpreter;

import java.util.*;


/**
 * Defines a terminal single rule that Produces the {@link ResultObjectGroup}. This class
 * is being used internaly by {@link OpenRuleR}.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class OpenSubRule extends SimpleRule {

    private ModelSubCheckType modelSubCheck = null;
    private Map<String, Object> ruleIDtoValueMap = null;
    private ResultObjectGroup successes = null;
    private ResultObjectGroup failures = null;


    public OpenSubRule(ModelSubCheckType modelSubCheck, Map<String, Object> ruleIDtoValueMap) {
        this.name = modelSubCheck.getName();
        this.modelSubCheck = modelSubCheck;
        this.ruleIDtoValueMap = ruleIDtoValueMap;
    }


    @Override
    public void check(IIFCModel ifcModel, RuleLogger logger) {
        //Reset the check
        this.checkedStatus = CheckedStatus.UNCHECKED;
        this.checkingProtocol = new ArrayList<String>();

        this.modelNode = ifcModel;

        if (ifcModel == null) {
            return;
        }

        successes = new ResultObjectGroup("ResultSet: Successfully Checked");
        failures = new ResultObjectGroup("ResultSet: Failed Checked");


        //this.checkingProtocol.add("=============Subcheck Protocol===============");
        try {

            System.out.println("Performing Subcheck: " + modelSubCheck.getName());

            if (isApplicable(modelSubCheck)) {
                boolean result = interpret(modelSubCheck.getRules());
                if (result) {
                    this.checkedStatus = CheckedStatus.SUCCESS;
                    this.checkingProtocol.add("This rule executed successfully.");
                } else {
                    this.checkedStatus = CheckedStatus.FAILED;
                    this.checkingProtocol.add("This rule executed unsuccessfully.");
                }

            } else {
                this.checkingProtocol.add("This subcheck is not applicable for the model.");
                this.checkedStatus = CheckedStatus.WARNING;
            }

        } catch (Exception e2) {
            //Set status to Failure if a error accure
            this.checkedStatus = CheckedStatus.FAILED;
            this.checkingProtocol.add("A error occured. Please revise the code");

            e2.printStackTrace();
        }


        if (!successes.getChildren().isEmpty()) {
            resultObjects.add(successes);
        }

        if (!failures.getChildren().isEmpty()) {
            resultObjects.add(failures);
        }

        //this.checkingProtocol.add("======================================");

    }

    private boolean isApplicable(ModelSubCheckType subCheck) {

        if (subCheck.getApplicability() == null) {
            return true; //if no applicability is set, allow all
        }

        RulesType rules = subCheck.getApplicability().getRules();

        return interpretRules(rules);
    }

    private boolean interpret(Object rulesOrFilterOrRule) {
        if (rulesOrFilterOrRule instanceof RuleType) {
            return interpretRule((RuleType) rulesOrFilterOrRule);
        }

        if (rulesOrFilterOrRule instanceof RulesType) {
            return interpretRules((RulesType) rulesOrFilterOrRule);
        }

        return false;
    }

    private boolean interpretRules(RulesType rules) {
        String operator = rules.getOperator();

        //evaluate children
        ArrayList<Boolean> results = new ArrayList<>();
        for (Object ruleOrFilterOrRules : rules.getRuleOrRules()) {
            boolean result = interpret(ruleOrFilterOrRules);
            results.add(result);
        }

        //evaluate all;
        switch (operator) {
            case "and":
                return results.stream().allMatch(e -> e);
            case "or":
                return results.stream().anyMatch(e -> e);
            case "xor": // Marcel: writes t'odo. Florian: well this doesn't look that bad. Also Florian: WAIT A SEC!
                return results.stream().anyMatch(e -> e); //TODO: Must be implemented properly
            default:
                break;
        }

        return false;
    }

    private boolean interpretRule(RuleType rule) {

        if (rule.getQuantifier() == null) {

            return handleQuantifierDefault(rule);

        } else {

            //Option to add a filter mask if quantifiers are provided

            List<?> mask = rule.getFilter() != null ?
                    FilterInterpreter.interpret(rule.getFilter(), ruleIDtoValueMap) : null;


            //handle with quantifiers
            if (rule.getQuantifier().equals("notexists")) {
                return !handleQuantifierExists(rule, mask);
            } else if (rule.getQuantifier().equals("exists")) {
                return handleQuantifierExists(rule, mask);
            } else if (rule.getQuantifier().equals("all")) {
                return handleQuantifierAll(rule, mask);
            }
        }

        return false;

    }

    private boolean handleQuantifierDefault(RuleType rule) {

        Object ruleOperand1 = ruleIDtoValueMap.get(rule.getOperand1());
        if (ruleOperand1 == null) {
            ruleOperand1 = rule.getOperand1();
        }

        Object ruleOperand2 = ruleIDtoValueMap.get(rule.getOperand2());
        if (ruleOperand2 == null) {
            ruleOperand2 = rule.getOperand2();
        }
		
		
/*		if (ruleOperand1 instanceof BOOLEAN) {
			Boolean operand1 = Boolean.parseBoolean(ruleOperand1.toString());
			Boolean operand2 = Boolean.parseBoolean(ruleOperand2.toString());
			return compare(operand1, operand2, rule.getOperator());
		}*/
        if (ruleOperand1 instanceof Boolean) {
            Boolean operand2 = Boolean.parseBoolean(ruleOperand2.toString());
            return compare(ruleOperand1, operand2, rule.getOperator());
        } else if (ruleOperand1 instanceof Double) {
            Double operand2 = Double.parseDouble(ruleOperand2.toString());
            return compare(ruleOperand1, operand2, rule.getOperator());
        } else if (ruleOperand1 instanceof Integer) {
            Integer operand2 = Integer.parseInt(ruleOperand2.toString());
            return compare(ruleOperand1, operand2, rule.getOperator());
        } else {
            //Default Case
            return compare(
                    ruleOperand1.toString(),
                    ruleOperand2.toString(),
                    rule.getOperator()
            );
        }
    }

    private boolean handleQuantifierExists(RuleType rule, List<?> mask) {

        boolean exists = false;
        ArrayList<Boolean> newMask = new ArrayList<Boolean>();

        //Handle as Map
        if (ruleIDtoValueMap.get(rule.getOperand1()) instanceof Map<?, ?> operands1) {

            if (operands1 != null && mask != null) {
                if (operands1.keySet().size() != mask.size()) {
                    this.checkingProtocol.add("List size of the filter do not match on for: " + rule.getOperand1());
                    return false;
                }
            }

            int index = -1;
            //for(Object n0 : operands1.keySet()) {
            for (Object n0 : operands1.keySet()) {

                Object o1 = operands1.get(n0);

                index++;
                if (mask != null) {
                    Object maskObj = mask.get(index);

                    if (maskObj instanceof Boolean) {
                        if (!(Boolean) maskObj) {
                            continue;
                        }
                    }
                }

                boolean comp = false;
                if (o1 instanceof ArrayList<?> list) {
                    //for(Object n1 : ((ArrayList<?>)o)) {
                    for (int n1Index = 0; n1Index < list.size(); n1Index++) {
                        Object n1 = list.get(n1Index);

                        Object operator2Value = rule.getOperand2();
                        if (ruleIDtoValueMap.get(operator2Value) != null) {
                            Map<?, ?> operands2 = (Map<?, ?>) ruleIDtoValueMap.get(operator2Value);
                            Object o2 = operands2.get(n0);
                            Object n2 = ((ArrayList<?>) o2).get(n1Index);
                            operator2Value = n2;
                        }

                        comp = comp || compare(n1, operator2Value, rule.getOperator());

                        if (comp && (n1 instanceof IIFCProduct)) {
                            successes.getChildren().add(new ResultObject((IIFCProduct) n0));
                        }

                        if (!comp && (n1 instanceof IIFCProduct)) {
                            failures.getChildren().add(new ResultObject((IIFCProduct) n0));
                        }

                    }
                }

                //Add to new Mask for ruleID save
                newMask.add(comp);
                if (comp) {
                    exists = true;
                }
            }

        } else if (ruleIDtoValueMap.get(rule.getOperand1()) instanceof AbstractCollection<?> operands) { //Handle as Collection

            if (operands != null && mask != null) {
                if (operands.size() != mask.size()) {
                    this.checkingProtocol.add("List size of the filter do not match on for: " + rule.getOperand1());
                    return false;
                }
            }

            Object[] operandArr = operands.toArray();
            for (int index = 0; index < operandArr.length; index++) {

                Object nO = operandArr[index];

                if (mask != null) {
                    Object maskObj = mask.get(index);

                    if (maskObj instanceof Boolean) {
                        if (!(Boolean) maskObj) {
                            continue;
                        }
                    }

                }

                if (nO == null) {
                    nO = "null";
                }

                Object operator2Value = rule.getOperand2();
                if (ruleIDtoValueMap.get(operator2Value) != null) {
                    AbstractCollection<?> operands2 = (AbstractCollection<?>) ruleIDtoValueMap.get(operator2Value);
                    Object[] operandArr2 = operands2.toArray();
                    Object n2 = operandArr2[index];
                    operator2Value = n2;
                }

                boolean comp = compare(nO, operator2Value, rule.getOperator());

                if (comp && (nO instanceof IIFCProduct product)) {
                    successes.getChildren().add(new ResultObject(product));
                }

                if (!comp && (nO instanceof IIFCProduct product)) {
                    failures.getChildren().add(new ResultObject(product));
                }

                //Add to new Mask for ruleID save
                newMask.add(comp);
                if (comp)
                    exists = true;
            }
        }

        String ruleID = rule.getLabel();
        if (ruleID != null) {
            ruleIDtoValueMap.put(ruleID, newMask);
        }

        return exists;
    }

    private boolean handleQuantifierAll(RuleType rule, List<?> mask) {

        boolean appliesForAll = true;
        final var newMask = new ArrayList<Boolean>();

        //Handle as Map
        if (ruleIDtoValueMap.get(rule.getOperand1()) instanceof Map<?, ?> operands1) {

            if (operands1 != null && mask != null) {
                if (operands1.keySet().size() != mask.size()) {
                    this.checkingProtocol.add("List size of the filter do not match on for: " + rule.getOperand1());
                    return false;
                }
            }

            int index = -1;
            for (Object n0 : operands1.keySet()) {
                Object o1 = operands1.get(n0);

                index++;
                if (mask != null) {
                    Object maskObj = mask.get(index);

                    if (maskObj instanceof Boolean) {
                        if (!(Boolean) maskObj) {
                            continue;
                        }
                    }
                }

                boolean comp = false;
                if (o1 instanceof ArrayList<?>) {
                    for (int n1Index = 0; n1Index < ((ArrayList<?>) o1).size(); n1Index++) {
                        Object n1 = ((ArrayList<?>) o1).get(n1Index);

                        Object operator2Value = rule.getOperand2();
                        if (ruleIDtoValueMap.get(operator2Value) != null) {
                            Map<?, ?> operands2 = (Map<?, ?>) ruleIDtoValueMap.get(operator2Value);
                            Object o2 = operands2.get(n0);
                            Object n2 = ((ArrayList<?>) o2).get(n1Index);
                            operator2Value = n2;
                        }

                        comp = comp || compare(n1, operator2Value, rule.getOperator());

                        //System.out.println(nO + " | " + rule.getOperand2() + " | " + rule.getOperator());

                        if (comp && (n1 instanceof IIFCProduct)) {
                            successes.getChildren().add(new ResultObject((IIFCProduct) n0));
                        }

                        if (!comp && (n1 instanceof IIFCProduct)) {
                            failures.getChildren().add(new ResultObject((IIFCProduct) n0));
                        }

                    }
                }

                //Add to new Mask for ruleID save
                newMask.add(comp);
                if (!comp) {
                    appliesForAll = false;
                }
            }

        } else if (ruleIDtoValueMap.get(rule.getOperand1()) instanceof AbstractCollection<?> operands) { //Handle as Collection

            if (operands != null && mask != null) {
                if (operands.size() != mask.size()) {
                    this.checkingProtocol.add("List size of the filter do not match on for: " + rule.getOperand1());
                    return false;
                }
            }

            int index = -1;
            for (Object nO : operands) {
                index++;

                if (mask != null) {
                    Object maskObj = mask.get(index);

                    if (maskObj instanceof Boolean) {
                        if (!(Boolean) maskObj) {
                            continue;
                        }
                    }

                }

                Object operator2Value = rule.getOperand2();
                if (ruleIDtoValueMap.get(operator2Value) != null) {
                    AbstractCollection<?> operands2 = (AbstractCollection<?>) ruleIDtoValueMap.get(operator2Value);
                    Object[] operandArr2 = operands2.toArray();
                    Object n2 = operandArr2[index];
                    operator2Value = n2;
                }

                boolean comp = compare(nO, operator2Value, rule.getOperator());

                if (comp && (nO instanceof IIFCProduct product)) {
                    successes.getChildren().add(new ResultObject(product));
                }

                if (!comp && (nO instanceof IIFCProduct product)) {
                    failures.getChildren().add(new ResultObject(product));
                }

                //Add to new Mask for ruleID save
                newMask.add(comp);
                if (!comp) {
                    appliesForAll = false;
                }
            }
        }

        String ruleID = rule.getLabel();
        if (ruleID != null) {
            ruleIDtoValueMap.put(ruleID, newMask);
        }

        return appliesForAll;
    }


    private boolean compare(Object operand1, Object operand2, String operator) {

        switch (operator.toLowerCase()) {
            case "equals":

                String b1 = operand1.toString().toLowerCase();
                String b2 = operand2.toString().toLowerCase();

                if (("t".equals(b1) || "f".equals(b1) || "true".equalsIgnoreCase(b1) || "false".equals(b1) || "0".equals(b1) || "1".equals(b1)) &&
                        ("t".equals(b2) || "f".equals(b2) || "true".equalsIgnoreCase(b2) || "false".equals(b2) || "0".equals(b2) || "1".equals(b2))) {
                    return Boolean.parseBoolean(b1) == Boolean.parseBoolean(b2);
                } else {
                    return operand1.toString().equals(operand2.toString());
                }

            case "includes":
                if (operand1 instanceof Collection<?>) {
                    return ((Collection<?>) operand1).contains(operand2);
                }
                // unnecessary cause List and Set are children of Collection
                /* else if (operand1 instanceof List<?>) {
                    return ((List<?>) operand1).contains(operand2);
                } else if (operand1 instanceof Set<?>) {
                    return ((Set<?>) operand1).contains(operand2);
                } */
            case "greaterthan":
                double operand1AsNumberGT = Double.parseDouble(operand1.toString());
                double operand2AsNumberGT = Double.parseDouble(operand2.toString());
                return operand1AsNumberGT > operand2AsNumberGT;
            case "lessorequals":
                double operand1AsNumberLOE = Double.parseDouble(operand1.toString());
                double operand2AsNumberLOE = Double.parseDouble(operand2.toString());
                return operand1AsNumberLOE <= operand2AsNumberLOE;
            default:
                break;
        }

        return false;
    }

}
