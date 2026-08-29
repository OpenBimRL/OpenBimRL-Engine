package de.rub.bi.inf.nativelib

@JvmInline
value class IfcHandle(val value: Long) {
    val isValid: Boolean get() = value != 0L
}

data class IfcData(
    val ifcClass: String,
    val guid: String,
    val propertySets: Map<String, Map<String, String>>,
    val quantitySets: Map<String, Map<String, Double>>,
)

class IfcPointer(val handle: Long) {
    private val ifcData: IfcData by lazy { loadIfcData() }

    val type: String get() = ifcData.ifcClass
    val guid: String get() = ifcData.guid
    val properties: Map<String, Map<String, String>> get() = ifcData.propertySets
    val quantities: Map<String, Map<String, Double>> get() = ifcData.quantitySets

    val polygon: Lazy<java.util.Optional<java.awt.geom.Path2D.Double>> = lazy {
        buildFootprintPolygon(NativeEngine.footprintPolygonXY(handle))
    }

    fun toHandle(): IfcHandle = IfcHandle(handle)

    private fun loadIfcData(): IfcData {
        val ifcClass = NativeEngine.ifcClass(handle).trim()
        val guid = NativeEngine.ifcGuid(handle)

        val propertySets = linkedMapOf<String, LinkedHashMap<String, String>>()
        val flat = NativeEngine.ifcPropertiesFlat(handle)
        var index = 0
        while (index + 2 < flat.size) {
            val psetName = flat[index++]
            val propertyName = flat[index++]
            val propertyValue = flat[index++]
            propertySets.getOrPut(psetName) { linkedMapOf() }[propertyName] = propertyValue
        }

        val quantitySets = linkedMapOf<String, LinkedHashMap<String, Double>>()
        val quantityKeys = NativeEngine.ifcQuantityKeys(handle)
        val quantityValues = NativeEngine.ifcQuantityValues(handle)
        quantityKeys.forEachIndexed { valueIndex, encodedKey ->
            val separator = encodedKey.indexOf('\t')
            if (separator <= 0 || valueIndex >= quantityValues.size) return@forEachIndexed
            val qsetName = encodedKey.substring(0, separator)
            val quantityName = encodedKey.substring(separator + 1)
            quantitySets.getOrPut(qsetName) { linkedMapOf() }[quantityName] =
                quantityValues[valueIndex]
        }

        return IfcData(
            ifcClass = ifcClass,
            guid = guid,
            propertySets = propertySets,
            quantitySets = quantitySets,
        )
    }

    companion object {
        fun fromHandle(handle: Long): IfcPointer? = if (handle == 0L) null else IfcPointer(handle)

        fun fromHandles(handles: LongArray): List<IfcPointer> = buildList {
            for (handle in handles) {
                fromHandle(handle)?.let { add(it) }
            }
        }

        private fun buildFootprintPolygon(values: DoubleArray): java.util.Optional<java.awt.geom.Path2D.Double> {
            if (values.size < 2) return java.util.Optional.empty()

            val coords = ArrayList<java.awt.geom.Point2D.Double>(values.size / 2)
            var x = 0.0
            for ((index, coordinate) in values.withIndex()) {
                if (index % 2 == 0) {
                    x = coordinate
                    continue
                }
                coords.add(java.awt.geom.Point2D.Double(x, coordinate))
            }

            val triangles = ArrayList<java.awt.geom.Path2D.Double>(coords.size / 3)
            var triangle: java.util.Optional<java.awt.geom.Path2D.Double> = java.util.Optional.empty()

            for ((index, coordinate) in coords.withIndex()) {
                if (triangle.isEmpty) {
                    val path = java.awt.geom.Path2D.Double()
                    path.moveTo(coordinate.x, coordinate.y)
                    triangle = java.util.Optional.of(path)
                    continue
                }

                val path = triangle.get()
                path.lineTo(coordinate.x, coordinate.y)
                if (index % 3 == 2) {
                    path.closePath()
                    triangles.add(path)
                    triangle = java.util.Optional.empty()
                }
            }

            if (triangles.isEmpty()) return java.util.Optional.empty()
            if (triangles.size == 1) return java.util.Optional.of(triangles[0])

            val polygon = java.awt.geom.Path2D.Double()
            triangles.forEach { polygon.append(it, false) }
            return java.util.Optional.of(polygon)
        }
    }

    override fun equals(other: Any?): Boolean =
        other is IfcPointer && handle == other.handle

    override fun hashCode(): Int = handle.hashCode()

    override fun toString(): String = "IfcPointer(handle=$handle, guid=$guid, type=$type)"
}
