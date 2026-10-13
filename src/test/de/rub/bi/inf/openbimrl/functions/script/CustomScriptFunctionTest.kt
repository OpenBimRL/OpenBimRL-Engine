package de.rub.bi.inf.openbimrl.functions.script

import de.rub.bi.inf.openbimrl.functions.annotations.findFunctionPortDefinitions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.media.j3d.BoundingBox
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
    fun `evaluator resolves BoundingBox lower upper extensions`() {
        val box = BoundingBox(Point3d(0.0, 1.0, 2.0), Point3d(3.0, 4.0, 5.0))
        val result = KtsScriptEvaluator.eval(
            userSource = """
                val lo = box.lower()
                val hi = box.upper()
                result = Point3d(hi.x - lo.x, hi.y - lo.y, hi.z - lo.z)
            """.trimIndent(),
            inputBindings = mapOf("box" to box),
            inputPorts = listOf(ScriptPort("box", typeHint = "BoundingBox")),
            outputPorts = listOf(ScriptPort("result", typeHint = "Point3d")),
        )
        val out = result.outputs["result"] as Point3d
        assertEquals(3.0, out.x, 1e-9)
        assertEquals(3.0, out.y, 1e-9)
        assertEquals(3.0, out.z, 1e-9)
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

    @Test
    fun `evaluator captures println stdout`() {
        val result = KtsScriptEvaluator.eval(
            userSource = """
                println("hello-debug")
                result = 1
            """.trimIndent(),
            inputBindings = emptyMap(),
            inputPorts = emptyList(),
            outputPorts = listOf(ScriptPort("result")),
        )
        assertEquals(1, result.outputs["result"])
        assertTrue(result.stdout.contains("hello-debug"), "stdout was: ${result.stdout}")
    }

    @Test
    fun `compile only reports success for valid script`() {
        val result = KtsScriptEvaluator.compile(
            userSource = "result = 42",
            inputPorts = emptyList(),
            outputPorts = listOf(ScriptPort("result")),
        )
        assertTrue(result.ok)
    }

    @Test
    fun `consolePrefix uses alias and node id`() {
        assertEquals("MyScript-n1", CustomScriptFunction.consolePrefix("MyScript", "n1"))
        assertEquals("Script-n1", CustomScriptFunction.consolePrefix(null, "n1"))
    }

    @Test
    fun `Double port coerces String and single-element collection`() {
        val port = ScriptPort("a", typeHint = "Double")
        assertEquals(10.5, ScriptInputCoercion.coerce("10,5", port))
        assertEquals(12.0, ScriptInputCoercion.coerce(listOf(12.0), port))
        assertEquals(3.0, ScriptInputCoercion.coerce(listOf("3"), port))
        assertEquals(7.0, ScriptInputCoercion.coerce(listOf(1, 7.0), port)) // filterIsInstance<Double> first
    }

    @Test
    fun `script with Double port skips manual conversion when coerced`() {
        val port = ScriptPort("a", typeHint = "Double")
        val coerced = ScriptInputCoercion.coerce(listOf(12.5), port)
        val result = KtsScriptEvaluator.eval(
            userSource = """
                val limit = 10.0
                bigger = a != null && a > limit
                smaller = a != null && a <= limit
            """.trimIndent(),
            inputBindings = mapOf("a" to coerced),
            inputPorts = listOf(port),
            outputPorts = listOf(
                ScriptPort("bigger", typeHint = "Boolean"),
                ScriptPort("smaller", typeHint = "Boolean"),
            ),
        )
        assertEquals(true, result.outputs["bigger"])
        assertEquals(false, result.outputs["smaller"])
    }
}
