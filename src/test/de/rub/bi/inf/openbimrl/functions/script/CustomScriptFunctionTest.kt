package de.rub.bi.inf.openbimrl.functions.script

import de.rub.bi.inf.openbimrl.functions.annotations.findFunctionPortDefinitions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.vecmath.Point3d

class CustomScriptFunctionTest {

    @Test
    fun `customScript exposes default template ports`() {
        val ports = findFunctionPortDefinitions(CustomScriptFunction::class.java)
        assertEquals(listOf("in0"), ports.inputs.map { it.displayName })
        assertEquals(listOf("result"), ports.outputs.map { it.displayName })
    }

    @Test
    fun `evaluator doubles a named Double input into result`() {
        val result = KtsScriptEvaluator.eval(
            userSource = "result = value * 2.0",
            inputBindings = mapOf("value" to 21.0),
            inputPorts = listOf(ScriptPort("value", typeHint = "Double")),
            outputPorts = listOf(ScriptPort("result", typeHint = "Double")),
        )
        assertEquals(42.0, result.outputs["result"])
    }

    @Test
    fun `evaluator transforms Point3d binding`() {
        val input = Point3d(1.0, 2.0, 3.0)
        val result = KtsScriptEvaluator.eval(
            userSource = """
                result = test?.let { Point3d(it.x, it.y + 1.0, it.z) }
            """.trimIndent(),
            inputBindings = mapOf("test" to input),
            inputPorts = listOf(ScriptPort("test", typeHint = "Point3d")),
            outputPorts = listOf(ScriptPort("result", typeHint = "Point3d")),
        )
        val out = result.outputs["result"] as Point3d
        assertEquals(1.0, out.x, 1e-9)
        assertEquals(3.0, out.y, 1e-9)
        assertEquals(3.0, out.z, 1e-9)
    }

    @Test
    fun `wrapUserSource declares output vars and collects map`() {
        val wrapped = KtsScriptEvaluator.wrapUserSource(
            "result = 1",
            listOf(ScriptPort("result"), ScriptPort("other")),
        )
        assertTrue(wrapped.contains("var result: Any? = null"))
        assertTrue(wrapped.contains("var other: Any? = null"))
        assertTrue(wrapped.contains("""mapOf("result" to result, "other" to other)"""))
    }

    @Test
    fun `compile errors surface as ScriptEvalException`() {
        val ex = assertThrows<KtsScriptEvaluator.ScriptEvalException> {
            KtsScriptEvaluator.eval(
                userSource = "result = thisIsNotDefined",
                inputBindings = emptyMap(),
                inputPorts = emptyList(),
                outputPorts = listOf(ScriptPort("result")),
            )
        }
        assertTrue(ex.reports.isNotEmpty() || ex.message!!.isNotBlank())
    }
}
