package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.logger.RuleLogger
import de.rub.bi.inf.model.RuleBase
import de.rub.bi.inf.nativelib.FunctionsNative
import de.rub.bi.inf.nativelib.ThreeDTester
import de.rub.bi.inf.openbimrl.helper.OpenBimRLReader
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        FunctionsNative.create("lib.so")
    } catch (_: Exception) {
        println("Could not load native lib! Aborting...")
        exitProcess(42)
    }

    try {
        ThreeDTester()
    } catch (e: RuntimeException) {
        // ignore
    }

    val functions = FunctionsNative.getInstance()

    if (args.isEmpty()) {
        println(usage())
        exitProcess(0)
    }

    val openBimRlFiles = args.filter { arg -> arg.endsWith(".openbimrl") }.map { arg -> File(arg) }
    val ifcFile = args.find { arg -> arg.endsWith(".ifc") }

    if (ifcFile != null && functions.initIfc(ifcFile)) {
        println("model loaded successfully")
    } else println("no model loaded")

    if (openBimRlFiles.isEmpty()) {
        println("no checking file loaded")
        exitProcess(1)
    }
    val test = OpenBimRLReader(openBimRlFiles)

    for (ruleDef in RuleBase.getInstance().rules) {
        ruleDef.check(RuleLogger())
        println(ruleDef.checkedStatus)
        println(ruleDef.resultObjects.size)
        println(ruleDef.getCheckingProtocol())
    }
    while (true) {
        Thread.sleep(100000)
    }

    exitProcess(0)
}

private fun usage(): String {
    return "Usage: java -jar OpenBimRL-Engine-<Version>-jar-with-dependencies.jar graph1.openbimrl [*.openbimrl] [model.ifc]"
}
