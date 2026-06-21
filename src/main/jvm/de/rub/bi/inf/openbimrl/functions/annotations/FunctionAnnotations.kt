package de.rub.bi.inf.openbimrl.functions.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class OpenBIMRLFunction(
    val name: String = "",
    val description: String = "",
    val packageName: String = "",
    val type: String = "functionType",
    val inputs: Array<FunctionPort> = [],
    val outputs: Array<FunctionPort> = [],
)

@Target(AnnotationTarget.FIELD)
annotation class FunctionInput(
    val position: Int,
    val collectionType: KClass<*> = Any::class,
    val nullable: Boolean = true
)

@Target(AnnotationTarget.FIELD)
annotation class FunctionOutput(val position: Int, val collectionType: KClass<*> = Any::class)

@Target(AnnotationTarget.CLASS)
annotation class FunctionPort(
    val position: Int,
    val name: String = "",
    val type: KClass<*> = Any::class,
    val collectionType: KClass<*> = Any::class,
    val isCollection: Boolean = false,
)
