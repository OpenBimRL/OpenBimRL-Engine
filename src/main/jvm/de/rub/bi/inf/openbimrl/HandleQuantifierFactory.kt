package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.model.ResultObject
import de.rub.bi.inf.model.ResultObjectGroup
import de.rub.bi.inf.nativelib.IfcPointer

class HandleQuantifierFactory(
    private val checkingProtocol: MutableList<String>,
    private val ruleIDtoValueMap: MutableMap<String, Any>,
    private val successes: ResultObjectGroup?,
    private val failures: ResultObjectGroup?,
    private val compare: (Any?, Any?, String) -> Boolean
) {

    private fun checkMask(rule: RuleType, mask: List<*>?, size: Int): Boolean {
        if (mask != null && size != mask.size) {
            this.checkingProtocol.add("Size of the filter does not match for: ${rule.getOperand1()}")
            return false
        }
        return true
    }

    private fun getOperand2(rule: RuleType, index: Int, key: Any? = null): Any? {
        return rule.getOperand2().let {
            when (val op2 = ruleIDtoValueMap[it]) {
                is Map<*, *> -> (op2[key] as? List<*>)?.get(index)
                is AbstractCollection<*> -> op2.toTypedArray()[index]
                null -> it
                else -> op2
            }
        }
    }


    private fun compareInnerList(
        list: Any?,
        rule: RuleType,
        key: Any?
    ): Boolean {
        if (list !is List<*>) return false

        var result = false
        for ((index, item) in list.withIndex()) {
            val op2 = getOperand2(rule, index, key)
            result = result || compare(item, op2, rule.getOperator())

            if (item is IfcPointer) {
                if (result) successes?.children?.add(ResultObject(item))
                else failures?.children?.add(ResultObject(item))
            }
        }

        return result
    }

    fun create(
        operation: (Boolean, Boolean) -> Boolean
    ): (RuleType, List<*>?) -> Boolean {
        return inner@{ rule: RuleType, mask: List<*>? ->
            var result = false

            val newMask = ArrayList<Boolean>(mask?.size ?: 0)

            when (val operands = ruleIDtoValueMap[rule.getOperand1()]) {
                is Map<*, *> -> {
                    if (!checkMask(rule, mask, operands.size)) return@inner false

                    operands.onEachIndexed { index, (key, value) ->
                        val maskItem = mask?.get(index)
                        if (maskItem is Boolean && !maskItem) { // null check implied
                            return@onEachIndexed
                        }

                        val comp = compareInnerList(value, rule, key)

                        newMask.add(comp)
                        result = operation(result, comp)
                    }
                }

                is AbstractCollection<*> -> {
                    if (!checkMask(rule, mask, operands.size)) return@inner false

                    for ((index, operand) in operands.withIndex()) {
                        val maskItem = mask?.get(index)
                        if (maskItem is Boolean && !maskItem) { // null check implied
                            continue
                        }

                        val op2 = getOperand2(rule, index)

                        val comp = compare(operand, op2, rule.getOperator())

                        if (operand is IfcPointer) {
                            if (comp) successes?.children?.add(ResultObject(operand))
                            else failures?.children?.add(ResultObject(operand))
                        }
                        newMask.add(comp)
                        result = operation(result, comp)
                    }
                }
            }

            return@inner result


        }
    }

}