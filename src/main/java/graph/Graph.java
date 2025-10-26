package graph;

import java.util.*;

public class Graph {
    private final List<String> nodes;
    private final List<Edge> edges;
    private final Map<String, List<Edge>> adjacencyList;

    public Graph(List<String> nodes, List<Edge> edges) {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
        this.adjacencyList = new HashMap<>();
        for (String node : nodes) {
            adjacencyList.put(node, new ArrayList<>());
        }
        for (Edge edge : edges) {
            adjacencyList.get(edge.from).add(edge);
            adjacencyList.get(edge.to).add(new Edge(edge.to, edge.from, edge.weight)); // Undirected
        }
    }

    public List<String> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Map<String, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public int getVertexCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }
}