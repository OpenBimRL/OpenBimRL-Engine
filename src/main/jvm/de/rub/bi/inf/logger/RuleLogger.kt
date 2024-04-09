package de.rub.bi.inf.logger

class RuleLogger {


    class Node(val inputs: Array<Any>, val outputs: Array<Any>)

    private val nodeMap = HashMap<String, Node>()
    private val results = HashMap<String, Any?>()

    fun logNode(nodeId: String, inputData: Array<Any>, outputData: Array<Any>) {
        nodeMap[nodeId] = Node(inputData, outputData)
    }

    fun logResult(name: String, input: Any?) {
        results[name] = input;
    }

    fun getLogs(): Map<String, Node> {
        return nodeMap
    }

    fun getResults(): HashMap<String, Any?> {
        return results
    }
}