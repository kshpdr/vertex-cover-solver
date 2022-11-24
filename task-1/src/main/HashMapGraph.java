package main;

import java.util.*;

public class HashMapGraph {
    public HashSet<Vertex> vertices = new HashSet<>();
    public HashMap<Vertex, HashSet<Vertex>> edges = new HashMap<>();
    public List<Edge> listEdges = new ArrayList<>();

    public HashSet<Vertex> red = new HashSet<>();
    public HashSet<Vertex> blue = new HashSet<>();

    public HashMapGraph(List<Edge> edges){
        this.vertices = getVerticesFromEdges(edges);
        this.edges = getHashmapFromList(edges);
        this.listEdges = edges;
    }

    public HashMapGraph(HashMap<Vertex, HashSet<Vertex>> edges){
        this.vertices = new HashSet<>(edges.keySet());
        this.edges = edges;
    }

    public HashMapGraph(HashMapGraph graphToCopy){
        this(copy(graphToCopy.getEdges()));
    }


    public Edge getRandomEdge(){
        Edge randomEdge = null;
        while (randomEdge == null){
            Vertex rndVertex = getRandomVertex();
            if (edges.containsKey(rndVertex)){
                if (!edges.get(rndVertex).isEmpty()) {
                    HashSet<Vertex> adjacentVertices = edges.get(rndVertex);
                    Vertex adjacentVertex = (new ArrayList<>(adjacentVertices)).get(0);
                    randomEdge = new Edge(rndVertex, adjacentVertex);
                }
            }
        }
        return randomEdge;
    }

    public Vertex getRandomVertex(){
        int rnd = new Random().nextInt(vertices.size());
        return (new ArrayList<>(vertices)).get(rnd);
    }

    public HashSet<Vertex> getVerticesFromEdges(List<Edge> edges){
        HashSet<Vertex> vertices = new HashSet<>();
        for (Edge edge : edges){
            vertices.add(edge.getFirstVertex());
            vertices.add(edge.getSecondVertex());
        }
        return vertices;
    }

    public HashMap<Vertex, HashSet<Vertex>> getHashmapFromList(List<Edge> edges){
        HashMap<Vertex, HashSet<Vertex>> mapEdges = new HashMap<>();

        for (Edge edge : edges){
            if (!mapEdges.containsKey(edge.getFirstVertex())){
                HashSet<Vertex> adjacentList = new HashSet<>();
                adjacentList.add(edge.getSecondVertex());
                mapEdges.put(edge.getFirstVertex(), adjacentList);
            }
            else{
                mapEdges.get(edge.getFirstVertex()).add(edge.getSecondVertex());
            }

            if (!mapEdges.containsKey(edge.getSecondVertex())){
                HashSet<Vertex> adjacentList = new HashSet<>();
                adjacentList.add(edge.getFirstVertex());
                mapEdges.put(edge.getSecondVertex(), adjacentList);
            }
            else{
                mapEdges.get(edge.getSecondVertex()).add(edge.getFirstVertex());
            }
        }

        return mapEdges;
    }

    public boolean hasEdges(){
        return !edges.isEmpty();
    }

    public HashMapGraph deleteVertex(Vertex vertexToDelete){
        vertices.remove(vertexToDelete);
        for (Vertex vertex : edges.get(vertexToDelete)){
            edges.get(vertex).remove(vertexToDelete);
        }
        edges.remove(vertexToDelete);
        return this;
    }

    public void deleteEmptyAdjacentLists(){
        HashSet<Vertex> verticesToDelete = new HashSet<>();
        for (Vertex vertex : this.edges.keySet()){
            if (edges.get(vertex).isEmpty()){
                verticesToDelete.add(vertex);
            }
        }
        for (Vertex vertex : verticesToDelete){
            edges.remove(vertex);
        }
    }

    public HashMap<Vertex, HashSet<Vertex>> getEdges(){
        return edges;
    }

    public List<Edge> getListEdges() {
        return listEdges;
    }

    public HashSet<Vertex> getAdjacentVertices(Vertex vertex){
        return edges.get(vertex);
    }

    public HashSet<Vertex> getVertices(){
        return vertices;
    }

    public static HashMap<Vertex, HashSet<Vertex>> copy(
            HashMap<Vertex, HashSet<Vertex>> original)
    {
        HashMap<Vertex, HashSet<Vertex>> copy = new HashMap<Vertex, HashSet<Vertex>>();
        for (Map.Entry<Vertex, HashSet<Vertex>> entry : original.entrySet())
        {
            copy.put(entry.getKey(),
                    // Or whatever List implementation you'd like here.
                    new HashSet<Vertex>(entry.getValue()));
        }
        return copy;
    }

    public void addVertex(Vertex vertex){
        vertices.add(vertex);
    }

    public boolean isBipartite(){
        Vertex randomVertex = vertices.iterator().next();
        randomVertex.color = 1;

        Stack<Vertex> vertices = new Stack<>();
        vertices.push(randomVertex);

        while (!vertices.isEmpty()){
            Vertex vertex = vertices.pop();
            for (Vertex neighbour : getEdges().get(vertex)){
                if (neighbour.color == 0){
                    if (vertex.color == 1){
                        neighbour.color = 2;
                    }
                    else {
                        neighbour.color = 1;
                    }
                    vertices.push(neighbour);
                }
                else {
                    if (vertex.color == neighbour.color){
                        return false;
                    }
                }
            }
        }
        for (Vertex vertex : this.vertices){
            if (vertex.color == 1){
                red.add(vertex);
            }
            else if (vertex.color == 2){
                blue.add(vertex);
            }
        }
        return true;
    }

    public void addEdges(Vertex vertexToAdd, HashSet<Vertex> edgesToAdd){
        vertices.add(vertexToAdd);
        edges.put(vertexToAdd, edgesToAdd);
        for (Vertex vertex : edgesToAdd){
            if (edges.containsKey(vertex)){
                edges.get(vertex).add(vertexToAdd);
            }
            else{
                HashSet<Vertex> hashSetToAdd = new HashSet<Vertex>();
                hashSetToAdd.add(vertexToAdd);
                edges.put(vertex, hashSetToAdd);
            }
        }
    }

}
