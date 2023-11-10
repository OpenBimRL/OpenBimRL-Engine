package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.model.RuleBase
import de.rub.bi.inf.openbimrl.helper.OpenBimRLReader
import java.io.File
import kotlin.io.path.Path

import de.rub.bi.inf.nativelib.*


fun main(args: Array<String>) {

    val functions = FunctionsNative("test.so");

    println(functions.sum(10, 10))

    if (args.isEmpty()) {
        println(usage())
        return
    }


    val openBimRlFiles = args.filter { arg -> arg.endsWith(".openbimrl") }.map { arg -> File(arg) }

    val test = OpenBimRLReader(openBimRlFiles)

    for (ruleDef in RuleBase.getInstance().rules) {
        ruleDef.check(
            null
        )
        println(ruleDef.checkedStatus)
        println(ruleDef.resultObjects.size)
        println(ruleDef.getCheckingProtocol())
    }
}

private fun usage(): String {
    return "Usage: java -jar OpenBimRL-Engine-<Version>-jar-with-dependencies.jar 1.openbimrl [*.openbimrl] [model.ifc]"
}
