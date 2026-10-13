package de.rub.bi.inf.openbimrl.functions.script

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionPort
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction

/**
 * Single registered engine function for per-node Kotlin scripts (CreatorTool Code node).
 *
 * Ports are defined dynamically on the node XML (mapper), not via field [@FunctionInput].
 * Script source is read from [de.rub.bi.inf.openbimrl.NodeType.getScript].
 */
@OpenBIMRLFunction(
    packageName = "script",
    name = "customScript",
    description = "Runs embedded Kotlin script with mapper-defined inputs/outputs.",
    type = "codeType",
    inputs = [FunctionPort(position = 0, name = "in0")],
    outputs = [FunctionPort(position = 0, name = "result")],
)
class CustomScriptFunction(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {

    override fun execute() {
        val node = nodeProxy.node
        val script = node.script
            ?: throw IllegalStateException("Node ${node.id} (script.customScript) has no <Script> element")
        val language = script.language?.takeIf { it.isNotBlank() } ?: "kotlin"
        if (!language.equals("kotlin", ignoreCase = true) &&
            !language.equals("kts", ignoreCase = true)
        ) {
            throw IllegalStateException("Unsupported script language '$language' (v1 supports kotlin only)")
        }
        val source = script.value?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Node ${node.id} has empty script body")

        val inputXml = node.inputs?.input.orEmpty()
        val outputXml = node.outputs?.output.orEmpty()

        val inputPorts = inputXml.mapIndexed { index, input ->
            ScriptPort(
                name = input.name?.takeIf { it.isNotBlank() } ?: "in$index",
                typeHint = input.typeHint,
                collectionType = input.collectionType,
                position = index,
            )
        }
        val outputPorts = if (outputXml.isEmpty()) {
            listOf(ScriptPort(name = "result", position = 0))
        } else {
            outputXml.mapIndexed { index, output ->
                ScriptPort(
                    name = output.name?.takeIf { it.isNotBlank() } ?: "out$index",
                    typeHint = output.typeHint,
                    collectionType = output.collectionType,
                    position = index,
                )
            }
        }

        val bindings = LinkedHashMap<String, Any?>()
        inputPorts.forEach { port ->
            // Mirror AbstractFunction injectInputs: coerce by mapper typeHint
            // (e.g. Double ← String / single-element Collection).
            bindings[port.name] = ScriptInputCoercion.coerce(getInput<Any?>(port.position), port)
        }

        val consolePrefix = consolePrefix(node.alias, node.id)

        try {
            val evalResult = KtsScriptEvaluator.eval(source, bindings, inputPorts, outputPorts)
            ScriptConsole.appendPrefixed(consolePrefix, evalResult.stdout)
            outputPorts.forEach { port ->
                setResult(port.position, evalResult.outputs[port.name])
            }
        } catch (e: KtsScriptEvaluator.ScriptEvalException) {
            ScriptConsole.appendPrefixed(consolePrefix, e.stdout)
            ScriptConsole.append("$consolePrefix: ERROR ${e.message}")
            e.reports.forEach { ScriptConsole.append("$consolePrefix:   $it") }
            throw e
        }
    }

    companion object {
        fun consolePrefix(alias: String?, nodeId: String?): String {
            val name = alias?.takeIf { it.isNotBlank() } ?: "Script"
            val id = nodeId?.takeIf { it.isNotBlank() } ?: "?"
            return "$name-$id"
        }
    }
}
