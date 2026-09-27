package de.rub.bi.inf.openbimrl.functions.script

import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

/**
 * Compiles and evaluates OpenBimRL Code-node Kotlin scripts.
 *
 * Inputs are injected as named [providedProperties]; outputs are declared as
 * nullable `var`s wrapping the user body, collected into a map as the last expression.
 */
object KtsScriptEvaluator {
    private val host = BasicJvmScriptingHost()

    data class EvalResult(
        val outputs: Map<String, Any?>,
        val reports: List<String> = emptyList(),
    )

    class ScriptEvalException(message: String, val reports: List<String> = emptyList()) :
        RuntimeException(message)

    fun eval(
        userSource: String,
        inputBindings: Map<String, Any?>,
        inputPorts: List<ScriptPort>,
        outputPorts: List<ScriptPort>,
    ): EvalResult {
        val wrapped = wrapUserSource(userSource, outputPorts)
        val source = wrapped.toScriptSource("openbimrl-code.kts")

        val compileConfig = buildCompilationConfiguration(inputPorts)
        val evalConfig = ScriptEvaluationConfiguration {
            providedProperties(inputBindings)
        }

        val result = host.eval(source, compileConfig, evalConfig)
        return when (result) {
            is ResultWithDiagnostics.Success -> {
                val reports = result.reports.map { it.render() }
                val returnValue = result.value.returnValue
                when (returnValue) {
                    is ResultValue.Value -> {
                        @Suppress("UNCHECKED_CAST")
                        val map = returnValue.value as? Map<String, Any?>
                            ?: throw ScriptEvalException(
                                "Script did not return an output map (got ${returnValue.value})",
                                reports,
                            )
                        EvalResult(map, reports)
                    }
                    is ResultValue.Unit -> EvalResult(
                        outputPorts.associate { it.name to null },
                        reports,
                    )
                    is ResultValue.Error -> throw ScriptEvalException(
                        "Script evaluation error: ${returnValue.error.message}",
                        reports + (returnValue.error.message ?: ""),
                    )
                    else -> throw ScriptEvalException(
                        "Unexpected script return value: $returnValue",
                        reports,
                    )
                }
            }
            is ResultWithDiagnostics.Failure -> {
                val reports = result.reports.map { it.render() }
                throw ScriptEvalException(
                    "Script compilation/evaluation failed:\n${reports.joinToString("\n")}",
                    reports,
                )
            }
        }
    }

    fun wrapUserSource(userSource: String, outputPorts: List<ScriptPort>): String {
        val outputs = outputPorts.ifEmpty { listOf(ScriptPort("result")) }
        val decls = outputs.joinToString("\n") { "var ${it.name}: Any? = null" }
        val collect = outputs.joinToString(", ") { "\"${it.name}\" to ${it.name}" }
        return buildString {
            appendLine(decls)
            appendLine()
            appendLine(userSource.trim())
            appendLine()
            appendLine("mapOf($collect)")
        }
    }

    private fun buildCompilationConfiguration(inputPorts: List<ScriptPort>): ScriptCompilationConfiguration {
        val base = createJvmCompilationConfigurationFromTemplate<OpenBimRlScriptTemplate>()
        if (inputPorts.isEmpty()) return base
        return ScriptCompilationConfiguration(base) {
            providedProperties(
                *inputPorts.map { it.name to ScriptTypeCatalog.toKotlinType(it) }.toTypedArray(),
            )
        }
    }

    private fun ScriptDiagnostic.render(): String =
        "${severity.name}: $message" + (location?.let { " at ${it.start.line}:${it.start.col}" } ?: "")
}
