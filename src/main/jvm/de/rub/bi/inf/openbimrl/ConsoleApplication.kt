package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.model.RuleBase
import de.rub.bi.inf.openbimrl.helper.OpenBimRLReader
import java.io.File
import kotlin.io.path.Path


fun main(args: Array<String>) {

    try {
		System.load("/usr/java/packages/lib/openbimrl-ifcopenshell.so")
	} catch (e: Exception) {
		e.printStackTrace();
		System.exit(1);
	}

    println(test12() + "lol")

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

external fun test12(): String

private fun usage(): String {
    return "Usage: java -jar OpenBimRL-Engine-<Version>-jar-with-dependencies.jar 1.openbimrl [*.openbimrl] [model.ifc]"
}
