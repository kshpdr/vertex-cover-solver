package main;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HashMapGraph {
    public HashSet<Vertex> vertices = new HashSet<>();
    public HashMap<Vertex, HashSet<Vertex>> edges = new HashMap<>();

    public HashMapGraph(List<Edge> edges){
        this.vertices = getVerticesFromEdges(edges);
        this.edges = getHashmapFromList(edges);
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
            if (!edges.get(rndVertex).isEmpty()){
                HashSet<Vertex> adjacentVertices = edges.get(rndVertex);
                Vertex adjacentVertex = (new ArrayList<>(adjacentVertices)).get(0);
                randomEdge = new Edge(rndVertex, adjacentVertex);
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
        for (Vertex vertex : vertices){
            if (!edges.get(vertex).isEmpty()){
                return true;
            }
        }
        return false;
    }

    public HashMapGraph deleteVertex(Vertex vertexToDelete){
        vertices.remove(vertexToDelete);
        for (Vertex vertex : edges.get(vertexToDelete)){
            edges.get(vertex).remove(vertexToDelete);
        }
        edges.remove(vertexToDelete);
        return this;
    }

    public HashMap<Vertex, HashSet<Vertex>> getEdges(){
        return edges;
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

    public void addEdges(Vertex vertexToAdd, HashSet<Vertex> edgesToAdd){
        if (!vertices.contains(vertexToAdd)){
            vertices.add(vertexToAdd);
        }
        edges.put(vertexToAdd, edgesToAdd);
        for (Vertex vertex : edgesToAdd){
            edges.get(vertex).add(vertexToAdd);
        }
    }

}
