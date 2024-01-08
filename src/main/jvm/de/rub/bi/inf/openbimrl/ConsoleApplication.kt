package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.model.RuleBase
import de.rub.bi.inf.nativelib.*
import de.rub.bi.inf.openbimrl.helper.OpenBimRLReader
import de.rub.bi.inf.logger.RuleLogger

import java.io.File

fun main(args: Array<String>) {

    FunctionsNative.create("lib.so")
    val functions = FunctionsNative.getInstance()

    if (args.isEmpty()) {
        println(usage())
        return
    }

    val openBimRlFiles = args.filter { arg -> arg.endsWith(".openbimrl") }.map { arg -> File(arg) }
    val ifcFile = args.find { arg -> arg.endsWith(".ifc") }

    if (ifcFile != null && functions.initIfc(ifcFile)) {
        println("model loaded successfully")
    } else println("no model loaded")

    if (openBimRlFiles.isEmpty()) {
        println("no checking file loaded")
        return
    }
    val test = OpenBimRLReader(openBimRlFiles)

    for (ruleDef in RuleBase.getInstance().rules) {
        ruleDef.check(null, RuleLogger())
        println(ruleDef.checkedStatus)
        println(ruleDef.resultObjects.size)
        println(ruleDef.getCheckingProtocol())
    }
}

private fun usage(): String {
    return "Usage: java -jar OpenBimRL-Engine-<Version>-jar-with-dependencies.jar graph1.openbimrl [*.openbimrl] [model.ifc]"
}
