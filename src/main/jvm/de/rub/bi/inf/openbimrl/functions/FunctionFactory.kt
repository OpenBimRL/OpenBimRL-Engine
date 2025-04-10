package de.rub.bi.inf.openbimrl.functions

import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.net.URL

/**
 * This class manages a collection of available functions for OpenBimRL execution,
 * effectively mapping all available function containing classes to an identifier namespace.
 * These templates are then called on demand, depending on which function should be executed.
 *
 * @author Marcel Stepien, Andre Vonthron (Reworked by Florian Becker)
 */
class FunctionFactory private constructor() {
    private val functions: MutableMap<String, Class<*>> = HashMap()

    init {
        val urls = functionPackages.stream().map<Collection<URL>>(ClasspathHelper::forPackage)
            .reduce(mutableListOf()) { acc, next -> acc + next }.toTypedArray()

        val reflection = Reflections(
            ConfigurationBuilder().setUrls(*urls).setScanners(Scanners.TypesAnnotated).setParallel(true)
        )

        val types = reflection.getTypesAnnotatedWith(OpenBIMRLFunction::class.java)
        for (type in types) {
            val annotation = type.getAnnotation(OpenBIMRLFunction::class.java)
            val name = annotation.name.let { name ->
                if (name == "") type.simpleName.replaceFirstChar { it.lowercaseChar() }
                else name
            }
            val packageName = annotation.packageName.let {
                if (it == "") type.packageName.split(".").last()
                else it
            }
            functions["$packageName.$name"] = type
        }
    }

    companion object {
        private val functionPackages = mutableListOf(
            "de.rub.bi.inf.openbimrl.functions.filter",
            "de.rub.bi.inf.openbimrl.functions.geometry",
            "de.rub.bi.inf.openbimrl.functions.ifc",
            "de.rub.bi.inf.openbimrl.functions.input",
            "de.rub.bi.inf.openbimrl.functions.list",
            "de.rub.bi.inf.openbimrl.functions.math",
        )

        @JvmStatic
        fun registerFunctionPackage(vararg packages: String) {
            functionPackages.addAll(packages)
        }

        @JvmStatic
        val registeredFunctions: Map<String, Class<*>>
            get() = instance!!.functions.toMap()

        private var instance: FunctionFactory? = null
            get() {
                if (field == null) field = FunctionFactory()
                return field
            }

        fun getFunction(nodeProxy: NodeProxy): AbstractFunction {
            val functionString = nodeProxy.node.function

            val instance = instance
            if (!instance!!.functions.containsKey(functionString)) throw RuntimeException("could not find Function: $functionString")

            try {
                return instance.functions[functionString]!!.getConstructor(NodeProxy::class.java)
                    .newInstance(nodeProxy) as AbstractFunction
            } catch (e: Exception) {
                // this should never happen but in case it does, it means you screwed up HARD!
                throw RuntimeException(
                    """
                    If you see this exception it means you screwed up so hard
                    that you now lost 500 Aura. In case you actually need to know what you did wrong,
                    please read the manual on how to extend OpenBIMRL functions.
                    If this manual does not exist yet, here is the original error instead: 
                    """.trimIndent() + e.message
                )
            }
        }
    }
}
