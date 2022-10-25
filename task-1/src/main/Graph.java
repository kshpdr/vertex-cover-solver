package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Graph {
    public List<Vertex> vertices = new ArrayList<>();
    public List<Edge> edges = new ArrayList<>();

    public Graph(List<Edge> edges){
        this.vertices = getVerticesFromEdges(edges);
        this.edges = edges;
    }

    public Graph(Graph graphToCopy){
        this(new ArrayList<Edge>(graphToCopy.getEdges()));
    }

    public boolean hasEdges(){
        return edges.size() != 0;
    }

    public Edge getRandomEdge(){
        int rnd = new Random().nextInt(edges.size());
        return edges.get(rnd);
    }

    public ArrayList<Vertex> getVerticesFromEdges(List<Edge> edges){
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (Edge edge : edges){
            if (!vertices.contains(edge.getFirstVertex())) {
                vertices.add(edge.getFirstVertex());
            }
            if (!vertices.contains(edge.getSecondVertex())) {
                vertices.add(edge.getSecondVertex());
            }
        }
        return vertices;
    }

    public Graph deleteVertex(Vertex vertexToDelete){
        vertices.remove(vertexToDelete);
        edges.removeIf(edge -> edge.getFirstVertex().equals(vertexToDelete) || edge.getSecondVertex().equals(vertexToDelete));
        return this;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "vertices=" + vertices +
                ", edges=" + edges +
                '}';
    }
}
