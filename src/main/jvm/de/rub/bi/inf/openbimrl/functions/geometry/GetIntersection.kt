package de.rub.bi.inf.openbimrl.functions.geometry

import de.rub.bi.inf.extensions.lower
import de.rub.bi.inf.extensions.upper
import de.rub.bi.inf.openbimrl.NodeProxy
import de.rub.bi.inf.openbimrl.functions.AbstractFunction
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionInput
import de.rub.bi.inf.openbimrl.functions.annotations.FunctionOutput
import de.rub.bi.inf.openbimrl.functions.annotations.OpenBIMRLFunction
import java.util.concurrent.ConcurrentLinkedQueue
import javax.media.j3d.BoundingBox
import javax.vecmath.Point3d
import kotlin.math.max
import kotlin.math.min

@OpenBIMRLFunction
class GetIntersection(nodeProxy: NodeProxy) : AbstractFunction(nodeProxy) {
    @FunctionInput(0, BoundingBox::class)
    lateinit var bounds0: List<BoundingBox>

    @FunctionInput(1, BoundingBox::class)
    lateinit var bounds1: List<BoundingBox>

    @FunctionInput(2)
    var threshold: String? = null

    @FunctionOutput(0, BoundingBox::class)
    var outputBounds: List<BoundingBox> = emptyList()

    override fun execute() {
        val threadSafeQueue = ConcurrentLinkedQueue<BoundingBox?>()

        val thresholdFloat = threshold?.toFloatOrNull() ?: 0.01f

        bounds0.parallelStream().forEach { e1 ->
            bounds1.forEach inner@{ e2 ->
                if (e1 == e2) return@inner
                val bbox = findPoints(
                    e1.lower().x,
                    e1.lower().z,
                    e1.upper().x,
                    e1.upper().z,
                    e2.lower().x,
                    e2.lower().z,
                    e2.upper().x,
                    e2.upper().z
                )

                if (bbox == null) return@inner
                if (bbox.upper().x - bbox.lower().x < thresholdFloat || bbox.upper().z - bbox.lower().z < thresholdFloat) return@inner
                threadSafeQueue.add(bbox)
            }
        }

        outputBounds = ArrayList(threadSafeQueue.filterNotNull())
    }

    private fun findPoints(
        x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double, x4: Double, y4: Double
    ): BoundingBox? {
        // gives bottom-left point
        // of intersection rectangle
        val x5 = max(x1, x3)
        val y5 = max(y1, y3)


        // gives top-right point
        // of intersection rectangle
        val x6 = min(x2, x4)
        val y6 = min(y2, y4)


        // no intersection
        if (x5 > x6 || y5 > y6) {
            println("No intersection")
            return null
        }


        // gives top-left point
        // of intersection rectangle
        val x7 = x5
        val y7 = y6


        // gives bottom-right point
        // of intersection rectangle
        val x8 = x6
        val y8 = y5

        return BoundingBox(Point3d(x8, .0, y8), Point3d(x7, .0, y7))

    }


}