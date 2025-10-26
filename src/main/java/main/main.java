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

public class main {
    public static void main(String[] args) {
        // data папкасын құру
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            System.err.println("data папкасын құру мүмкін емес: " + e.getMessage());
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String inputPath = "data/ass_3_input.json";
        String outputPath = "data/ass_3_output.json";

        // input файлдың бар-жоғын тексеру
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.err.println("Қате: " + inputPath + " табылмады!");
            System.err.println("data/ass_3_input.json файлыңызды қосыңыз.");
            return;
        }

        try (FileReader reader = new FileReader(inputPath)) {
            JsonObject inputJson = gson.fromJson(reader, JsonObject.class);
            JsonArray graphsArray = inputJson.getAsJsonArray("graphs");
            JsonArray results = new JsonArray();

            for (JsonElement graphElem : graphsArray) {
                JsonObject graphObj = graphElem.getAsJsonObject();
                int graphId = graphObj.get("id").getAsInt();

                // Nodes
                JsonArray nodesArray = graphObj.getAsJsonArray("nodes");
                List<String> nodes = new ArrayList<>();
                for (JsonElement node : nodesArray) {
                    nodes.add(node.getAsString());
                }

                // Edges
                JsonArray edgesArray = graphObj.getAsJsonArray("edges");
                List<Edge> edges = new ArrayList<>();
                for (JsonElement edgeElem : edgesArray) {
                    JsonObject edgeObj = edgeElem.getAsJsonObject();
                    String from = edgeObj.get("from").getAsString();
                    String to = edgeObj.get("to").getAsString();
                    int weight = edgeObj.get("weight").getAsInt();
                    edges.add(new Edge(from, to, weight));
                }

                Graph graph = new Graph(nodes, edges);

                // Prim
                Prim.Result primResult = Prim.findMST(graph);
                // Kruskal
                Kruskal.Result kruskalResult = Kruskal.findMST(graph);

                // Result object
                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("graph_id", graphId);

                JsonObject inputStats = new JsonObject();
                inputStats.addProperty("vertices", graph.getVertexCount());
                inputStats.addProperty("edges", graph.getEdgeCount());
                resultObj.add("input_stats", inputStats);

                // Prim
                JsonObject primJson = new JsonObject();
                JsonArray primEdgesJson = new JsonArray();
                for (Edge e : primResult.mstEdges) {
                    JsonObject eJson = new JsonObject();
                    eJson.addProperty("from", e.from);
                    eJson.addProperty("to", e.to);
                    eJson.addProperty("weight", e.weight);
                    primEdgesJson.add(eJson);
                }
                primJson.add("mst_edges", primEdgesJson);
                primJson.addProperty("total_cost", primResult.totalCost);
                primJson.addProperty("operations_count", primResult.operationsCount);
                primJson.addProperty("execution_time_ms", primResult.executionTimeMs);
                resultObj.add("prim", primJson);

                // Kruskal
                JsonObject kruskalJson = new JsonObject();
                JsonArray kruskalEdgesJson = new JsonArray();
                for (Edge e : kruskalResult.mstEdges) {
                    JsonObject eJson = new JsonObject();
                    eJson.addProperty("from", e.from);
                    eJson.addProperty("to", e.to);
                    eJson.addProperty("weight", e.weight);
                    kruskalEdgesJson.add(eJson);
                }
                kruskalJson.add("mst_edges", kruskalEdgesJson);
                kruskalJson.addProperty("total_cost", kruskalResult.totalCost);
                kruskalJson.addProperty("operations_count", kruskalResult.operationsCount);
                kruskalJson.addProperty("execution_time_ms", kruskalResult.executionTimeMs);
                resultObj.add("kruskal", kruskalJson);

                results.add(resultObj);
            }

            // Output
            JsonObject outputJson = new JsonObject();
            outputJson.add("results", results);

            try (FileWriter writer = new FileWriter(outputPath)) {
                gson.toJson(outputJson, writer);
            }

            System.out.println("ass_3_output.json сәтті құрылды: " + outputPath);

        } catch (Exception e) {
            System.err.println("Қате: " + e.getMessage());
            e.printStackTrace();
        }
    }
}