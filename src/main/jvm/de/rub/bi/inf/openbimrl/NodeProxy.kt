package de.rub.bi.inf.openbimrl

import de.rub.bi.inf.openbimrl.functions.FunctionFactory

class NodeProxy(val node: NodeType) {

    private val inputEdges = let {
        if (node.getInputs()?.getInput() == null)
            return@let null

        val size = node.getInputs().getInput().size
        val map = HashMap<Int, MutableList<EdgeProxy>?>(size)
        for (i in 0..size)
            map[i] = null

        return@let map
    }
    private val outputEdges = let {
        if (node.getOutputs()?.getOutput() == null)
            return@let null

        val size = node.getOutputs().getOutput().size
        val map = HashMap<Int, MutableList<EdgeProxy>?>(size)
        for (i in 0..size)
            map[i] = null

        return@let map
    }

    val function = let { FunctionFactory.getFunction(this) }

    private fun setEdge(edgeMap: MutableMap<Int, MutableList<EdgeProxy>?>?, edge: EdgeProxy, pos: Int) {
        if (edgeMap.isNullOrEmpty() || edgeMap.size <= pos) return

        edgeMap[pos] = let {
            val edges = edgeMap[pos] ?: ArrayList()
            edges.add(edge)
            return@let edges
        }
    }

    private fun getEdges(edgeMap: Map<Int, MutableList<EdgeProxy>?>?, pos: Int) = edgeMap?.get(pos)


    fun setInputEdge(edge: EdgeProxy, pos: Int) = setEdge(inputEdges, edge, pos)
    fun setOutputEdge(edge: EdgeProxy, pos: Int) = setEdge(outputEdges, edge, pos)

    fun getInputEdges(pos: Int) = getEdges(inputEdges, pos)
    fun getInputEdges(): List<EdgeProxy> =
        inputEdges?.values?.filterNotNull()?.flatten() ?: emptyList()

    fun getOutputEdges(pos: Int) = getEdges(outputEdges, pos)
    /**
     * All outgoing edges across output handles.
     *
     * Must ignore null handle slots: a [reduce] starting on a null handle-0 list
     * used to return an empty collection, which broke topological sort whenever the
     * only wired output was handle 1+ (e.g. planePlaneMetric Straight → visualize).
     */
    fun getOutputEdges(): List<EdgeProxy> =
        outputEdges?.values?.filterNotNull()?.flatten() ?: emptyList()

    fun getInputEdge(pos: Int) = getInputEdges(pos)?.getOrNull(0)
    fun getOutputEdge(pos: Int) = getOutputEdges(pos)?.getOrNull(0)


    fun getNumberOfOutputs() = outputEdges?.size ?: 0

    fun execute() {
        try {
            function.injectInputs()
            function.execute()
            function.injectOutputs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}