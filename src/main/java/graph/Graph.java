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

        // Түйіндер үшін көршілес тізімдер құру
        for (String node : nodes) {
            adjacencyList.put(node, new ArrayList<>()); // Әр түйін үшін жаңа бос тізім
        }

        // Қабырғаларды қосу
        for (Edge edge : edges) {
            // Түйіндер үшін көршілес тізімдерге қабырғаларды қосу
            if (!adjacencyList.containsKey(edge.from)) {
                adjacencyList.put(edge.from, new ArrayList<>());
            }
            adjacencyList.get(edge.from).add(edge);

            // Екі жақты граф үшін көршілес тізімдерге қабырғаларды қосу
            if (!adjacencyList.containsKey(edge.to)) {
                adjacencyList.put(edge.to, new ArrayList<>());
            }
            adjacencyList.get(edge.to).add(new Edge(edge.to, edge.from, edge.weight));
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
