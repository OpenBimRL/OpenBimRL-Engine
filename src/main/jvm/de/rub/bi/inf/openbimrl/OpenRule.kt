package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.logger.RuleLogger
import de.rub.bi.inf.model.AbstractRuleDefinition
import de.rub.bi.inf.model.ResultObjectGroup
import de.rub.bi.inf.model.RuleSet
import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.openbimrl.functions.DisplayableFunction
import de.rub.bi.inf.openbimrl.helper.FilterInterpreter

/**
 * Defines the entry-point for execution of an OpenBimRL file, containing precalculations and rules. The check-method
 * is providing the main process for rule checking execution and should be used to run the engine.
 * An instance of [OpenRule] is derived from [RuleSet], containing several sub checks of the typ
 * [OpenSubRule]. The results of the precaluations are executed and stored prior the actual rule check.
 *
 * @author Marcel Stepien, Andre Vonthron (Reworked by Florian Becker)
 */
class OpenRule(modelCheck: ModelCheckType, precalculations: PrecalculationsType?) : RuleSet() {
    private val modelCheck: ModelCheckType
    private var precalculationContext: PrecalculationContext? = null
    private val ruleIDtoValueMap: MutableMap<String, Any?> = HashMap()

    init {
        this.name = modelCheck.getName()
        this.modelCheck = modelCheck
        this.precalculationContext = PrecalculationContext(precalculations)

        generateRules(modelCheck)
    }


    /**
     * Builds a ResultObjectGroup to be retrieved and displayed custom viewers.
     *
     * @param name
     * @param value
     */
    private fun computeResultsElements(name: String, value: Any?): ResultObjectGroup {
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

        return ResultObjectGroup(name)
    }

    override fun check(logger: RuleLogger) {
        //Reset the check
        this.resultObjects = ArrayList()
        this.checkedStatus = CheckedStatus.UNCHECKED
        this.checkingProtocol = ArrayList()

        //Step 1: Build Precalculation
        this.handlePrecalculations(logger)

        //Step 2: Transfer via RuleIdentifier
        val allParametersAvailable = handleRuleIdentifier(logger)

        //Step 3: Execute Rules
        if (allParametersAvailable) {
            handleRuleChecks(logger)
        } else {
            checkingProtocol.add("Some precalculations were not available")
            this.checkedStatus = CheckedStatus.FAILED
        }

        //Step 4: Extract Predefined ResultSet
        this.handleResultSets()
    }

    private fun handlePrecalculations(logger: RuleLogger) {
        precalculationContext?.graphSortedNodes?.forEach { node ->
            //System.out.println(node.getId()+" (FunctionName): " + node.getFunction());

            // precalculationContext can safely be assumed to have a value since this point can't be reached if it's null
            val nodeProxy = precalculationContext!!.getNodeProxy(node)

            if (nodeProxy.function is DisplayableFunction)
                nodeProxy.function.setLogger(logger)

            try {
                nodeProxy.execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            logger.logNode(
                node.function + node.getId(),
                nodeProxy.getInputEdges().stream().map { obj: EdgeProxy -> obj.currentData }.toArray(),
                nodeProxy.function.results.filterNotNull().toTypedArray()
            )

            node.getOutputs()?.getOutput()?.indices?.forEach { i ->
                val outputValue = nodeProxy.function.results[i]

                //System.out.println(nodeProxy.getNode().getId()+" (Result): "+ outputValue);
                if (outputValue == null) {
                    checkingProtocol.add("Please Check Node ${nodeProxy.node.id} (Type: ${node.getFunction()}): ${outputValue.toString()}")
                }
            }
        }
    }

    /**
     * @return
     */
    private fun handleRuleIdentifier(logger: RuleLogger): Boolean {
        var allParametersAvailable = true

        modelCheck.getRuleIdentifiers().getRuleIdentifier().forEach { ri ->

            // unsafe assumption of not null
            val nodeProxy = precalculationContext!!.getNodeProxy(ri.getSource())
            val value = nodeProxy.function.results[ri.getSourceHandle()]

            logger.logResult(ri.getLabel(), value)

            ruleIDtoValueMap[ri.getLabel()] = value
            if (value == null) {
                allParametersAvailable = false
                checkingProtocol.add(ri.getLabel() + " nicht mit einem Wert belegt!")
            } else {
                checkingProtocol.add(ri.getLabel() + "=" + value)
            }

            //Register results of the precalculation to the result view, for selection and
            val group = this.computeResultsElements("RuleIdentifier: " + ri.getLabel(), value)

            if (group.children.isNotEmpty()) {
                resultObjects.add(group)
            }
        }

        return allParametersAvailable
    }

    private fun handleRuleChecks(logger: RuleLogger) {
        //Execute for all subrules
        var tempStatus = CheckedStatus.WARNING

        for (subRule in getChildren()) {
            subRule.check(logger)

            if (subRule.checkedStatus == CheckedStatus.FAILED) {
                tempStatus = CheckedStatus.FAILED
            } else if (subRule.checkedStatus == CheckedStatus.SUCCESS && tempStatus != CheckedStatus.FAILED) {
                tempStatus = CheckedStatus.SUCCESS
            } else if (subRule.checkedStatus == CheckedStatus.WARNING && tempStatus != CheckedStatus.FAILED && tempStatus != CheckedStatus.SUCCESS) {
                tempStatus = CheckedStatus.WARNING
            }
        }

        this.checkedStatus = tempStatus
    }

    /**
     *
     */
    private fun handleResultSets() {
        modelCheck.getResultSets()?.getResultSet()?.forEach { rs ->
            val markedIfcElement = mutableListOf<IfcPointer>()

            val filterList: List<*>? = FilterInterpreter.interpret(rs.getFilter(), ruleIDtoValueMap)
            val elementsList = mutableListOf<Any?>()

            when (val elements = ruleIDtoValueMap[rs.getElements()]) {
                is Collection<*> -> elementsList.addAll(elements)
                is Any -> elementsList.add(elements)
            }

            if (filterList?.size != elementsList.size) {
                val msg =
                    "Size of elements and filter do not match for ResultSet: ${rs.getName()}( ${elementsList.size} != ${filterList?.size.toString()})"
                checkingProtocol.add(msg)
            }

            for ((index, nO) in elementsList.withIndex()) {

                if (filterList != null) {
                    val maskObj = filterList[index]!!

                    if (maskObj is Boolean) {
                        if (!maskObj) {
                            continue
                        }
                    }
                }

                if (nO is IfcPointer) {
                    markedIfcElement.add(nO);
                }
            }

            //Register results of the pre-calculation to the result
            val group = this.computeResultsElements(
                "ResultSet: " + rs.getName(),
                null // prev markedIfcElement
            )

            if (group.children.isNotEmpty()) {
                resultObjects.add(group)
            }
        }
    }

    /**
     * @param modelCheck
     */
    fun generateRules(modelCheck: ModelCheckType) {
        val root = ArrayList<AbstractRuleDefinition>()

        val subchecks = modelCheck.getModelSubChecks()
        if (subchecks != null) {
            for (subcheck in subchecks.getModelSubCheck()) {
                val ruleName = subcheck.getName()

                val subrule = OpenSubRule(subcheck, ruleIDtoValueMap)
                subrule.name = ruleName

                this.addChild(subrule)
            }
        }
    }
}
