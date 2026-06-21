package de.rub.bi.inf.openbimrl.functions.math

import de.rub.bi.inf.openbimrl.functions.annotations.findFunctionPortDefinitions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MaximumTest {

    @Test
    fun `maximum exposes double collection input and output ports`() {
        val ports = findFunctionPortDefinitions(Maximum::class.java)

        assertEquals(1, ports.inputs.size)
        assertEquals(1, ports.outputs.size)
        assertEquals("Double", ports.inputs.single().displayName)
        assertEquals("Double", ports.outputs.single().displayName)
    }
}
