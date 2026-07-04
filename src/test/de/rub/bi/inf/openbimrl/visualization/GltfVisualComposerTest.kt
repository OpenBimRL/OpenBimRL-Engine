package de.rub.bi.inf.openbimrl.visualization

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.vecmath.Point3d

class GltfVisualComposerTest {

    @Test
    fun `distance heatmap produces valid GLB with node gpu instancing`() {
        val composer = GltfVisualComposer()
        composer.addDistanceHeatmap(
            listOf(Point3d(0.0, 0.0, 0.0), Point3d(1.0, 0.0, 1.0)),
            listOf(0.0, 5.0),
            maxDistance = 10.0,
        )

        val glb = composer.toGlb()
        assertNotNull(glb)
        assertTrue(glb!!.size > 12)

        assertEquals('g'.code.toByte(), glb[0])
        assertEquals('l'.code.toByte(), glb[1])
        assertEquals('T'.code.toByte(), glb[2])
        assertEquals('F'.code.toByte(), glb[3])

        val jsonStart = String(glb, Charsets.UTF_8)
        assertTrue(jsonStart.contains("EXT_mesh_gpu_instancing"))
        assertTrue(jsonStart.contains("unitSphere"))
        assertTrue(jsonStart.contains("_COLOR_0"))
    }

    @Test
    fun `distance heatmap respects custom point size`() {
        val composer = GltfVisualComposer()
        composer.addDistanceHeatmap(
            listOf(Point3d(0.0, 0.0, 0.0)),
            listOf(1.0),
            maxDistance = 10.0,
            pointSize = 2.0,
        )

        val glb = composer.toGlb()
        assertNotNull(glb)
        assertTrue(String(glb!!, Charsets.UTF_8).contains("unitSphere"))
    }
}
