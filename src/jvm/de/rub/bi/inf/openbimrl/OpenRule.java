package de.rub.bi.inf.openbimrl;

import de.rub.bi.inf.model.AbstractRuleDefinition;
import de.rub.bi.inf.model.ResultObject;
import de.rub.bi.inf.model.ResultObjectGroup;
import de.rub.bi.inf.model.RuleSet;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCModel;
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCProduct;
import de.rub.bi.inf.openbimrl.helper.FilterInterpreter;

import java.util.*;

/**
 * Defines the entry-point for execution of an OpenBimRL file, containing precalculations and rules. The check-method
 * is providing the main process for rule checking execution and should be used to run the engine.
 * An instance of {@link OpenRule} is derived from {@link RuleSet}, containing several sub checks of the type
 * {@link OpenSubRule}. The results of the precaluations are executed and stored prior the actuall rule check.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class OpenRule extends RuleSet {
    private ModelCheckType modelCheck = null;
    private PrecalculationContext precalculationContext = null;
    private Map<String, Object> ruleIDtoValueMap = new HashMap<>();

    public OpenRule(ModelCheckType modelCheck, PrecalculationsType precalculations) {
        this.name = modelCheck.getName();
        this.modelCheck = modelCheck;
        this.precalculationContext = new PrecalculationContext(precalculations);

        generateRules(modelCheck);
    }


    /**
     * Builds a ResultObjectGroup to be retrieved and displayed custom viewers.
     *
     * @param name
     * @param value
     */
    private ResultObjectGroup computeResultsElements(String name, Object value) {
        ResultObjectGroup group = new ResultObjectGroup(name);
/*        if (value instanceof Collection<?>) {
            int index = 0;
            for (Object o : (Collection<?>) value) {

                if (o instanceof IfcProduct.Ifc4) {
                    group.getChildren().add(new ResultObject((IIFCProduct) o));
                }


                if (o instanceof Collection<?>) {
                    ResultObjectGroup subGroup = this.computeResultsElements("[" + index + "]", o);
                    group.getChildren().add(subGroup);
                }

                index++;
            }
        }

        if (value instanceof Map<?, ?>) {
            HashMap<String, ResultObjectGroup> tempMap = new HashMap<String, ResultObjectGroup>();

            int mapKeyIndex = 0;
            for (Object key : ((Map<?, ?>) value).keySet()) {
                Object mapValue = ((Map<?, ?>) value).get(key);
                String mapKey = key.toString() + " [" + mapKeyIndex + "]";

                if (tempMap.get(mapKey) == null) {
                    tempMap.put(mapKey, new ResultObjectGroup(key, "Map : " + mapKey));
                    group.getChildren().add(tempMap.get(mapKey));
                }

                if (mapValue instanceof Collection<?>) {
                    int index = 0;
                    for (Object o : (Collection<?>) mapValue) {

                        if (o instanceof IfcProduct.Ifc4) {
                            tempMap.get(mapKey).getChildren().add(new ResultObject((IIFCProduct) o));
                        }

                        if (o instanceof Collection<?>) {
                            ResultObjectGroup subGroup = this.computeResultsElements("[" + index + "]", o);
                            tempMap.get(mapKey).getChildren().add(subGroup);
                        }
                        index++;
                    }
                }
                mapKeyIndex++;
            }
        }*/

        return group;
    }

    @Override
    public void check(IIFCModel ifcModel) {
        //Reset the check
        this.resultObjects = new ArrayList<ResultObjectGroup>();
        this.checkedStatus = CheckedStatus.UNCHECKED;
        this.checkingProtocol = new ArrayList<String>();

        //Step 1: Build Precalculation
        this.handlePrecalculations(ifcModel);

        //Step 2: Transfer via RuleIdentifier
        boolean allParametersAvailable = handleRuleIdentifier();

        //Step 3: Execute Rules
        if (allParametersAvailable) {
            handleRuleChecks(ifcModel);
        } else {
            this.checkingProtocol.add("Some precalculations were not available");
            this.checkedStatus = CheckedStatus.FAILED;
        }

        //Step 4: Extract Predefined ResultSet
        this.handleResultSets();
    }

    /**
     * @param ifcModel
     */
    private void handlePrecalculations(IIFCModel ifcModel) {
        for (NodeType node : precalculationContext.getGraphSortedNodes()) { // precalculationContext.getGraphNodes()) {

            //System.out.println(node.getId()+" (FunctionName): " + node.getFunction());

            NodeProxy nodeProxy = precalculationContext.getNodeProxy(node);

            try {
                nodeProxy.execute(ifcModel, nodeProxy);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < node.getOutputs().getOutput().size(); i++) {
                Object outputValue = nodeProxy.getFunction().getResults().get(i);
                //System.out.println(nodeProxy.getNode().getId()+" (Result): "+ outputValue);

                if (outputValue == null) {
                    this.checkingProtocol.add("Please Check Node " + nodeProxy.getNode().getId() + " (Type: " + node.getFunction() + "): " + outputValue);
                }
            }

        }
    }

    /**
     * @return
     */
    private boolean handleRuleIdentifier() {
        boolean allParametersAvailable = true;
        for (RuleIdentifierType ri : modelCheck.getRuleIdentifiers().getRuleIdentifier()) {

            NodeProxy nodeProxy = precalculationContext.getNodeProxy(ri.getSource());
            Object value = nodeProxy.getFunction().getResults().get(ri.getSourceHandle());

            ruleIDtoValueMap.put(ri.getLabel(), value);
            if (value == null) {
                allParametersAvailable = false;
                checkingProtocol.add(ri.getLabel() + " nicht mit einem Wert belegt!");
            } else {
                checkingProtocol.add(ri.getLabel() + "=" + value);
            }

            //Register results of the precalculation to the result view, for selection and
            ResultObjectGroup group = this.computeResultsElements("RuleIdentifier: " + ri.getLabel(), value);

            if (!group.getChildren().isEmpty()) {
                resultObjects.add(group);
            }

        }
        return allParametersAvailable;
    }

    /**
     * @param ifcModel
     */
    private void handleRuleChecks(IIFCModel ifcModel) {
        //Execute for all subrules
        CheckedStatus tempStatus = CheckedStatus.WARNING;

        for (AbstractRuleDefinition subRule : getChildren()) {
            subRule.check(ifcModel);

            if (subRule.getCheckedStatus().equals(CheckedStatus.FAILED)) {
                tempStatus = CheckedStatus.FAILED;
            } else if (subRule.getCheckedStatus().equals(CheckedStatus.SUCCESS) && !tempStatus.equals(CheckedStatus.FAILED)) {
                tempStatus = CheckedStatus.SUCCESS;
            } else if (subRule.getCheckedStatus().equals(CheckedStatus.WARNING) && !tempStatus.equals(CheckedStatus.FAILED) && !tempStatus.equals(CheckedStatus.SUCCESS)) {
                tempStatus = CheckedStatus.WARNING;
            }
        }

        this.checkedStatus = tempStatus;
    }

    /**
     *
     */
    private void handleResultSets() {
        if (modelCheck.getResultSets() != null) {
            for (ResultSetType rs : modelCheck.getResultSets().getResultSet()) {

                final var markedIfcElement = new ArrayList<IIFCProduct>();

                List<?> filterList = FilterInterpreter.interpret(rs.getFilter(), ruleIDtoValueMap);
                Collection<?> elementsList = null;

                Object elements = ruleIDtoValueMap.get(rs.getElements());
                if (elements instanceof Collection<?>) {
                    elementsList = (Collection<?>) elements;
                } else if (elements != null) {
                    ArrayList tempList = new ArrayList();
                    tempList.add(elements);
                    elementsList = tempList;
                }

                if (filterList != null ? filterList.size() != elementsList.size() : false) {
                    String msg = "Size of elements and filter do not match for ResultSet: " +
                            rs.getName() + "(" + elementsList.size() + "!=" + filterList.size() + ")";
                    this.checkingProtocol.add(msg);
                }

                int index = -1;
                for (Object nO : elementsList) {
                    index++;

                    if (filterList != null) {
                        Object maskObj = filterList.get(index);

                        if (maskObj instanceof Boolean) {
                            if (!(Boolean) maskObj) {
                                continue;
                            }
                        }

                    }

                    if (nO instanceof IIFCProduct product) {
                        markedIfcElement.add(product);
                    }
                }

                //Register results of the pre-calculation to the result
                ResultObjectGroup group = this.computeResultsElements(
                        "ResultSet: " + rs.getName(),
                        markedIfcElement
                );

                if (!group.getChildren().isEmpty()) {
                    resultObjects.add(group);
                }


            }
        }
    }

    /**
     * @param modelCheck
     */
    public void generateRules(ModelCheckType modelCheck) {

        ArrayList<AbstractRuleDefinition> root = new ArrayList<AbstractRuleDefinition>();

        ModelSubChecksType subchecks = modelCheck.getModelSubChecks();
        if (subchecks != null) {
            for (ModelSubCheckType subcheck : subchecks.getModelSubCheck()) {

                String ruleName = subcheck.getName();

                OpenSubRule subrule = new OpenSubRule(subcheck, ruleIDtoValueMap);
                subrule.setName(ruleName);

                this.addChild(subrule);
            }
        }

    }


}
