package de.rub.bi.inf.nativelib

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.sun.jna.Memory
import com.sun.jna.Pointer
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

data class IfcData(
    @SerializedName("ifc_class") val ifcClass: String,
    @SerializedName("guid") val guid: String,
    @SerializedName("properties") val propertySets: Map<String, Map<String, String>>,
    @SerializedName("quantities") val quantitySets: Map<String, Map<String, Double>>,
)

/**
 * for whatever reason the calculation of every memory allocation is wrong by a factor of 8. idk...
 */
class IfcPointer : Pointer {
    constructor(peer: Long) : super(peer)

    constructor(pointer: Pointer) : super(nativeValue(pointer))

    private val nativeLib: FunctionsLibrary = FunctionsNative.getInstance()
    private val ifcData: IfcData = let {
        val size = nativeLib.request_ifc_object_json_size(this)
        val buffer = Memory(size.toLong() + 1)
        buffer.clear()
        nativeLib.ifc_object_to_json(buffer)

        val test = Gson().fromJson(buffer.getString(0, StandardCharsets.UTF_8.name()), IfcData::class.java)

        return@let test
    }

    val type = ifcData.ifcClass.trim()
    val guid = ifcData.guid
    val properties = ifcData.propertySets
    val quantities = ifcData.quantitySets

    val polygon: Lazy<Optional<Path2D.Double>> = lazy {
        try {
            val size = nativeLib.request_geometry_polygon(this)
            if (size.toLong() < 1) return@lazy Optional.empty()
            val memory = Memory(size.toLong() * Double.SIZE_BYTES)
            nativeLib.copy_geometry_polygon(memory)
            val values = memory.getDoubleArray(0, size.toInt()) // toInt is risky!

            if (values.size < 2) return@lazy Optional.empty()

            val coordsList = ArrayList<Point2D.Double>(values.size / 2)
            var x = .0
            for ((index, coordinate) in values.withIndex()) {
                if (index % 2 == 0) {
                    x = coordinate + 800
                    continue
                }
                val y = coordinate + 400
                println("[$x, $y]")
                coordsList.add(Point2D.Double(x, y))
            }

            val triangleList = ArrayList<Path2D.Double>(coordsList.size / 3)
            var triangle: Optional<Path2D.Double> = Optional.empty()

            for ((index, coordinate) in coordsList.withIndex()) {
                //ThreeDTester.addPoint(coordinate)
                if (triangle.isEmpty)
                {
                    val tri = Path2D.Double()
                    tri.moveTo(coordinate.x, coordinate.y)
                    triangle = Optional.of(tri)
                    continue
                }

                val tri = triangle.get()
                tri.lineTo(coordinate.x, coordinate.y)
                if (index % 3 == 2){
                    tri.closePath()
                    // ThreeDTester.addShape(tri)
                    triangleList.add(tri)
                    triangle = Optional.empty()
                }
            }

            if (triangleList.isEmpty()) return@lazy Optional.empty()
            if (triangleList.size == 1) return@lazy Optional.of(triangleList[0])

            val polygon = Path2D.Double()
            triangleList.forEach { polygon.append(it, false) }
            return@lazy Optional.of(polygon)
        } catch (e: Exception) {
            throw RuntimeException("encountered: " + e.stackTraceToString())
        }
    }
}


