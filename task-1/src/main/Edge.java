package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Edge {
    public final Vertex v;
    public final Vertex w;

    public Edge(Vertex v, Vertex w){
        this.v = v;
        this.w = w;
    }

    public Vertex getFirstVertex() {
        return v;
    }

    public Vertex getSecondVertex() {
        return w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(v, edge.v) && Objects.equals(w, edge.w);
    }

    @Override
    public int hashCode() {
        return Objects.hash(v, w);
    }

    @Override
    public String toString() {
        return "Edge{" +
                v + "-" + w +
                '}';
    }
}
