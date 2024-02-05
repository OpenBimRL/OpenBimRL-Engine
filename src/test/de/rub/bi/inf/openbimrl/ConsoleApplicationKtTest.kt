package de.rub.bi.inf.openbimrl

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertAll
import java.nio.file.Paths
import kotlin.io.path.listDirectoryEntries
import de.rub.bi.inf.openbimrl.main as mainFun


class ConsoleApplicationKtTest {

    @org.junit.jupiter.api.Test
    fun main() {
        val resourceDirectory = Paths.get("src", "test", "resources")
        val absolutePath = resourceDirectory.toFile().absolutePath

        println("loading models from path $absolutePath")

        assertTrue(absolutePath.endsWith("src/test/resources"))

        val ifcFiles = resourceDirectory.listDirectoryEntries("*.ifc").map {
            it.toFile().absolutePath
        }
        val openBimRLFiles = resourceDirectory.listDirectoryEntries("*.openbimrl").map {
            it.toFile().absolutePath
        }

        assertFalse(ifcFiles.isEmpty())
        assertFalse(openBimRLFiles.isEmpty())

        for (ifcFile in ifcFiles) {
            for (testFile in openBimRLFiles) {
                println("testing $ifcFile with $testFile")
                assertAll({ mainFun(arrayOf(ifcFile, testFile)) })
            }
        }
    }
}