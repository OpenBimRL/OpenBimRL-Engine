package de.rub.bi.inf.logger

import arrow.core.Either
import javax.media.j3d.Bounds

typealias BoundsWithProperties = Pair<Bounds, Map<String, Either<Int, String>>?>

class RuleLogger {


    class Node(val inputs: Array<Any>, val outputs: Array<Any>)

    private val nodeMap = HashMap<String, Node>()
    private val results = HashMap<String, Any?>()
    private val graphicOutputs = HashMap<String, List<BoundsWithProperties>>()

    fun logNode(nodeId: String, inputData: Array<Any>, outputData: Array<Any>) {
        nodeMap[nodeId] = Node(inputData, outputData)
    }

    fun logResult(name: String, input: Any?) {
        results[name] = input
    }

    fun logGraphicalOutput(n: String, elements: List<BoundsWithProperties>) {
        graphicOutputs[n] = elements
    }

    // returns a clone of the original
    fun getLogs() = HashMap(nodeMap)

    // returns a clone of the original
    fun getResults() = HashMap(results)

    // returns a clone of the original
    fun getGraphicalOutputs() = HashMap(graphicOutputs)
}