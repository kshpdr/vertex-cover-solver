import java.util.Objects;

public class Edge {
    public Vertex from;
    public Vertex to;
    public int capacity;
    public int flow;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    public Edge(Vertex from, Vertex to, int capacity, int flow){
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = flow;
    }

    @Override
    public String toString() {
        return from.name + " -" + capacity + "-> " + to.name;
    }
}
