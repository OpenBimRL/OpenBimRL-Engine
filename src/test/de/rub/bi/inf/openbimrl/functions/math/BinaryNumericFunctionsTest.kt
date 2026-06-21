package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.functions.annotations.findFunctionPortDefinitions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BinaryNumericFunctionsTest {

    @Test
    fun `binary numeric functions expose class level ports`() {
        listOf(Addition::class.java, Subtraction::class.java, Multiplication::class.java).forEach { functionClass ->
            val ports = findFunctionPortDefinitions(functionClass)
            assertEquals(listOf("number(s)", "number(s)"), ports.inputs.map { it.displayName })
            assertEquals(listOf("result"), ports.outputs.map { it.displayName })
        }
    }
}
