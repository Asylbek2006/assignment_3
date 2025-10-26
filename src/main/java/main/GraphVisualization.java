package main;

import graph.Edge;
import graph.Graph;
import algorithms.Kruskal;
import algorithms.Prim;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GraphVisualization {
    public static void main(String[] args) {
        generateGraphImages();
        analyzeGraphPerformance();
    }

    private static void generateGraphImages() {
        System.out.println("Графтардың суреттерін жасау...");

        // Графтарды жасау
        Graph smallGraph = createSmallGraph();
        Graph mediumGraph = createMediumGraph();
        Graph largeGraph = createLargeGraph();

        // MST табу
        Prim.Result smallPrim = Prim.findMST(smallGraph);
        Prim.Result mediumPrim = Prim.findMST(mediumGraph);
        Prim.Result largePrim = Prim.findMST(largeGraph);

        // SVG суреттерін жасау
        generateSVG(smallGraph, smallPrim.mstEdges, "small_graph", "Кіші граф");
        generateSVG(mediumGraph, mediumPrim.mstEdges, "medium_graph", "Орташа граф");
        generateSVG(largeGraph, largePrim.mstEdges, "large_graph", "Үлкен граф");

        System.out.println("✓ 3 графтың суреттері жасалды:");
        System.out.println("  - small_graph.html");
        System.out.println("  - medium_graph.html");
        System.out.println("  - large_graph.html");
    }

    private static void generateSVG(Graph graph, List<Edge> mstEdges, String filename, String title) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename + ".html"))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("    <title>" + title + "</title>");
            writer.println("    <style>");
            writer.println("        .container { margin: 20px; font-family: Arial, sans-serif; }");
            writer.println("        .graph { border: 1px solid #ccc; padding: 20px; margin: 10px 0; }");
            writer.println("        .node { fill: #4CAF50; stroke: #388E3C; stroke-width: 2; }");
            writer.println("        .edge { stroke: #2196F3; stroke-width: 2; }");
            writer.println("        .mst-edge { stroke: #FF5722; stroke-width: 3; }");
            writer.println("        .label { font-size: 12px; text-anchor: middle; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("    <div class='container'>");
            writer.println("        <h1>" + title + "</h1>");
            writer.println("        <p>Төбелер: " + graph.getVertexCount() + ", Қырлар: " + graph.getEdgeCount() + "</p>");

            // Графты көрсету
            writer.println("        <div class='graph'>");
            writer.println("            <h3>Бастапқы граф</h3>");
            writer.println("            <svg width='600' height='400'>");

            // Төбелерді орналастыру - қырлардан төбелерді шығару
            List<String> nodes = extractNodesFromGraph(graph);
            int centerX = 300, centerY = 200;
            double radius = Math.min(150, 2000 / nodes.size()); // Өлшемге байланысты радиус

            for (int i = 0; i < nodes.size(); i++) {
                double angle = 2 * Math.PI * i / nodes.size();
                int x = (int)(centerX + radius * Math.cos(angle));
                int y = (int)(centerY + radius * Math.sin(angle));

                writer.println("                <circle class='node' cx='" + x + "' cy='" + y + "' r='20'/>");
                writer.println("                <text class='label' x='" + x + "' y='" + (y + 5) + "'>" + nodes.get(i) + "</text>");
            }

            // Қырларды сызу
            List<Edge> edges = getEdgesFromGraph(graph);
            for (Edge edge : edges) {
                int fromIndex = nodes.indexOf(edge.from);
                int toIndex = nodes.indexOf(edge.to);

                if (fromIndex != -1 && toIndex != -1) {
                    double fromAngle = 2 * Math.PI * fromIndex / nodes.size();
                    double toAngle = 2 * Math.PI * toIndex / nodes.size();

                    int fromX = (int)(centerX + radius * Math.cos(fromAngle));
                    int fromY = (int)(centerY + radius * Math.sin(fromAngle));
                    int toX = (int)(centerX + radius * Math.cos(toAngle));
                    int toY = (int)(centerY + radius * Math.sin(toAngle));

                    boolean isMST = isEdgeInMST(edge, mstEdges);
                    String edgeClass = isMST ? "mst-edge" : "edge";

                    writer.println("                <line class='" + edgeClass + "' x1='" + fromX + "' y1='" + fromY +
                            "' x2='" + toX + "' y2='" + toY + "'/>");
                    writer.println("                <text class='label' x='" + ((fromX + toX)/2) + "' y='" + ((fromY + toY)/2 - 5) +
                            "' fill='" + (isMST ? "#FF5722" : "#2196F3") + "'>" + edge.weight + "</text>");
                }
            }

            writer.println("            </svg>");
            writer.println("        </div>");

            // MST ақпараты
            writer.println("        <div>");
            writer.println("            <h3>Minimum Spanning Tree (MST)</h3>");
            writer.println("            <p><strong>Қызыл сызықтар</strong> - MST құрамындағы қырлар</p>");
            writer.println("            <p><strong>Көк сандар</strong> - қырлардың салмақтары</p>");

            // MST құны
            int mstCost = calculateMSTCost(mstEdges);
            writer.println("            <p><strong>MST жалпы құны: " + mstCost + "</strong></p>");
            writer.println("        </div>");

            writer.println("    </div>");
            writer.println("</body>");
            writer.println("</html>");
        } catch (Exception e) {
            System.err.println("SVG жасау қатесі: " + e.getMessage());
        }
    }

    // Graph класынан төбелерді алу - қырлардан шығару
    private static List<String> extractNodesFromGraph(Graph graph) {
        List<String> nodes = new ArrayList<>();
        List<Edge> edges = getEdgesFromGraph(graph);

        for (Edge edge : edges) {
            if (!nodes.contains(edge.from)) {
                nodes.add(edge.from);
            }
            if (!nodes.contains(edge.to)) {
                nodes.add(edge.to);
            }
        }
        return nodes;
    }

    // Graph класынан қырларды алу әдісі
    private static List<Edge> getEdgesFromGraph(Graph graph) {
        try {
            // Бұл әдіс Graph класыңызда бар болуы керек
            return graph.getEdges();
        } catch (Exception e) {
            System.err.println("getEdges() әдісі жоқ: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Қырдың MST-де бар-жоғын тексеру
    private static boolean isEdgeInMST(Edge edge, List<Edge> mstEdges) {
        for (Edge mstEdge : mstEdges) {
            if ((mstEdge.from.equals(edge.from) && mstEdge.to.equals(edge.to)) ||
                    (mstEdge.from.equals(edge.to) && mstEdge.to.equals(edge.from))) {
                return true;
            }
        }
        return false;
    }

    // MST жалпы құнын есептеу
    private static int calculateMSTCost(List<Edge> mstEdges) {
        int totalCost = 0;
        for (Edge edge : mstEdges) {
            totalCost += edge.weight;
        }
        return totalCost;
    }

    private static void analyzeGraphPerformance() {
        System.out.println("\nГрафтардың өнімділігін талдау");
        System.out.println("=============================");

        Graph smallGraph = createSmallGraph();
        Graph mediumGraph = createMediumGraph();
        Graph largeGraph = createLargeGraph();

        analyzeSingleGraph(smallGraph, "Кіші граф");
        analyzeSingleGraph(mediumGraph, "Орташа граф");
        analyzeSingleGraph(largeGraph, "Үлкен граф");
    }

    private static void analyzeSingleGraph(Graph graph, String name) {
        System.out.println("\nГраф: " + name);
        System.out.println("Төбелер: " + graph.getVertexCount() +
                ", Қырлар: " + graph.getEdgeCount());

        long primStartTime = System.nanoTime();
        Prim.Result primResult = Prim.findMST(graph);
        long primEndTime = System.nanoTime();
        double primTimeMs = (primEndTime - primStartTime) / 1_000_000.0;

        long kruskalStartTime = System.nanoTime();
        Kruskal.Result kruskalResult = Kruskal.findMST(graph);
        long kruskalEndTime = System.nanoTime();
        double kruskalTimeMs = (kruskalEndTime - kruskalStartTime) / 1_000_000.0;

        System.out.println("Prim алгоритмі:");
        System.out.println("  Құны: " + primResult.totalCost +
                ", Уақыт: " + String.format("%.3f", primTimeMs) + "ms");

        System.out.println("Kruskal алгоритмі:");
        System.out.println("  Құны: " + kruskalResult.totalCost +
                ", Уақыт: " + String.format("%.3f", kruskalTimeMs) + "ms");

        if (primTimeMs < kruskalTimeMs) {
            System.out.println("✓ Prim жылдамырақ: " +
                    String.format("%.3f", kruskalTimeMs - primTimeMs) + "ms");
        } else {
            System.out.println("✓ Kruskal жылдамырақ: " +
                    String.format("%.3f", primTimeMs - kruskalTimeMs) + "ms");
        }
    }

    private static Graph createSmallGraph() {
        List<String> nodes = List.of("A", "B", "C");
        List<Edge> edges = List.of(
                new Edge("A", "B", 1),
                new Edge("B", "C", 2),
                new Edge("C", "A", 3)
        );
        return new Graph(nodes, edges);
    }

    private static Graph createMediumGraph() {
        List<String> nodes = List.of("A", "B", "C", "D", "E");
        List<Edge> edges = List.of(
                new Edge("A", "B", 2),
                new Edge("A", "C", 3),
                new Edge("B", "D", 1),
                new Edge("C", "E", 4),
                new Edge("D", "E", 2)
        );
        return new Graph(nodes, edges);
    }

    private static Graph createLargeGraph() {
        List<String> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        for (char i = 'A'; i <= 'J'; i++) {
            nodes.add(String.valueOf(i));
        }

        edges.add(new Edge("A", "B", 1));
        edges.add(new Edge("B", "C", 2));
        edges.add(new Edge("C", "D", 1));
        edges.add(new Edge("D", "E", 3));
        edges.add(new Edge("E", "F", 2));
        edges.add(new Edge("F", "G", 1));
        edges.add(new Edge("G", "H", 4));
        edges.add(new Edge("H", "I", 2));
        edges.add(new Edge("I", "J", 1));

        return new Graph(nodes, edges);
    }
}