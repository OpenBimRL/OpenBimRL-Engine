package de.rub.bi.inf.nativelib

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.sun.jna.Memory
import com.sun.jna.Pointer
import java.nio.charset.StandardCharsets

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

    val type = ifcData.ifcClass
    val guid = ifcData.guid
    val properties = ifcData.propertySets
    val quantities = ifcData.quantitySets
}


