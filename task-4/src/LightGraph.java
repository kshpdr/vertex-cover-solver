import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LightGraph {
    // adjacency list to store the graph
    private final Map<Vertex, ArrayList<Vertex>> adjList;

    public LightGraph() {
        // initialize the adjacency list
        adjList = new HashMap<>();
    }

    // method to add a vertex to the graph
    public void addVertex(Vertex vertex) {
        adjList.putIfAbsent(vertex, new ArrayList<>());
    }

    // method to add an edge to the graph
    public void addEdge(Vertex source, Vertex destination) {
        addVertex(source);
        addVertex(destination);
        adjList.get(source).add(destination);
        adjList.get(destination).add(source);
    }

    // method to remove a vertex from the graph
    public void removeVertex(Vertex vertex) {
        // remove the vertex from the adjacency list
        adjList.remove(vertex);

        // remove all the edges to this vertex
        for (Vertex v : adjList.keySet()) {
            adjList.get(v).remove(vertex);
        }
    }

    // method to get the vertices of the graph
    public Map<Vertex, ArrayList<Vertex>> getAdjVertices() {
        return adjList;
    }

//    public HashSet<Vertex> getVertices(){
//        return adjList.keySet();
//    }

    public Vertex getVertexWithGreaterGainFromRandomEdge(HashSet<Vertex> solution, Vertex v){
        for (Vertex vertex : adjList.keySet()){
            for (Vertex neighbor : adjList.get(vertex)){
                if (!solution.contains(vertex) && !solution.contains(neighbor)) {
                    if (vertex.equals(v)) {
                        return neighbor;
                    } else if (neighbor.equals(v)) {
                        return vertex;
                    } else {
                        if (vertex.gain > neighbor.gain) {
                            return vertex;
                        } else {
                            return neighbor;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Vertex getVertexWithGreaterGain(HashSet<Vertex> solution){
        for (Vertex vertex : getAdjVertices().keySet()){
            for (Vertex neighbor : getAdjVertices().get(vertex)){
                if (!solution.contains(vertex) && !solution.contains(neighbor)){
                    if (vertex.gain > neighbor.gain){
                        return vertex;
                    }
                    else {
                        return neighbor;
                    }
                }
            }
        }
        return null;
    }

    // method to check if the given set of vertices is a vertex cover
    // a vertex cover is a set of vertices that includes at least one end of each edge in the graph
    public boolean isVertexCover(HashSet<Vertex> vertices) {
        // go through all the edges and check if at least one of the endpoints is in the set of vertices
        for (Vertex source : adjList.keySet()) {
            for (Vertex destination : adjList.get(source)) {
                if (!vertices.contains(source) && !vertices.contains(destination)) {
                    // if the set of vertices does not contain either endpoint, it is not a vertex cover
                    return false;
                }
            }
        }

        // if the set of vertices includes at least one endpoint of each edge, it is a vertex cover
        return true;
    }
}
