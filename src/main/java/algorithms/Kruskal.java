package algorithms;

import graph.Edge;
import graph.Graph;

import java.util.*;

public class Kruskal {
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

    static class UnionFind {
        private final Map<String, String> parent;
        private final Map<String, Integer> rank;

        public UnionFind(List<String> nodes) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            for (String node : nodes) {
                parent.put(node, node);
                rank.put(node, 0);
            }
        }

        public String find(String node, int[] ops) {
            if (!parent.get(node).equals(node)) {
                parent.put(node, find(parent.get(node), ops)); // Path compression
                ops[0]++;
            }
            ops[0]++;
            return parent.get(node);
        }

        public void union(String node1, String node2, int[] ops) {
            String root1 = find(node1, ops);
            String root2 = find(node2, ops);
            if (root1.equals(root2)) return;

            ops[0]++;
            if (rank.get(root1) < rank.get(root2)) {
                parent.put(root1, root2);
            } else if (rank.get(root1) > rank.get(root2)) {
                parent.put(root2, root1);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank.get(root1) + 1);
            }
        }
    }

    public static Result findMST(Graph graph) {
        long startTime = System.nanoTime();
        int[] operations = {0};

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        edges.sort(Comparator.naturalOrder());
        operations[0] += edges.size() * (int) Math.log(edges.size()); // Approximate sorting cost

        UnionFind uf = new UnionFind(graph.getNodes());
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;

        for (Edge edge : edges) {
            String rootFrom = uf.find(edge.from, operations);
            String rootTo = uf.find(edge.to, operations);
            if (!rootFrom.equals(rootTo)) {
                uf.union(edge.from, edge.to, operations);
                mstEdges.add(edge);
                totalCost += edge.weight;
            }
        }

        // Check if MST is valid (connected graph)
        String root = uf.find(graph.getNodes().get(0), operations);
        boolean connected = true;
        for (String node : graph.getNodes()) {
            if (!uf.find(node, operations).equals(root)) {
                connected = false;
                break;
            }
        }

        if (!connected || mstEdges.size() != graph.getNodes().size() - 1) {
            return new Result(new ArrayList<>(), -1, operations[0], 0);
        }

        double executionTimeMs = (System.nanoTime() - startTime) / 1_000_000.0;
        return new Result(mstEdges, totalCost, operations[0], executionTimeMs);
    }
}