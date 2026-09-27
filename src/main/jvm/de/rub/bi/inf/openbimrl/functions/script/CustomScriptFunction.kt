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
            bindings[port.name] = getInput<Any?>(port.position)
        }

        val evalResult = KtsScriptEvaluator.eval(source, bindings, inputPorts, outputPorts)
        outputPorts.forEach { port ->
            setResult(port.position, evalResult.outputs[port.name])
        }
    }
}
