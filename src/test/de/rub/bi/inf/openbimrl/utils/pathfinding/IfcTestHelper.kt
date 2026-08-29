package de.rub.bi.inf.openbimrl.utils.pathfinding

import de.rub.bi.inf.nativelib.IfcPointer
import de.rub.bi.inf.nativelib.NativeEngine
import de.rub.bi.inf.openbimrl.utils.boundingBoxFromDoubles
import java.nio.file.Paths
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d

object IfcTestHelper {

    const val DOOR_1_GUID = "0PathTest000000000022"
    const val DOOR_2_GUID = "0PathTest000000000026"

    /** JVM property / env override for integration tests: absolute path or filename under test/resources. */
    private const val IFC_PATH_PROPERTY = "openbimrl.test.ifc"
    private const val IFC_PATH_ENV = "OPENBIMRL_TEST_IFC"
    private const val START_GUID_PROPERTY = "openbimrl.test.startGuid"
    private const val START_GUID_ENV = "OPENBIMRL_TEST_START_GUID"

    fun testResourcePath(vararg relativeUnderResources: String): String {
        val rel = Paths.get("src", "test", "resources", *relativeUnderResources)
        val candidates = mutableListOf(rel.toFile())
        System.getenv("OPENBIMRL_ENGINE_ROOT")?.let { root ->
            candidates += Paths.get(root).resolve(rel).toFile()
        }
        System.getenv("TEST_SRCDIR")?.let { srcdir ->
            val srcdirPath = Paths.get(srcdir)
            val workspace = System.getenv("TEST_WORKSPACE") ?: "_main"
            candidates += srcdirPath.resolve(workspace).resolve(rel).toFile()
            candidates += srcdirPath.resolve(rel).toFile()
            // Monorepo root runs Engine as @openbimrl_engine (runfiles: openbimrl_engine+/…).
            candidates += srcdirPath.resolve("openbimrl_engine+").resolve(rel).toFile()
        }
        System.getenv("JAVA_RUNFILES")?.let { runfiles ->
            val runfilesPath = Paths.get(runfiles)
            val workspace = System.getenv("TEST_WORKSPACE") ?: "_main"
            candidates += runfilesPath.resolve(workspace).resolve(rel).toFile()
            candidates += runfilesPath.resolve(rel).toFile()
            candidates += runfilesPath.resolve("openbimrl_engine+").resolve(rel).toFile()
        }
        return candidates.firstOrNull { it.isFile }?.absolutePath
            ?: rel.toFile().absolutePath
    }

    fun pathfindingMinimalIfcPath(): String =
        testResourcePath("pathfinding_minimal.ifc")

    fun ic6JournalPaperIfcPath(): String =
        testResourcePath("2024-10-25_IC6_ASR_Journal_Paper.ifc")

    fun resolveTestIfcPath(defaultResourceFileName: String): String {
        overrideValue(IFC_PATH_PROPERTY, IFC_PATH_ENV)?.let { return resolveIfcPathCandidate(it) }
        return testResourcePath(defaultResourceFileName)
    }

    fun resolveTestStartGuid(defaultGuid: String): String =
        overrideValue(START_GUID_PROPERTY, START_GUID_ENV) ?: defaultGuid

    private fun overrideValue(property: String, env: String): String? =
        System.getProperty(property)?.takeIf { it.isNotBlank() }
            ?: System.getenv(env)?.takeIf { it.isNotBlank() }

    private fun resolveIfcPathCandidate(value: String): String {
        val file = Paths.get(value).toFile()
        if (file.isFile) return file.absolutePath
        return testResourcePath(value)
    }

    fun railsParallelGaugeIfcPath(): String =
        testResourcePath("rails_parallel_gauge.ifc")

    fun wallsParallelIfcPath(): String =
        testResourcePath("walls_parallel.ifc")

    fun railsParallelGaugeOpenBimRLPath(): String =
        testResourcePath("rails_parallel_gauge.openbimrl")

    fun wallsParallelOpenBimRLPath(): String =
        testResourcePath("walls_parallel.openbimrl")

    fun loadNativeLibrary() {
        NativeEngine.loadNative()
    }

    fun loadIfc(absolutePath: String): Boolean =
        NativeEngine.initIfc(absolutePath)

    fun loadPathfindingMinimalIfc(): Boolean =
        loadIfc(pathfindingMinimalIfcPath())

    fun loadIc6JournalPaperIfc(): Boolean =
        loadIfc(ic6JournalPaperIfcPath())

    fun filterByElement(ifcType: String): List<IfcPointer> =
        IfcPointer.fromHandles(NativeEngine.filterByElement(ifcType))

    fun calculateBuildingBounds(): Pair<Point3d, BoundingBox> {
        val values = NativeEngine.calculatingBuildingBounds()
            ?: error("Building bounds unavailable")
        return boundingBoxFromDoubles(values)
    }

    fun getElementByGuid(guid: String): IfcPointer? =
        IfcPointer.fromHandle(NativeEngine.filterByGuid(guid))
}
