import java.util.*;

public class NewGraph {
    public HashSet<Vertex> vertices = new HashSet<>();
    public HashSet<Edge> edges = new HashSet<>();

    public NewGraph(HashSet<String[]> stringEdges) {
        for (String[] stringEdge : stringEdges) {
            Vertex vertex1 = new Vertex(stringEdge[0], Integer.parseInt(stringEdge[0]));
            Vertex vertex2 = new Vertex(stringEdge[1], Integer.parseInt(stringEdge[1]));
            vertices.add(vertex1);
            vertices.add(vertex2);
            edges.add(new Edge(vertex1, vertex2));
        }
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }

    public HashSet<Vertex> removeVertex(Vertex vertex) {
        HashSet<Vertex> neighbours = new HashSet<>();
        vertices.remove(vertex);
        HashSet<Edge> edgesToDelete = new HashSet<>();
        for (Edge edge : edges) {
            if (edge.contains(vertex)) {
                neighbours.add(edge.getAnotherVertex(vertex));
                edgesToDelete.add(edge);
            }
        }
        this.edges.removeAll(edgesToDelete);
        return neighbours;
    }

    public void putVertexBack(Vertex vertex, HashSet<Vertex> neighbors) {
        vertices.add(vertex);
        for (Vertex neighbour : neighbors) {
            edges.add(new Edge(vertex, neighbour));
        }
    }

    public HashMap<Vertex, HashSet<Vertex>> removeSetofVertices(HashSet<Vertex> vertices) {
        HashMap<Vertex, HashSet<Vertex>> verticesWithNeighbours = new HashMap<>();
        for (Vertex vertex : vertices) {
            HashSet<Vertex> neighbours = new HashSet<>();
            HashSet<Edge> edgesToDelete = new HashSet<>();
            for (Edge edge : this.edges) {
                if (edge.contains(vertex)) {
                    neighbours.add(edge.getAnotherVertex(vertex));
                    edgesToDelete.add(edge);
                }
            }
            this.edges.removeAll(edgesToDelete);
            verticesWithNeighbours.put(vertex, neighbours);
        }
        this.vertices.removeAll(vertices);
        return verticesWithNeighbours;
    }

    public void putManyVerticesBack(HashMap<Vertex, HashSet<Vertex>> vertices) {
        this.vertices.addAll(vertices.keySet());
        for (Vertex vertex : vertices.keySet()) {
            for (Vertex neighbour : vertices.get(vertex)) {
                edges.add(new Edge(vertex, neighbour));
            }
        }
    }

    public int getMaxLowerBound(boolean cliqueBound, boolean lpBound){
        return 0;
    }

    public Vertex getNextNode(){
        return vertices.iterator().next();
    }

    public HashSet<Vertex> getNeighbours(Vertex vertex){
        HashSet<Vertex> neighbours = new HashSet<>();
        for (Edge edge : edges){
            if (edge.contains(vertex)){
                neighbours.add(edge.getAnotherVertex(vertex));
            }
        }
        return neighbours;
    }
}