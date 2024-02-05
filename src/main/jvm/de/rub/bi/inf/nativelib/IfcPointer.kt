package de.rub.bi.inf.nativelib

import com.sun.jna.Memory
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import java.nio.charset.StandardCharsets

/**
 * for whatever reason the calculation of every memory allocation is wrong by a factor of 8. idk...
 */
class IfcPointer : Pointer {
    constructor(peer: Long) : super(peer)

    constructor(pointer: Pointer) : super(nativeValue(pointer))

    private val nativeLib: FunctionsLibrary = FunctionsNative.getInstance()

    override fun toString(): String {
        val len = 512L // this can cause memory leak or overflow
        val outString = Memory(len)
        outString.clear()
        val valid = nativeLib.ifc_object_to_string(this, outString, len)
        return if (valid) outString.getString(0, StandardCharsets.US_ASCII.name()) else "null"
    }

    open class ReadonlyLazyMap<K, V>(val size: Int = 0) {
        protected val map: MutableMap<K, Lazy<V>> = HashMap(size)
        operator fun get(key: K): V? = map[key]?.value

        fun containsKey(key: K): Boolean {
            return map.containsKey(key)
        }
    }


    class LazyMap<K, V>(size: Int = 0) : ReadonlyLazyMap<K, V>(size) {
        val openMap = map
    }

    val properties: ReadonlyLazyMap<String, LazyMap<String, String?>>
        get() {
            val noOfPropertySets = nativeLib.initPropertyIterator(this).toLong()
            if (noOfPropertySets == 0L) return LazyMap()

            val returnMap = LazyMap<String, LazyMap<String, String?>>()

            for (i in 0..noOfPropertySets) {
                val setIndex = NativeLong(i)
                val setName = getPropertySet(setIndex)
                val noOfProperties = nativeLib.getNoOfPropertiesInSet(setIndex).toLong()

                returnMap.openMap[setName] = lazy {
                    // this is dangerous but ¯\_(ツ)_/¯ (cause of conversion from int to long)
                    val map = LazyMap<String, String?>(noOfProperties.toInt())

                    for (j in 0..noOfProperties) {
                        val propertyIndex = NativeLong(j)
                        val propertyNameLen = nativeLib.getBufferSizePropertyName(setIndex, propertyIndex)
                        if (propertyNameLen.toLong() == 0L) continue
                        val propertyNameMemory = Memory(propertyNameLen.toLong() * 8)
                        propertyNameMemory.clear()
                        if (!nativeLib.getPropertyName(setIndex, propertyIndex, propertyNameMemory)) continue

                        val propertyName = propertyNameMemory.getString(0)

                        map.openMap[propertyName] = lazy {
                            // please don't look! This screams inefficiency
                            val propertyValueMemory = Memory(1000)
                            // this is a potential risk but if people use more than 1000 characters, they deserve it
                            val success = nativeLib.getPropertyValue(setIndex, propertyIndex, propertyValueMemory)
                            when (success) {
                                true -> propertyValueMemory.getString(0).trim()
                                false -> null
                            }
                        }

                    }
                    return@lazy map
                }

            }
            return returnMap
        }

    val quantities: ReadonlyLazyMap<String, LazyMap<String, Double>>
        get() {
            val noOfQuantitySets = nativeLib.initQuantityIterator(this).toLong()
            if (noOfQuantitySets == 0L) return LazyMap()

            val returnMap = LazyMap<String, LazyMap<String, Double>>()

            for (i in 0..noOfQuantitySets) {
                val setIndex = NativeLong(i)
                val setName = getQuantitySetName(setIndex)
                val noOfQuantities = nativeLib.getNoOfPropertiesInSet(setIndex).toLong()

                returnMap.openMap[setName] = lazy {
                    // this is dangerous but ¯\_(ツ)_/¯ (cause of conversion from int to long)
                    val map = LazyMap<String, Double>(noOfQuantities.toInt())

                    for (j in 0..noOfQuantities) {
                        val quantityIndex = NativeLong(j)
                        println("loading quantity name")
                        val quantityNameLen = nativeLib.getBufferSizeQuantityName(setIndex, quantityIndex)
                        if (quantityNameLen.toLong() == 0L) continue
                        val quantityNameMemory = Memory(quantityNameLen.toLong() * 8)
                        quantityNameMemory.clear()
                        if (!nativeLib.getQuantityName(setIndex, quantityIndex, quantityNameMemory)) continue

                        val quantityName = quantityNameMemory.getString(0)

                        map.openMap[quantityName] = lazy {
                            println("loading quantity double")
                            nativeLib.getQuantityValue(setIndex, quantityIndex)
                        }

                    }
                    return@lazy map
                }
            }
            return returnMap
        }

    private fun getQuantitySetName(index: NativeLong): String {
        val quantitySetNameSize = nativeLib.getBufferSizeQuantitySetName(index)

        if (quantitySetNameSize.toLong() == 0L) return index.toString() // set names are optional

        val setNameMemory = Memory(quantitySetNameSize.toLong() * 8)
        setNameMemory.clear()
        if (!nativeLib.getPropertySetName(
                index, setNameMemory
            )
        ) return index.toString() // failsafe if something went wrong
        return setNameMemory.getString(0)
    }

    private fun getPropertySet(index: NativeLong): String {
        val propertySetNameSize = nativeLib.getBufferSizePropertySetName(index)

        if (propertySetNameSize.toLong() == 0L) return index.toString() // set names are optional

        val setNameMemory = Memory(propertySetNameSize.toLong() * 8)
        setNameMemory.clear()
        if (!nativeLib.getPropertySetName(
                index, setNameMemory
            )
        ) return index.toString() // failsafe if something went wrong
        return setNameMemory.getString(0)
    }
}
