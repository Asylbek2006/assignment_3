package algorithms;

import graph.Edge;
import graph.Graph;

import java.util.*;

public class Prim {
    public static class Result {
        public List<Edge> mstEdges;
        public int totalCost;
        public int operationsCount;
        public double executionTimeMs;

        public Result(List<Edge> mstEdges, int totalCost, int operationsCount, double executionTimeMs) {
            this.mstEdges = mstEdges;
            this.totalCost = totalCost;
            this.operationsCount = operationsCount;
            this.executionTimeMs = executionTimeMs;
        }
    }

    public static Result findMST(Graph graph) {
        long startTime = System.nanoTime();
        int operations = 0;

        List<String> nodes = graph.getNodes();
        if (nodes.isEmpty()) {
            return new Result(new ArrayList<>(), 0, 0, 0);
        }

        String startNode = nodes.get(0);
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;

        visited.add(startNode);
        for (Edge edge : graph.getAdjacencyList().get(startNode)) {
            pq.add(edge);
            operations++;
        }

        while (!pq.isEmpty() && visited.size() < nodes.size()) {
            Edge minEdge = pq.poll();
            operations++;
            String toNode = minEdge.to;

            if (visited.contains(toNode)) {
                continue;
            }

            visited.add(toNode);
            mstEdges.add(minEdge);
            totalCost += minEdge.weight;
            operations++;

            for (Edge nextEdge : graph.getAdjacencyList().get(toNode)) {
                if (!visited.contains(nextEdge.to)) {
                    pq.add(nextEdge);
                    operations++;
                }
            }
        }

        if (visited.size() != nodes.size()) {
            return new Result(new ArrayList<>(), -1, operations, 0);
        }

        double executionTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return new Result(mstEdges, totalCost, operations, executionTimeMs);
    }
}
