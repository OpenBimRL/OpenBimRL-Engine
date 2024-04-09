package de.rub.bi.inf.nativelib

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.sun.jna.Memory
import com.sun.jna.Pointer
import java.nio.charset.StandardCharsets

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
        nativeLib.ifc_object_to_json(this, buffer)
        return@let Gson().fromJson(buffer.getString(0, StandardCharsets.US_ASCII.name()), IfcData::class.java)
    }

    val type = ifcData.ifcClass
    val guid = ifcData.guid
    val properties = ifcData.propertySets
    val quantities = ifcData.quantitySets

}


data class IfcData(
    @SerializedName("ifcClass") val ifcClass: String,
    @SerializedName("GUID") val guid: String,
    @SerializedName("propertySets") val propertySets: Map<String, Map<String, String>>,
    @SerializedName("quantitySets") val quantitySets: Map<String, Map<String, Double>>,
)