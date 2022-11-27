import java.util.*;

public class Graph {
    private final HashMap<Vertex, HashSet<Vertex>> adjacentMap = new HashMap<>();
    private final ArrayList<Edge> listEdges = new ArrayList<>();

    public Graph(HashSet<String[]> edges) {
        int id = 0;
        for (String[] edge : edges) {
            Vertex vertex1 = new Vertex(id++, edge[0]);
            this.adjacentMap.put(vertex1, new HashSet<>());

            Vertex vertex2 = new Vertex(id++, edge[1]);
            this.adjacentMap.put(vertex2, new HashSet<>());

            this.adjacentMap.get(vertex1).add(vertex2);
            this.adjacentMap.get(vertex2).add(vertex1);

            this.listEdges.add(new Edge(vertex1, vertex1));
        }

    }
    public Graph() {}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex vertex : this.adjacentMap.keySet()) {
            sb.append(vertex).append(": ");
            for (Vertex adjacentVertex : this.adjacentMap.get(vertex)) {
                sb.append(adjacentVertex).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean isEmpty() {
        for (Vertex vertex : this.adjacentMap.keySet()) {
            if (!this.adjacentMap.get(vertex).isEmpty()){
                return false;
            }
        }
        return true;
    }

    // remove vertex and return all his neigbours
    public HashSet<Vertex> removeVertex(Vertex vertexToRemove) {
        HashSet<Vertex> adjacentVertices = new HashSet<>();
        Iterator<Vertex> iterator = this.adjacentMap.keySet().iterator();

        while (iterator.hasNext()) {
            Vertex adjacentVertex = iterator.next();
            this.adjacentMap.get(adjacentVertex).remove(vertexToRemove);
            adjacentVertices.add(adjacentVertex);
            if (this.adjacentMap.get(adjacentVertex).isEmpty()) {
                iterator.remove();
            }
        }

        adjacentMap.remove(vertexToRemove);
        return adjacentVertices;
    }

    public void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors) {
        if (!adjacentMap.containsKey(originalVertex))
            adjacentMap.put(originalVertex, neighbors);

        for (Vertex neighbor : neighbors) {
            if (!adjacentMap.containsKey(neighbor))
                adjacentMap.put(neighbor, new HashSet<>());
            adjacentMap.get(neighbor).add(originalVertex);
        }
    }

    public HashMap<Vertex, HashSet<Vertex>> removeVertices(HashSet<Vertex> verticesToRemove) {
        HashMap<Vertex, HashSet<Vertex>> adjacentVertices = new HashMap<>();

        Iterator<Vertex> iterator = verticesToRemove.iterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            adjacentVertices.put(vertex, this.removeVertex(vertex));
        }
        return adjacentVertices;
    }

    void putVertices(HashMap<Vertex, HashSet<Vertex>> verticesBack) {
        for (Map.Entry<Vertex, HashSet<Vertex>> entry : verticesBack.entrySet()) {
            putVertexBack(entry.getKey(), entry.getValue());
        }
    }

    public Vertex getNextNode() {
        Vertex maxDegreeVertex = null;
        for (Vertex vertex : adjacentMap.keySet()){
            if (maxDegreeVertex == null || adjacentMap.get(vertex).size() > adjacentMap.get(maxDegreeVertex).size()){
                maxDegreeVertex = vertex;
            }
        }
        return maxDegreeVertex;
    }

    public Graph getCopy() {
        Graph copy = new Graph();

        for (Vertex vertex : this.adjacentMap.keySet()) {
            HashSet<Vertex> neighbors = new HashSet<>();
            for (Vertex neighbor : this.adjacentMap.get(vertex)) {
                neighbors.add(new Vertex(neighbor.id, neighbor.name));
            }
            copy.adjacentMap.put(vertex, neighbors);
        }
        return copy;
    }

    public HashSet<Vertex> getMaximalCliqueFromVertex(Vertex firstVertex) {
        HashSet<Vertex> clique = new HashSet<>();
        clique.add(firstVertex);
        for (Vertex vertex : this.adjacentMap.keySet()) {
            if (!clique.contains(vertex)) {
                if (this.adjacentMap.containsKey(vertex) && this.adjacentMap.get(vertex).containsAll(clique)) {
                    clique.add(vertex);
                }
            }
        }
        return clique;

    }

    HashSet<HashSet<Vertex>> getApproximateMaximumCliqueCover() {
        Graph copyGraph = this.getCopy();
        HashSet<HashSet<Vertex>> cliqueCover = new HashSet<>();
        HashSet<Vertex> maxClique;

        while (!copyGraph.isEmpty()) {
            maxClique = copyGraph.getMaximalCliqueFromVertex(copyGraph.getNextNode());
            cliqueCover.add(maxClique);
            copyGraph.removeVertices(maxClique);
        }
        return cliqueCover;

    }

    public int getCliqueLowerBound() {
        return this.adjacentMap.keySet().size() - this.getApproximateMaximumCliqueCover().size();
    }

    public HashSet<Vertex> getVertices() {
        return (HashSet<Vertex>) this.adjacentMap.keySet();
    }

    public ArrayList<Edge> getListEdges() {
        return listEdges;
    }

    public int getLpBound() {
        BipartiteGraph bipartiteGraph = new BipartiteGraph(this);
        return (int) Math.ceil((double) bipartiteGraph.findMaximumMatchingSize() / 2);

    }

    public int getMaxLowerBound() {
        return Math.max(this.getCliqueLowerBound(), this.getLpBound());
    }
}
