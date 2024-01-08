package de.rub.bi.inf.logger

class RuleLogger {


    class Node(val inputs: Array<Any>, val outputs: Array<Any>)

    private val nodeMap = HashMap<String, Node>()

    fun logNode(nodeId: String, inputData: Array<Any>, outputData: Array<Any>) {
        nodeMap[nodeId] = Node(inputData, outputData)
    }

    fun getLogs(): Map<String, Node> {
        return nodeMap
    }
}