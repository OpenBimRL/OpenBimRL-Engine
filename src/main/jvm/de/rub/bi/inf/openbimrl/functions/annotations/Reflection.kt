package de.rub.bi.inf.openbimrl.functions.annotations

import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import java.lang.reflect.Field
import java.util.Collection

data class FunctionFields(val inputs: Set<Field>, val outputs: Set<Field>)

data class FunctionPortInfo(
    val position: Int,
    val displayName: String,
)

data class FunctionPortDefinitions(
    val inputs: List<FunctionPortInfo>,
    val outputs: List<FunctionPortInfo>,
)

/**
 * @return null safe set
 */
private fun findAnnotatedFields(`class`: Class<*>, ann: Class<out Annotation>): Set<Field> {
    return `class`.declaredFields.filter {
        it.isAnnotationPresent(ann)
    }.toSet()
}

fun findFunctionFields(`class`: Class<*>): FunctionFields {
    if (!AbstractFunction::class.java.isAssignableFrom(`class`))
        throw IllegalArgumentException(`class`.simpleName + " is not an OpenBIMRL Function type!")
    return FunctionFields(
        findAnnotatedFields(`class`, FunctionInput::class.java),
        findAnnotatedFields(`class`, FunctionOutput::class.java),
    )
}

fun findFunctionPortDefinitions(`class`: Class<*>): FunctionPortDefinitions {
    if (!AbstractFunction::class.java.isAssignableFrom(`class`))
        throw IllegalArgumentException(`class`.simpleName + " is not an OpenBIMRL Function type!")

    val fields = findFunctionFields(`class`)
    val annotation = `class`.getAnnotation(OpenBIMRLFunction::class.java)
        ?: throw IllegalArgumentException(`class`.simpleName + " is missing @OpenBIMRLFunction")

    val inputs = if (fields.inputs.isNotEmpty()) {
        fields.inputs
            .sortedBy { it.getAnnotation(FunctionInput::class.java).position }
            .map { it.toInputPortInfo() }
    } else {
        annotation.inputs
            .sortedBy { it.position }
            .map { it.toPortInfo() }
    }

    val outputs = if (fields.outputs.isNotEmpty()) {
        fields.outputs
            .sortedBy { it.getAnnotation(FunctionOutput::class.java).position }
            .map { it.toOutputPortInfo() }
    } else {
        annotation.outputs
            .sortedBy { it.position }
            .map { it.toPortInfo() }
    }

    return FunctionPortDefinitions(inputs, outputs)
}

private fun Field.toInputPortInfo(): FunctionPortInfo {
    val annotation = getAnnotation(FunctionInput::class.java)
    val displayName = when {
        annotation.name.isNotBlank() -> annotation.name
        Collection::class.java.isAssignableFrom(type) -> annotation.collectionType.simpleName!!
        else -> type.simpleName
    }
    return FunctionPortInfo(annotation.position, displayName)
}

private fun Field.toOutputPortInfo(): FunctionPortInfo {
    val annotation = getAnnotation(FunctionOutput::class.java)
    val displayName = when {
        annotation.name.isNotBlank() -> annotation.name
        Collection::class.java.isAssignableFrom(type) -> annotation.collectionType.simpleName!!
        else -> type.simpleName
    }
    return FunctionPortInfo(annotation.position, displayName)
}

private fun FunctionPort.toPortInfo(): FunctionPortInfo {
    val displayName = when {
        name.isNotBlank() -> name
        isCollection -> collectionType.simpleName!!
        else -> type.simpleName!!
    }
    return FunctionPortInfo(position, displayName)
}
