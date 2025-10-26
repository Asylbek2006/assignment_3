package algorithms;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class MSTTest {

    private Graph createSmallGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1), new Edge("A", "C", 4),
                new Edge("B", "C", 2), new Edge("C", "D", 3),
                new Edge("B", "D", 5)
        );
        return new Graph(nodes, edges);
    }

    @Test
    void primAndKruskalSameCost() {
        Graph g = createSmallGraph();
        var p = Prim.findMST(g);
        var k = Kruskal.findMST(g);
        assertEquals(p.totalCost, k.totalCost);
        assertEquals(g.V() - 1, p.mstEdges.size());
        assertEquals(g.V() - 1, k.mstEdges.size());
    }

    @Test
    void disconnectedGraphHandled() {
        List<String> nodes = Arrays.asList("A", "B", "C");
        List<Edge> edges = Arrays.asList(new Edge("A", "B", 1));
        Graph g = new Graph(nodes, edges);
        assertEquals(-1, Prim.findMST(g).totalCost);
        assertEquals(-1, Kruskal.findMST(g).totalCost);
    }

    @Test
    void performanceMetricsValid() {
        Graph g = createSmallGraph();
        var p = Prim.findMST(g);
        assertTrue(p.executionTimeMs >= 0);
        assertTrue(p.operationsCount > 0);
    }
}