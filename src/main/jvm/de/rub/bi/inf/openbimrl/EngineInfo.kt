package de.rub.bi.inf.openbimrl

/** OpenBimRL Engine (JVM) release version from Bazel `OPENBIMRL_ENGINE_VERSION`. */
object EngineInfo {
    private const val RESOURCE = "openbimrl_engine_version.properties"

    val version: String by lazy {
        EngineInfo::class.java.classLoader
            .getResourceAsStream(RESOURCE)
            ?.bufferedReader()
            ?.use { reader ->
                reader.lineSequence()
                    .firstOrNull { it.startsWith("version=") }
                    ?.substringAfter("=")
                    ?.trim()
            }
            ?.takeIf { it.isNotEmpty() }
            ?: "dev"
    }
}
