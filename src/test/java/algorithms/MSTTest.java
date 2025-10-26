package algorithms;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class MSTTest {

    // Үлкен граф жасау функциясы
    private Graph createSmallGraph() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
                new Edge("A", "B", 1), new Edge("A", "C", 4),
                new Edge("B", "C", 2), new Edge("C", "D", 3),
                new Edge("B", "D", 5)
        );
        return new Graph(nodes, edges);
    }

    // Prim және Kruskal алгоритмдерінің бірдей шығынды екенін тексеретін тест
    @Test
    void primAndKruskalSameCost() {
        Graph g = createSmallGraph();
        var p = Prim.findMST(g);  // Prim алгоритмі арқылы MST табу
        var k = Kruskal.findMST(g);  // Kruskal алгоритмі арқылы MST табу

        // MST құнының тең екенін тексеру
        assertEquals(p.totalCost, k.totalCost);

        // Әрбір алгоритм үшін шеттер санының дұрыстығын тексеру (g.getVertexCount() - 1)
        assertEquals(g.getVertexCount() - 1, p.mstEdges.size());
        assertEquals(g.getVertexCount() - 1, k.mstEdges.size());
    }

    // Қосылмаған графтарды өңдеу
    @Test
    void disconnectedGraphHandled() {
        List<String> nodes = Arrays.asList("A", "B", "C");
        List<Edge> edges = Arrays.asList(new Edge("A", "B", 1));  // Бір ғана шет
        Graph g = new Graph(nodes, edges);

        // Қосылмаған граф үшін MST нәтижесінің -1 болатынын тексеру
        assertEquals(-1, Prim.findMST(g).totalCost);
        assertEquals(-1, Kruskal.findMST(g).totalCost);
    }

    // Алгоритмдердің орындалу уақытын және операциялар санын тексеру
    @Test
    void performanceMetricsValid() {
        Graph g = createSmallGraph();
        var p = Prim.findMST(g);

        // Алгоритмнің орындау уақытының және операциялар санының дұрыстығын тексеру
        assertTrue(p.executionTimeMs >= 0);
        assertTrue(p.operationsCount > 0);
    }
}
