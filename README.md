# Assignment 3: Optimization of a City Transportation Network (Minimum Spanning Tree)

## Overview
This project implements **Prim's** and **Kruskal's** algorithms to find the Minimum Spanning Tree (MST) for a city transportation network, ensuring all districts are connected with minimal construction cost. The implementation is in **Java**, using a custom graph structure (`Graph.java`, `Edge.java`) for the **bonus section**. The project meets all requirements: input/output via JSON, automated JUnit tests, 30 datasets (10 small, 10 medium, 10 large), and graph visualizations.

## Repository Structure
- `pom.xml`: Maven configuration with Gson and JUnit 5.
- `data/ass_3_input.json`: 30 graphs (10 small: 4–6 vertices, 10 medium: 10–15 vertices, 10 large: 20–30 vertices).
- `data/ass_3_output.json`: Generated results (MST edges, total cost, operations count, execution time).
- `graph_images/`: 30 PNG images visualizing each graph (generated via Graphviz).
- `src/main/java/`: Source code (`Graph`, `Edge`, `Prim`, `Kruskal`, `Main`).
- `src/test/java/`: JUnit 5 tests for correctness and performance.

## Input Data
The input file `data/ass_3_input.json` contains 30 graphs:
- **Small**: 4–6 vertices, 5–10 edges (for correctness).
- **Medium**: 10–15 vertices, 15–30 edges (for moderate performance).
- **Large**: 20–30 vertices, 30–50 edges (for scalability).

Each graph has an `id`, `nodes`, and `edges` (with `from`, `to`, `weight`).

## Algorithm Implementation
- **Prim's Algorithm**: Uses a priority queue (O(E log V)). Operations counted: insert, extract-min, and edge additions.
- **Kruskal's Algorithm**: Uses Union-Find with path compression and rank (O(E log E)). Operations counted: sorting, find, union.
- Both handle disconnected graphs by returning `total_cost = -1`.

## Results
Results are saved in `data/ass_3_output.json`. Example summary (actual values depend on execution):

| Graph ID | Type   | Vertices | Edges | Prim Cost | Prim Time (ms) | Prim Ops | Kruskal Cost | Kruskal Time (ms) | Kruskal Ops |
|----------|--------|----------|-------|-----------|----------------|----------|--------------|-------------------|-------------|
| 1        | Small  | 5        | 7     | 16        | 0.12           | 15       | 16           | 0.09              | 35          |
| 11       | Medium | 11       | 15    | 147       | 0.56           | 45       | 147          | 0.43              | 55          |
| 21       | Large  | 26       | 39    | 325       | 1.23           | 120      | 325          | 1.11              | 150         |

## Comparison: Prim vs Kruskal
### Theoretical Analysis
- **Prim's**: O(E log V) using a binary heap. Efficient for dense graphs due to adjacency list traversal.
- **Kruskal's**: O(E log E) due to edge sorting. Better for sparse graphs, as it processes all edges upfront.

### Practical Observations
- **Small graphs**: Both algorithms are fast (<1ms). Kruskal slightly faster in sparse cases due to fewer operations in Union-Find vs. Prim's priority queue.
- **Medium graphs**: Kruskal performs better in sparse graphs (E ≈ V), while Prim is comparable.
- **Large graphs**: Prim may outperform in dense graphs (E >> V), but our datasets are relatively sparse, favoring Kruskal slightly.
- **Operations**: Kruskal has higher counts due to sorting, while Prim's counts are lower in dense graphs.

## Conclusions
- **Prim**: Preferred for dense graphs or when adjacency list representation is optimized.
- **Kruskal**: Better for sparse graphs due to efficient edge sorting and Union-Find.
- **Implementation Complexity**: Kruskal requires Union-Find (path compression + rank), while Prim relies on a priority queue. Both are manageable with Java’s built-in collections.
- Our datasets are sparse (E ≈ V or E < V²), so Kruskal often shows better practical performance.

## Bonus: Graph Design
- Implemented `Graph.java` and `Edge.java` for clean OOP design.
- Graph uses adjacency list (for Prim) and edge list (for Kruskal).
- Visualizations for all 30 graphs are in `graph_images/` (e.g., `graph_small_1.png`).
    - Example for Graph ID 1: ![Small Graph 1](graph_images/graph_small_1.png)

## Testing
JUnit 5 tests in `src/test/java/algorithms/MSTTest.java` verify:
- **Correctness**: Same total cost for Prim and Kruskal, V-1 edges, no cycles, connected components.
- **Performance**: Non-negative execution time and operation counts.
- **Edge Cases**: Disconnected graphs return -1 cost.

## How to Run
1. Clone the repository:
   ```bash
   git clone <repository-url>