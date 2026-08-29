package de.rub.bi.inf.nativelib

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.JarURLConnection
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.jar.JarEntry
import java.util.jar.JarFile

object NativeEngine {
    /** Classpath resource name of the embedded amd64 JNI shared library. */
    const val NATIVE_LIBRARY_RESOURCE = "libopenbimrl_jni.so"

    private const val LIBRARY_NAME = "openbimrl_jni"

    @Volatile
    private var loaded = false

    init {
        loadNative()
    }

    @JvmStatic
    @Synchronized
    fun loadNative() {
        if (loaded) return
        try {
            System.loadLibrary(LIBRARY_NAME)
        } catch (_: UnsatisfiedLinkError) {
            System.load(extractLibraryFromClasspath())
        }
        loaded = true
    }

    @JvmStatic
    external fun initIfc(path: String): Boolean

    @JvmStatic
    external fun filterByElement(ifcType: String): LongArray

    @JvmStatic
    external fun filterByGuid(guid: String): Long

    @JvmStatic
    external fun getBoundingBox(handle: Long): DoubleArray?

    @JvmStatic
    external fun getElementFrame(handle: Long): DoubleArray?

    @JvmStatic
    external fun getElementFrameSource(handle: Long): Int

    @JvmStatic
    external fun calculatingBuildingBounds(): DoubleArray?

    @JvmStatic
    external fun calculatePathEdgeCosts(
        pointsXY: DoubleArray,
        pointCount: Int,
        edgePointIndices: IntArray,
        edgeCount: Int,
        passageHandles: LongArray,
        obstacleHandles: LongArray,
    ): DoubleArray

    @JvmStatic
    external fun ifcGuid(handle: Long): String

    @JvmStatic
    external fun ifcClass(handle: Long): String

    @JvmStatic
    external fun ifcPropertiesFlat(handle: Long): Array<String>

    @JvmStatic
    external fun ifcQuantityKeys(handle: Long): Array<String>

    @JvmStatic
    external fun ifcQuantityValues(handle: Long): DoubleArray

    @JvmStatic
    external fun footprintPolygonXY(handle: Long): DoubleArray

    /** True when this JNI library was built with ROCm or CUDA OpenMP offload enabled. */
    @JvmStatic
    external fun isGpuOffloadEnabled(): Boolean

    /** Offload target arch baked in at native build time (e.g. gfx1100, sm_89), or null. */
    @JvmStatic
    external fun gpuOffloadArch(): String?

    @JvmStatic
    external fun nativeLibraryVersion(): String

    @JvmStatic
    external fun nativeBuildDate(): String

    @JvmStatic
    external fun nativeBuildCompiler(): String

    /** Compile-time metadata from libopenbimrl_jni.so. */
    @JvmStatic
    fun libInfo(): LibInfo = LibInfo(
        version = nativeLibraryVersion(),
        buildDate = nativeBuildDate(),
        buildCompiler = nativeBuildCompiler(),
        gpuOffloadEnabled = isGpuOffloadEnabled(),
        gpuOffloadArch = gpuOffloadArch(),
    )

    private fun extractLibraryFromClasspath(): String {
        val file = File.createTempFile("lib", ".so")
        FileUtils.writeByteArrayToFile(file, readNativeResourceBytes(NATIVE_LIBRARY_RESOURCE))
        file.deleteOnExit()
        return file.absolutePath
    }

    private fun readNativeResourceBytes(fileName: String): ByteArray {
        val classLoader = NativeEngine::class.java.classLoader
        classLoader.getResourceAsStream(fileName)?.use { stream ->
            return stream.readBytes()
        }

        val suffix = "/$fileName"
        for (url in classpathJarUrls()) {
            if (url.protocol != "jar" && !url.path.endsWith(".jar")) continue
            val jarUrl = if (url.protocol == "jar") url else URI("jar:${url}!/").toURL()
            val connection = jarUrl.openConnection() as JarURLConnection
            connection.useCaches = false
            connection.jarFile.use { jar ->
                for (entry in jar.entries()) {
                    val name = entry.name
                    if (name == fileName || name.endsWith(suffix)) {
                        jar.getInputStream(entry).use { input -> return input.readBytes() }
                    }
                }
            }
        }
        throw IOException("Native library resource not found on classpath: $fileName")
    }

    private fun classpathJarUrls(): List<URL> {
        val urls = mutableListOf<URL>()
        for (entry in System.getProperty("java.class.path", "").split(File.pathSeparator)) {
            if (entry.isEmpty() || !entry.endsWith(".jar")) continue
            try {
                urls += File(entry).toURI().toURL()
            } catch (_: MalformedURLException) {
                // ignore invalid classpath entries
            }
        }
        return urls
    }
}
