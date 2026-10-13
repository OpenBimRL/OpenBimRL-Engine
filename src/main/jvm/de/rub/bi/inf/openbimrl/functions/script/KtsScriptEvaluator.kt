package de.rub.bi.inf.openbimrl.functions.script

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.JvmScriptCompiler
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

/**
 * Compiles and evaluates OpenBimRL Code-node Kotlin scripts.
 *
 * Inputs are injected as named [providedProperties]; outputs are declared as
 * nullable `var`s wrapping the user body, collected into a map as the last expression.
 *
 * [println]/[System.out] during evaluation is captured into [EvalResult.stdout].
 */
object KtsScriptEvaluator {
    private val host = BasicJvmScriptingHost()
    private val compiler = JvmScriptCompiler()
    private val evalLock = Any()

    data class EvalResult(
        val outputs: Map<String, Any?>,
        val reports: List<String> = emptyList(),
        val stdout: String = "",
    )

    data class CompileResult(
        val ok: Boolean,
        val reports: List<String> = emptyList(),
    )

    class ScriptEvalException(
        message: String,
        val reports: List<String> = emptyList(),
        val stdout: String = "",
    ) : RuntimeException(message)

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

        synchronized(evalLock) {
            val captured = ByteArrayOutputStream()
            val capturingOut = PrintStream(captured, true, StandardCharsets.UTF_8)
            val previousOut = System.out
            val previousErr = System.err
            try {
                System.setOut(capturingOut)
                // Scripts often use println; leave err as-is so host diagnostics still show,
                // but also tee common println-only paths via out.
                val result = host.eval(source, compileConfig, evalConfig)
                capturingOut.flush()
                val stdout = captured.toString(StandardCharsets.UTF_8)
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
                                        stdout,
                                    )
                                EvalResult(map, reports, stdout)
                            }
                            is ResultValue.Unit -> EvalResult(
                                outputPorts.associate { it.name to null },
                                reports,
                                stdout,
                            )
                            is ResultValue.Error -> throw ScriptEvalException(
                                "Script evaluation error: ${returnValue.error.message}",
                                reports + (returnValue.error.message ?: ""),
                                stdout,
                            )
                            else -> throw ScriptEvalException(
                                "Unexpected script return value: $returnValue",
                                reports,
                                stdout,
                            )
                        }
                    }
                    is ResultWithDiagnostics.Failure -> {
                        val reports = result.reports.map { it.render() }
                        throw ScriptEvalException(
                            "Script compilation/evaluation failed:\n${reports.joinToString("\n")}",
                            reports,
                            stdout,
                        )
                    }
                }
            } finally {
                System.setOut(previousOut)
                System.setErr(previousErr)
            }
        }
    }

    /** Compile-only; does not evaluate or require input bindings. */
    fun compile(
        userSource: String,
        inputPorts: List<ScriptPort>,
        outputPorts: List<ScriptPort>,
    ): CompileResult {
        val wrapped = wrapUserSource(userSource, outputPorts)
        val source: SourceCode = wrapped.toScriptSource("openbimrl-code.kts")
        val compileConfig = buildCompilationConfiguration(inputPorts)

        synchronized(evalLock) {
            val result = runBlocking { compiler.invoke(source, compileConfig) }
            return when (result) {
                is ResultWithDiagnostics.Success<*> -> CompileResult(
                    ok = true,
                    reports = result.reports.map { it.render() },
                )
                is ResultWithDiagnostics.Failure -> CompileResult(
                    ok = false,
                    reports = result.reports.map { it.render() },
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
