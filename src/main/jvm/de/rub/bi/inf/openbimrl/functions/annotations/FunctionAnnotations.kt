package de.rub.bi.inf.openbimrl.functions.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class OpenBIMRLFunction(
    val name: String = "",
    val packageName: String = "",
    val type: String = "functionType"
)

@Target(AnnotationTarget.FIELD)
annotation class FunctionInput(
    val position: Int,
    val collectionType: KClass<*> = Any::class,
    val nullable: Boolean = true
)

@Target(AnnotationTarget.FIELD)
annotation class FunctionOutput(val position: Int, val collectionType: KClass<*> = Any::class)