package de.rub.bi.inf.openbimrl.visualization

import kmp.gltf.model.Accessor
import kmp.gltf.model.Asset
import kmp.gltf.model.Buffer
import kmp.gltf.model.BufferView
import kmp.gltf.model.Gltf
import kmp.gltf.model.Mesh
import kmp.gltf.model.Node
import kmp.gltf.model.Scene
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

internal object GlbWriter {

    private val json = kotlinx.serialization.json.Json {
        encodeDefaults = false
        explicitNulls = false
    }

    fun encode(gltf: Gltf, bin: ByteArray): ByteArray {
        val jsonBytes = json.encodeToString(Gltf.serializer(), gltf).toByteArray(StandardCharsets.UTF_8)
        val jsonPadding = (4 - jsonBytes.size % 4) % 4
        val binPadding = (4 - bin.size % 4) % 4
        val jsonChunkLength = jsonBytes.size + jsonPadding
        val binChunkLength = bin.size + binPadding
        val totalLength = 12 + 8 + jsonChunkLength + 8 + binChunkLength

        val buffer = ByteBuffer.allocate(totalLength).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putInt(0x46546C67)
        buffer.putInt(2)
        buffer.putInt(totalLength)
        buffer.putInt(jsonChunkLength)
        buffer.putInt(0x4E4F534A)
        buffer.put(jsonBytes)
        repeat(jsonPadding) { buffer.put(0x20) }
        buffer.putInt(binChunkLength)
        buffer.putInt(0x004E4942)
        buffer.put(bin)
        repeat(binPadding) { buffer.put(0) }
        return buffer.array()
    }
}

internal object GltfGlbEncoder {

    fun encode(
        spheres: List<SphereInstance>,
        boxes: List<BoxInstance>,
        lines: List<LineSegmentInstance> = emptyList(),
    ): ByteArray {
        val assembly = GltfAssembly()

        if (spheres.isNotEmpty()) {
            val (positions, normals, indices) = buildUnitSphere()
            val posBv = assembly.addFloatBufferView(positions, BufferView.Target.ARRAY_BUFFER)
            val normBv = assembly.addFloatBufferView(normals, BufferView.Target.ARRAY_BUFFER)
            val idxBv = assembly.addIndexBufferView(indices)

            val posAcc = assembly.addAccessor(
                bufferView = posBv,
                componentType = Accessor.ComponentType.FLOAT,
                type = Accessor.Type.VEC3,
                count = positions.size / 3,
                min = vec3Min(positions),
                max = vec3Max(positions),
            )
            val normAcc = assembly.addAccessor(
                bufferView = normBv,
                componentType = Accessor.ComponentType.FLOAT,
                type = Accessor.Type.VEC3,
                count = normals.size / 3,
            )
            val idxAcc = assembly.addAccessor(
                bufferView = idxBv,
                componentType = Accessor.ComponentType.UNSIGNED_SHORT,
                type = Accessor.Type.SCALAR,
                count = indices.size,
            )

            assembly.meshes.add(
                Mesh(
                    name = "unitSphere",
                    primitives = listOf(
                        Mesh.Primitive(
                            attributes = mapOf(
                                "POSITION" to posAcc,
                                "NORMAL" to normAcc,
                            ),
                            indices = idxAcc,
                            mode = Mesh.Primitive.Mode.TRIANGLES,
                        ),
                    ),
                ),
            )
            val meshIndex = assembly.meshes.lastIndex

            val translations = FloatArray(spheres.size * 3)
            val scales = FloatArray(spheres.size * 3)
            val colors = FloatArray(spheres.size * 3)
            spheres.forEachIndexed { i, s ->
                translations[i * 3] = s.x
                translations[i * 3 + 1] = s.y
                translations[i * 3 + 2] = s.z
                scales[i * 3] = s.scale
                scales[i * 3 + 1] = s.scale
                scales[i * 3 + 2] = s.scale
                colors[i * 3] = s.r
                colors[i * 3 + 1] = s.g
                colors[i * 3 + 2] = s.b
            }

            val translationAcc = assembly.addAccessor(
                bufferView = assembly.addFloatBufferView(translations, BufferView.Target.ARRAY_BUFFER),
                componentType = Accessor.ComponentType.FLOAT,
                type = Accessor.Type.VEC3,
                count = spheres.size,
            )
            val scaleAcc = assembly.addAccessor(
                bufferView = assembly.addFloatBufferView(scales, BufferView.Target.ARRAY_BUFFER),
                componentType = Accessor.ComponentType.FLOAT,
                type = Accessor.Type.VEC3,
                count = spheres.size,
            )
            val colorAcc = assembly.addAccessor(
                bufferView = assembly.addFloatBufferView(colors, BufferView.Target.ARRAY_BUFFER),
                componentType = Accessor.ComponentType.FLOAT,
                type = Accessor.Type.VEC3,
                count = spheres.size,
            )

            // Three.js GLTFLoader reads EXT_mesh_gpu_instancing on the node, not the primitive.
            val instancingExtension = buildJsonObject {
                put(
                    "EXT_mesh_gpu_instancing",
                    buildJsonObject {
                        put(
                            "attributes",
                            buildJsonObject {
                                put("TRANSLATION", translationAcc)
                                put("SCALE", scaleAcc)
                                put("_COLOR_0", colorAcc)
                            },
                        )
                    },
                )
            }

            assembly.nodes.add(
                Node(
                    name = "spheres",
                    mesh = meshIndex,
                    extensions = instancingExtension,
                ),
            )
        }

        boxes.forEachIndexed { i, box ->
            val (verts, lineIndices) = buildBoxMesh(box)
            addLineMesh(assembly, "box$i", verts, lineIndices)
        }

        if (lines.isNotEmpty()) {
            val vertices = FloatArray(lines.size * 6)
            val indices = ShortArray(lines.size * 2)
            lines.forEachIndexed { index, line ->
                val base = index * 6
                vertices[base] = line.x1
                vertices[base + 1] = line.y1
                vertices[base + 2] = line.z1
                vertices[base + 3] = line.x2
                vertices[base + 4] = line.y2
                vertices[base + 5] = line.z2
                indices[index * 2] = (index * 2).toShort()
                indices[index * 2 + 1] = (index * 2 + 1).toShort()
            }
            addLineMesh(assembly, "geometryLines", vertices, indices)
        }

        val gltf = Gltf(
            asset = Asset(version = "2.0", generator = "OpenBimRL-Engine"),
            extensionsUsed = if (spheres.isNotEmpty()) setOf("EXT_mesh_gpu_instancing") else emptySet(),
            buffers = listOf(Buffer(byteLength = assembly.binSize)),
            bufferViews = assembly.bufferViews,
            accessors = assembly.accessors,
            meshes = assembly.meshes,
            nodes = assembly.nodes,
            scene = 0,
            scenes = listOf(Scene(nodes = assembly.nodes.indices.toSet())),
        )

        return GlbWriter.encode(gltf, assembly.binBytes())
    }

    private class GltfAssembly {
        val accessors = mutableListOf<Accessor>()
        val bufferViews = mutableListOf<BufferView>()
        val meshes = mutableListOf<Mesh>()
        val nodes = mutableListOf<Node>()
        private var bytes = ByteArray(0)

        val binSize: Int get() = bytes.size

        fun binBytes() = bytes

        fun addFloatBufferView(data: FloatArray, target: BufferView.Target): Int {
            val offset = bytes.size
            val chunk = ByteBuffer.allocate(data.size * 4).order(ByteOrder.LITTLE_ENDIAN)
            chunk.asFloatBuffer().put(data)
            bytes += chunk.array()
            val index = bufferViews.size
            bufferViews.add(
                BufferView(
                    buffer = 0,
                    byteOffset = offset,
                    byteLength = data.size * 4,
                    target = target,
                ),
            )
            return index
        }

        fun addIndexBufferView(data: ShortArray): Int {
            val offset = bytes.size
            val chunk = ByteBuffer.allocate(data.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            chunk.asShortBuffer().put(data)
            bytes += chunk.array()
            val index = bufferViews.size
            bufferViews.add(
                BufferView(
                    buffer = 0,
                    byteOffset = offset,
                    byteLength = data.size * 2,
                    target = BufferView.Target.ELEMENT_ARRAY_BUFFER,
                ),
            )
            return index
        }

        fun addAccessor(
            bufferView: Int,
            componentType: Accessor.ComponentType,
            type: Accessor.Type,
            count: Int,
            min: List<Float> = emptyList(),
            max: List<Float> = emptyList(),
        ): Int {
            val index = accessors.size
            accessors.add(
                Accessor(
                    bufferView = bufferView,
                    componentType = componentType,
                    count = count,
                    type = type,
                    min = min,
                    max = max,
                ),
            )
            return index
        }
    }

    private fun addLineMesh(
        assembly: GltfAssembly,
        name: String,
        vertices: FloatArray,
        indices: ShortArray,
    ) {
        val posBv = assembly.addFloatBufferView(vertices, BufferView.Target.ARRAY_BUFFER)
        val idxBv = assembly.addIndexBufferView(indices)
        val posAcc = assembly.addAccessor(
            bufferView = posBv,
            componentType = Accessor.ComponentType.FLOAT,
            type = Accessor.Type.VEC3,
            count = vertices.size / 3,
            min = vec3Min(vertices),
            max = vec3Max(vertices),
        )
        val idxAcc = assembly.addAccessor(
            bufferView = idxBv,
            componentType = Accessor.ComponentType.UNSIGNED_SHORT,
            type = Accessor.Type.SCALAR,
            count = indices.size,
        )
        assembly.meshes.add(
            Mesh(
                name = name,
                primitives = listOf(
                    Mesh.Primitive(
                        attributes = mapOf("POSITION" to posAcc),
                        indices = idxAcc,
                        mode = Mesh.Primitive.Mode.LINES,
                    ),
                ),
            ),
        )
        assembly.nodes.add(Node(name = name, mesh = assembly.meshes.lastIndex))
    }

    private fun vec3Min(data: FloatArray): List<Float> {
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        for (i in data.indices step 3) {
            minX = kotlin.math.min(minX, data[i])
            minY = kotlin.math.min(minY, data[i + 1])
            minZ = kotlin.math.min(minZ, data[i + 2])
        }
        return listOf(minX, minY, minZ)
    }

    private fun vec3Max(data: FloatArray): List<Float> {
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY
        for (i in data.indices step 3) {
            maxX = kotlin.math.max(maxX, data[i])
            maxY = kotlin.math.max(maxY, data[i + 1])
            maxZ = kotlin.math.max(maxZ, data[i + 2])
        }
        return listOf(maxX, maxY, maxZ)
    }

    private fun buildUnitSphere(): Triple<FloatArray, FloatArray, ShortArray> {
        val segments = 8
        val rings = 6
        val vertices = mutableListOf<FloatArray>()
        val normals = mutableListOf<FloatArray>()
        val indices = mutableListOf<Short>()

        for (ring in 0..rings) {
            val phi = Math.PI * ring / rings
            val y = kotlin.math.cos(phi).toFloat()
            val ringRadius = kotlin.math.sin(phi).toFloat()
            for (seg in 0..segments) {
                val theta = 2.0 * Math.PI * seg / segments
                val x = (ringRadius * kotlin.math.cos(theta)).toFloat()
                val z = (ringRadius * kotlin.math.sin(theta)).toFloat()
                vertices.add(floatArrayOf(x, y, z))
                normals.add(floatArrayOf(x, y, z))
            }
        }

        for (ring in 0 until rings) {
            for (seg in 0 until segments) {
                val first = (ring * (segments + 1) + seg).toShort()
                val second = (first + segments + 1).toShort()
                indices.add(first)
                indices.add(second)
                indices.add((first + 1).toShort())
                indices.add(second)
                indices.add((second + 1).toShort())
                indices.add((first + 1).toShort())
            }
        }

        return Triple(
            vertices.flatMap { it.toList() }.toFloatArray(),
            normals.flatMap { it.toList() }.toFloatArray(),
            indices.toShortArray(),
        )
    }

    private fun buildBoxMesh(box: BoxInstance): Pair<FloatArray, ShortArray> {
        val hx = (box.sizeX * 0.5).toFloat()
        val hy = (box.sizeY * 0.5).toFloat()
        val hz = (box.sizeZ * 0.5).toFloat()
        val cx = box.centerX
        val cy = box.centerY
        val cz = box.centerZ
        val corners = floatArrayOf(
            cx - hx, cy - hy, cz - hz,
            cx + hx, cy - hy, cz - hz,
            cx + hx, cy + hy, cz - hz,
            cx - hx, cy + hy, cz - hz,
            cx - hx, cy - hy, cz + hz,
            cx + hx, cy - hy, cz + hz,
            cx + hx, cy + hy, cz + hz,
            cx - hx, cy + hy, cz + hz,
        )
        val lines = shortArrayOf(
            0, 1, 1, 2, 2, 3, 3, 0,
            4, 5, 5, 6, 6, 7, 7, 4,
            0, 4, 1, 5, 2, 6, 3, 7,
        )
        return corners to lines
    }
}
