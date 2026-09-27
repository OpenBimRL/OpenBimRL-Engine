package de.rub.bi.inf.openbimrl.functions.script

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

/**
 * Base script definition for OpenBimRL Code nodes.
 *
 * Default imports expose geometry helpers; IFC/native types are intentionally
 * not imported (scripts should consume them via upstream graph nodes only).
 */
@KotlinScript(
    fileExtension = "openbimrl.kts",
    compilationConfiguration = OpenBimRlScriptCompilationConfiguration::class,
)
abstract class OpenBimRlScriptTemplate

object OpenBimRlScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        "javax.vecmath.Point3d",
        "javax.vecmath.Vector3d",
        "de.rub.bi.inf.openbimrl.utils.math.Straight",
        "de.rub.bi.inf.openbimrl.utils.math.Plane",
        "kotlin.collections.listOf",
        "kotlin.collections.mapOf",
        "kotlin.collections.emptyList",
    )
    jvm {
        // Trusted-operator model for v1; true sandbox / classloader whitelist is follow-up work.
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
})
