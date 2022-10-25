package main;

import java.util.Objects;

public class Vertex {
    String v;

    public Vertex(String v){
        this.v = v;
    }

    @Override
    public String toString() {
        return v;
    }

    public String getVertex() {
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(v, vertex.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(v);
    }
}
