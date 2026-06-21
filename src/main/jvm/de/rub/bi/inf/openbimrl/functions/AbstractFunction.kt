package de.rub.bi.inf.openbimrl.functions

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.findFunctionFields
import de.rub.bi.inf.openbimrl.utils.InvalidFunctionDefinitionException
import de.rub.bi.inf.openbimrl.utils.InvalidFunctionInputException
import java.lang.reflect.InaccessibleObjectException

/**
 * An abstract super-type of all functions supported by the OpenBimRL engine.
 * Instances of this class need to be registered in [FunctionFactory].
 * Furthermore, an implemented function can be addressed in an OpenBimRL file as
 * and elementary node of the pre-calculation graph.
 *
 * @author Marcel Stepien, Andre Vonthron (reworked by Florian Becker)
 */
abstract class AbstractFunction(@JvmField protected var nodeProxy: NodeProxy) {
    @JvmField
    val results: ArrayList<Any?>

    init {
        val outputLength = nodeProxy.node.outputs?.output?.size ?: 0
        results = ArrayList(outputLength)
        for (i in 0 until outputLength) {
            results.add(null) //initializing for set(i, element) method
        }
    }

    @Throws(InvalidFunctionInputException::class)
    fun injectInputs() {
        val (inputs) = findFunctionFields(javaClass)
        inputs.forEach {
            val annotation = it.getAnnotation(FunctionInput::class.java)
            val position = annotation.position

            try {
                if (Collection::class.java.isAssignableFrom(it.type)) {
                    it.set(
                        this, getInputAsTypedCollection(position, annotation.collectionType.java)
                    )
                } else {
                    if (!annotation.nullable && getInput<Any?>(position) == null) throw InvalidFunctionInputException("Parameter with name ${it.name} is not Nullable!")
                    try {
                        it.isAccessible = true
                        it.set(this, it.type.cast(getInput(position)))
                    } catch (e: IllegalAccessException) {
                        throw InvalidFunctionDefinitionException(
                            "cannot access member ${it.name} of class ${javaClass.name}",
                            e
                        )
                    } catch (e: InaccessibleObjectException) {
                        throw InvalidFunctionDefinitionException(
                            "could not set accessibility for field ${it.name} in class ${javaClass.name}",
                            e
                        )
                    }
                }
            } catch (e: ClassCastException) {
                throw InvalidFunctionInputException("Can not convert input of type ${getInput<Any>(position)!!.javaClass.name} to ${it.type.name}")
            }
        }
    }

    fun injectOutputs() {
        val (_, outputs) = findFunctionFields(javaClass)

        outputs.forEach {
            it.trySetAccessible()
            setResult(it.getAnnotation(FunctionOutput::class.java).position, it.get(this))
        }
    }

    abstract fun execute()

    protected inline fun <reified T> getInput(pos: Int): T? {
        return when (val data = nodeProxy.getInputEdge(pos)?.currentData) {
            is T -> data
            else -> null
        }
    }

    protected fun <T> getInput(pos: Int, clazz: Class<T>): T? {
        val data = nodeProxy.getInputEdge(pos)?.currentData
        return when {
            clazz.isInstance(data) -> clazz.cast(data)
            else -> null
        }
    }

    protected fun getInputAsCollection(pos: Int): Collection<*> {
        val `in` = getInput<Any?>(pos)
        if (`in` is Collection<*>) return `in`
        if (`in` == null) return emptyList<Any>()
        return listOf(`in`)
    }

    protected fun <T> getInputAsTypedCollection(pos: Int, type: Class<T>): Collection<T> {
        val input = getInputAsCollection(pos)
        if (isNumericCollectionType(type)) {
            @Suppress("UNCHECKED_CAST")
            return input.map { coerceToDouble(it) } as Collection<T>
        }
        @Suppress("UNCHECKED_CAST")
        return input.filter { type.isInstance(it) } as List<T>
    }

    protected inline fun <reified T> getInputAsTypedCollection(pos: Int): Collection<T> {
        return getInputAsTypedCollection(pos, T::class.java)
    }

    private fun isNumericCollectionType(type: Class<*>): Boolean =
        type == Double::class.java || type == java.lang.Double.TYPE

    private fun coerceToDouble(value: Any?): Double {
        if (value == null) {
            throw InvalidFunctionInputException("Cannot convert null to Double")
        }
        if (value is Collection<*>) {
            throw InvalidFunctionInputException("Expected numeric value but got nested collection")
        }
        if (value is Double) return value
        if (value is Number) return value.toDouble()
        return try {
            value.toString().toDouble()
        } catch (e: NumberFormatException) {
            throw InvalidFunctionInputException("Cannot convert input of type ${value.javaClass.name} to Double", e)
        }
    }

    protected fun setResult(pos: Int, result: Any?) {
        results[pos] = result
    }
}
