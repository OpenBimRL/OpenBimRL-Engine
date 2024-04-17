package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.logger.RuleLogger
import de.rub.bi.inf.model.ResultObjectGroup
import de.rub.bi.inf.model.SimpleRule
import de.rub.bi.inf.openbimrl.helper.FilterInterpreter
import java.util.*

class OpenSubRule(
    private val modelSubCheck: ModelSubCheckType,
    private val ruleIDtoValueMap: MutableMap<String, Any>
) :
    SimpleRule() {
    init {
        this.name = modelSubCheck.name
    }

    private var successes: ResultObjectGroup? = null
    private var failures: ResultObjectGroup? = null

    override fun check(logger: RuleLogger?) {
        // Reset the check
        super.checkedStatus = CheckedStatus.UNCHECKED
        super.checkingProtocol.clear()

        successes = ResultObjectGroup("ResultSet: Successfully Checked")
        failures = ResultObjectGroup("ResultSet: Failed Checked")

        //this.checkingProtocol.add("=============Subcheck Protocol===============")

        //this.checkingProtocol.add("=============Subcheck Protocol===============");
        try {
            println("Performing Subcheck: " + modelSubCheck.getName())

            when {
                // executes if not applicable
                !this.isApplicable(modelSubCheck) -> {
                    super.checkingProtocol.add("This subcheck is not applicable for the model.")
                    super.checkedStatus = CheckedStatus.WARNING
                }

                // implied else
                interpret(modelSubCheck.getRules()) -> {
                    this.checkedStatus = CheckedStatus.SUCCESS
                    checkingProtocol.add("This rule executed successfully.")
                }

                // if both paths are not taken (not applicable and not interpreted)
                else -> {
                    this.checkedStatus = CheckedStatus.FAILED
                    checkingProtocol.add("This rule executed unsuccessfully.")
                }
            }
        } catch (e2: Exception) {
            // sets status to "FAILED" if an error occurred
            this.checkedStatus = CheckedStatus.FAILED
            checkingProtocol.add("A error occured. Please revise the code")

            e2.printStackTrace()
        }

        if (successes?.children?.isNotEmpty() == true) {
            resultObjects.add(successes)
        }

        if (failures?.children?.isNotEmpty() == true) {
            resultObjects.add(failures)
        }

        // this.checkingProtocol.add("======================================");
    }

    private fun isApplicable(subCheck: ModelSubCheckType): Boolean {
        if (subCheck.getApplicability() == null) {
            return true //if no applicability is set, allow all
        }

        val rules = subCheck.getApplicability().getRules()

        return interpretRules(rules)
    }

    private fun interpret(rulesOrFilterOrRule: Any): Boolean {
        return when (rulesOrFilterOrRule) {
            is RuleType -> interpretRule(rulesOrFilterOrRule)
            is RulesType -> interpretRules(rulesOrFilterOrRule)
            else -> false
        }
    }

    private fun interpretRules(rules: RulesType): Boolean {
        val operator = rules.getOperator()

        //evaluate children
        val results = ArrayList<Boolean>()
        for (ruleOrFilterOrRules in rules.getRuleOrRules()) {
            val result = interpret(ruleOrFilterOrRules)
            results.add(result)
        }

        return when (operator) {
            "and" -> results.stream().allMatch { e: Boolean -> e }
            "or" -> results.stream().anyMatch { e: Boolean -> e }
            "xor" -> (results.reduceOrNull { acc: Boolean, value: Boolean -> acc.xor(value) } ?: true)
            else -> throw RuntimeException("operator $operator not found!")
        }
    }

    private fun interpretRule(rule: RuleType): Boolean {
        if (rule.getQuantifier() == null) {
            return handleQuantifierDefault(rule)
        }

        //Option to add a filter mask if quantifiers are provided
        val mask = rule.getFilter()?.let { FilterInterpreter.interpret(it, ruleIDtoValueMap) }

        val factory = HandleQuantifierFactory(checkingProtocol, ruleIDtoValueMap, successes, failures, this::compare)

        return when (rule.getQuantifier()) {
            "notexists" -> !factory.create(Boolean::or)(rule, mask)
            "exists" -> factory.create(Boolean::or)(rule, mask)
            "all" -> factory.create(Boolean::and)(rule, mask)
            else -> false
        }
    }

    private fun handleQuantifierDefault(rule: RuleType): Boolean {
        val ruleOperand1 = ruleIDtoValueMap[rule.getOperand1()] ?: rule.getOperand1()
        val ruleOperand2 = ruleIDtoValueMap[rule.getOperand2()] ?: rule.getOperand2()

        when (ruleOperand1) {
            is Boolean -> {
                val operand2 = ruleOperand2.toString().toBoolean()
                return compare(ruleOperand1, operand2, rule.getOperator())
            }

            is Double -> {
                val operand2 = ruleOperand2.toString().toDouble()
                return compare(ruleOperand1, operand2, rule.getOperator())
            }

            is Int -> {
                val operand2 = ruleOperand2.toString().toInt()
                return compare(ruleOperand1, operand2, rule.getOperator())
            }

            else -> {
                // default Case
                return compare(
                    ruleOperand1.toString(),
                    ruleOperand2.toString(),
                    rule.getOperator()
                )
            }
        }
    }

    private fun compare(operand1: Any?, operand2: Any?, operator: String): Boolean {
        val operations = mapOf(
            "greaterthan" to { a: Double, b: Double -> a > b },
            "greaterorequals" to { a: Double, b: Double -> a >= b },
            "lessthan" to { a: Double, b: Double -> a < b },
            "lessorequals" to { a: Double, b: Double -> a <= b },
        )

        when (val operatorLC = operator.lowercase(Locale.getDefault())) {
            "equals" -> {
                val b1 = operand1.toString().lowercase(Locale.getDefault())
                val b2 = operand2.toString().lowercase(Locale.getDefault())

                val trueVals = arrayOf("t", "true", "1")
                val falseVals = arrayOf("f", "false", "0")
                val boolVals = arrayOf(*trueVals, *falseVals)
                if (b1 in boolVals && b2 in boolVals)
                    return (b1 in trueVals) == (b2 in trueVals)
                return b1 == b2
            }

            "includes" -> {
                if (operand1 is Collection<*>) {
                    return operand1.contains(operand2)
                }
                // please fix me why
                val operand1AsNumberGT = operand1.toString().toDouble()
                val operand2AsNumberGT = operand2.toString().toDouble()
                return operand1AsNumberGT > operand2AsNumberGT
            }

            in operations.keys -> {
                val operand1AsNumber = operand1.toString().toDouble()
                val operand2AsNumber = operand2.toString().toDouble()
                return operations[operatorLC]!!(operand1AsNumber, operand2AsNumber)
            }

            else -> throw RuntimeException("operator $operator not found!")
        }
    }
}