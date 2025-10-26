package graph;

public class Edge implements Comparable<Edge> {
    public final String from;
    public final String to;
    public final int weight;

    public Edge(String from, String to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        int weightComparison = Integer.compare(this.weight, other.weight);
        if (weightComparison != 0) {
            return weightComparison;
        }

        int fromComparison = this.from.compareTo(other.from);
        if (fromComparison != 0) {
            return fromComparison;
        }

        return this.to.compareTo(other.to);
    }

    @Override
    public String toString() {
        return String.format("{\"from\": \"%s\", \"to\": \"%s\", \"weight\": %d}", from, to, weight);
    }
}
