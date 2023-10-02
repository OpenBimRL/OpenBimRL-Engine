package de.rub.bi.inf.utils.pathfinding.astar;


import com.google.common.graph.ValueGraph;

import java.util.*;
import java.util.function.Function;

public class AStarWithTreeSet {

    public static <N extends Comparable<N>> List<N> findShortestPath(
            ValueGraph<N, Double> graph, N source, N target, Function<N, Double> heuristic) {
        Map<N, AStarNodeWrapper<N>> nodeWrappers = new HashMap<>();
        TreeSet<AStarNodeWrapper<N>> queue = new TreeSet<>();
        Set<N> shortestPathFound = new HashSet<>();

        AStarNodeWrapper<N> sourceWrapper =
                new AStarNodeWrapper<>(source, null, 0.0, heuristic.apply(source));
        nodeWrappers.put(source, sourceWrapper);
        queue.add(sourceWrapper);

        while (!queue.isEmpty()) {
            AStarNodeWrapper<N> nodeWrapper = queue.pollFirst();
            N node = nodeWrapper.getNode();
            shortestPathFound.add(node);

            if (node.equals(target)) {
                return buildPath(nodeWrapper);
            }

            Set<N> neighbors = graph.adjacentNodes(node);
            for (N neighbor : neighbors) {
                if (shortestPathFound.contains(neighbor)) {
                    continue;
                }

                double cost = graph.edgeValue(node, neighbor).orElseThrow(IllegalStateException::new);
                double totalCostFromStart = nodeWrapper.getTotalCostFromStart() + cost;

                AStarNodeWrapper<N> neighborWrapper = nodeWrappers.get(neighbor);
                if (neighborWrapper == null) {
                    neighborWrapper =
                            new AStarNodeWrapper<>(
                                    neighbor, nodeWrapper, totalCostFromStart, heuristic.apply(neighbor));
                    nodeWrappers.put(neighbor, neighborWrapper);
                    queue.add(neighborWrapper);
                } else if (totalCostFromStart < neighborWrapper.getTotalCostFromStart()) {
                    queue.remove(neighborWrapper);

                    neighborWrapper.setTotalCostFromStart(totalCostFromStart);
                    neighborWrapper.setPredecessor(nodeWrapper);

                    queue.add(neighborWrapper);
                }
            }
        }

        return null;
    }

    private static <N extends Comparable<N>> List<N> buildPath(AStarNodeWrapper<N> nodeWrapper) {
        List<N> path = new ArrayList<>();
        while (nodeWrapper != null) {
            path.add(nodeWrapper.getNode());
            nodeWrapper = nodeWrapper.getPredecessor();
        }
        Collections.reverse(path);
        return path;
    }
}