package main;

import algorithms.Kruskal;
import algorithms.Prim;
import com.google.gson.*;
import graph.Edge;
import graph.Graph;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            createDataDirectory();
            processGraphs();
        } catch (Exception e) {
            System.err.println("Басты қате: " + e.getMessage());
        }
    }

    private static void createDataDirectory() throws IOException {
        Files.createDirectories(Paths.get("data"));
    }

    private static void processGraphs() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String inputPath = "data/ass_3_input.json";
        String outputPath = "data/ass_3_output.json";
        String csvOutputPath = "data/algorithm_analysis.csv"; // data папкасына ауыстырдым

        if (!checkInputFileExists(inputPath)) {
            return;
        }

        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject inputJson = gson.fromJson(reader, JsonObject.class);
            JsonArray results = processAllGraphs(inputJson.getAsJsonArray("graphs"));
            saveResultsToFile(gson, outputPath, results);

            // CSV файлға жазу
            writeCSV(results, csvOutputPath);

            System.out.println("ass_3_output.json сәтті құрылды: " + outputPath);
            System.out.println("algorithm_analysis.csv сәтті құрылды: " + csvOutputPath);

        } catch (Exception e) {
            System.err.println("Графтарды өңдеу кезіндегі қате: " + e.getMessage());
        }
    }

    private static boolean checkInputFileExists(String inputPath) {
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.err.println("Қате: " + inputPath + " табылмады!");
            return false;
        }
        return true;
    }

    private static JsonArray processAllGraphs(JsonArray graphsArray) {
        JsonArray results = new JsonArray();

        for (JsonElement graphElem : graphsArray) {
            JsonObject graphObj = graphElem.getAsJsonObject();
            JsonObject resultObj = processSingleGraph(graphObj);
            if (resultObj != null) {
                results.add(resultObj);
            }
        }

        return results;
    }

    private static JsonObject processSingleGraph(JsonObject graphObj) {
        try {
            int graphId = graphObj.get("id").getAsInt();
            Graph graph = createGraphFromJson(graphObj);

            Prim.Result primResult = Prim.findMST(graph);
            Kruskal.Result kruskalResult = Kruskal.findMST(graph);

            return createResultObject(graphId, graph, primResult, kruskalResult);
        } catch (Exception e) {
            System.err.println("Графты өңдеу кезіндегі қате: " + e.getMessage());
            return null;
        }
    }

    private static Graph createGraphFromJson(JsonObject graphObj) {
        List<String> nodes = extractNodesFromJson(graphObj);
        List<Edge> edges = extractEdgesFromJson(graphObj);
        return new Graph(nodes, edges);
    }

    private static List<String> extractNodesFromJson(JsonObject graphObj) {
        List<String> nodes = new ArrayList<>();
        JsonArray nodesArray = graphObj.getAsJsonArray("nodes");

        for (JsonElement node : nodesArray) {
            nodes.add(node.getAsString());
        }

        return nodes;
    }

    private static List<Edge> extractEdgesFromJson(JsonObject graphObj) {
        List<Edge> edges = new ArrayList<>();
        JsonArray edgesArray = graphObj.getAsJsonArray("edges");

        for (JsonElement edgeElem : edgesArray) {
            JsonObject edgeObj = edgeElem.getAsJsonObject();
            String from = edgeObj.get("from").getAsString();
            String to = edgeObj.get("to").getAsString();
            int weight = edgeObj.get("weight").getAsInt();
            edges.add(new Edge(from, to, weight));
        }

        return edges;
    }

    private static JsonObject createResultObject(int graphId, Graph graph,
                                                 Prim.Result primResult,
                                                 Kruskal.Result kruskalResult) {
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("graph_id", graphId);
        resultObj.add("input_stats", createInputStats(graph));
        resultObj.add("prim", createPrimResult(primResult));
        resultObj.add("kruskal", createKruskalResult(kruskalResult));
        return resultObj;
    }

    private static JsonObject createInputStats(Graph graph) {
        JsonObject inputStats = new JsonObject();
        inputStats.addProperty("vertices", graph.getVertexCount());
        inputStats.addProperty("edges", graph.getEdgeCount());
        return inputStats;
    }

    private static JsonObject createPrimResult(Prim.Result primResult) {
        JsonObject primJson = new JsonObject();
        primJson.add("mst_edges", createEdgesArray(primResult.mstEdges));
        primJson.addProperty("total_cost", primResult.totalCost);
        primJson.addProperty("operations_count", primResult.operationsCount);
        primJson.addProperty("execution_time_ms", primResult.executionTimeMs);
        return primJson;
    }

    private static JsonObject createKruskalResult(Kruskal.Result kruskalResult) {
        JsonObject kruskalJson = new JsonObject();
        kruskalJson.add("mst_edges", createEdgesArray(kruskalResult.mstEdges));
        kruskalJson.addProperty("total_cost", kruskalResult.totalCost);
        kruskalJson.addProperty("operations_count", kruskalResult.operationsCount);
        kruskalJson.addProperty("execution_time_ms", kruskalResult.executionTimeMs);
        return kruskalJson;
    }

    private static JsonArray createEdgesArray(List<Edge> edges) {
        JsonArray edgesJson = new JsonArray();

        for (Edge edge : edges) {
            edgesJson.add(createEdgeObject(edge));
        }

        return edgesJson;
    }

    private static JsonObject createEdgeObject(Edge edge) {
        JsonObject edgeJson = new JsonObject();
        edgeJson.addProperty("from", edge.from);
        edgeJson.addProperty("to", edge.to);
        edgeJson.addProperty("weight", edge.weight);
        return edgeJson;
    }

    private static void saveResultsToFile(Gson gson, String outputPath, JsonArray results) throws IOException {
        JsonObject outputJson = new JsonObject();
        outputJson.add("results", results);

        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(outputJson, writer);
        }
    }


    private static void writeCSV(JsonArray results, String csvOutputPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvOutputPath))) {

            writer.println("GraphID,Algorithm,Vertices,Edges,TotalCost,OperationsCount,ExecutionTimeMs");

            for (JsonElement resultElem : results) {
                JsonObject resultObj = resultElem.getAsJsonObject();
                int graphId = resultObj.get("graph_id").getAsInt();
                JsonObject inputStats = resultObj.get("input_stats").getAsJsonObject();
                int vertices = inputStats.get("vertices").getAsInt();
                int edges = inputStats.get("edges").getAsInt();

                JsonObject primResult = resultObj.get("prim").getAsJsonObject();
                JsonObject kruskalResult = resultObj.get("kruskal").getAsJsonObject();


                writer.printf("%d,Prim,%d,%d,%d,%d,%.3f%n",
                        graphId,
                        vertices,
                        edges,
                        primResult.get("total_cost").getAsInt(),
                        primResult.get("operations_count").getAsInt(),
                        primResult.get("execution_time_ms").getAsDouble()
                );


                writer.printf("%d,Kruskal,%d,%d,%d,%d,%.3f%n",
                        graphId,
                        vertices,
                        edges,
                        kruskalResult.get("total_cost").getAsInt(),
                        kruskalResult.get("operations_count").getAsInt(),
                        kruskalResult.get("execution_time_ms").getAsDouble()
                );
            }
        } catch (IOException e) {
            System.err.println("CSV файлға жазу қатесі: " + e.getMessage());
        }
    }
}