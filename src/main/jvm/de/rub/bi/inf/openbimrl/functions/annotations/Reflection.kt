package de.rub.bi.inf.openbimrl.functions.annotations

import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import java.lang.reflect.Field

data class FunctionFields(val inputs: Set<Field>, val outputs: Set<Field>)

/**
 * @return null safe set
 */
private fun findAnnotatedFields(`class`: Class<*>, ann: Class<out Annotation>): Set<Field> {
    return `class`.declaredFields.filter {
        it.isAnnotationPresent(ann)
    }.toSet()
}

fun findFunctionFields(`class`: Class<*>): FunctionFields {
    if (!AbstractFunction::class.java.isAssignableFrom(`class`)) // safety check only. This should realistically never happen
        throw IllegalArgumentException(`class`.simpleName + " is not an OpenBIMRL Function type!")
    return FunctionFields(
        findAnnotatedFields(`class`, FunctionInput::class.java),
        findAnnotatedFields(`class`, FunctionOutput::class.java)
    )
}