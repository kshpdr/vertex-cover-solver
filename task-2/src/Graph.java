import java.util.*;

public class Graph {
    private final Map<Vertex, HashSet<Vertex>> adjVertices = new HashMap<>();
    private final ArrayList<String> vertexStringMap = new ArrayList<>();
    private final ArrayList<Vertex> arrayVertex = new ArrayList<>();

    private final VertexDegreeOrder degreeOrder = new VertexDegreeOrder();
    private final ArrayList<Edge> listEdges = new ArrayList<>();



    public Graph(HashSet<String[]> edges) {
        int index = 0;

        Map<String, Vertex> stringVertexMap = new HashMap<>();

        for (String[] edge : edges) {

            if (!stringVertexMap.containsKey(edge[0])) {
                Vertex vertex1 = new Vertex(index);
                this.vertexStringMap.add(edge[0]);
                this.adjVertices.put(vertex1, new HashSet<>());
                stringVertexMap.put(edge[0], vertex1);
                this.arrayVertex.add(vertex1);
                this.degreeOrder.addVertex(vertex1);
                index++;
            }
            if (!stringVertexMap.containsKey(edge[1])) {
                Vertex vertex2 = new Vertex(index);
                this.vertexStringMap.add(edge[1]);
                stringVertexMap.put(edge[1], vertex2);
                this.adjVertices.put(vertex2, new HashSet<>());
                this.arrayVertex.add(vertex2);
                this.degreeOrder.addVertex(vertex2);
                index++;
            }

            this.adjVertices.get(stringVertexMap.get(edge[0])).add(stringVertexMap.get(edge[1]));

            this.adjVertices.get(stringVertexMap.get(edge[1])).add(stringVertexMap.get(edge[0]));

            // increasing degrees of vertices
            stringVertexMap.get(edge[0]).degree++;
            stringVertexMap.get(edge[1]).degree++;
            this.degreeOrder.increaseDegreeOfVertex(stringVertexMap.get(edge[0]), 1);
            this.degreeOrder.increaseDegreeOfVertex(stringVertexMap.get(edge[1]), 1);
            this.listEdges.add(new Edge(stringVertexMap.get(edge[0]), stringVertexMap.get(edge[1])));

        }

    }

    public Graph() {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // sb.append("Vertices: ").append("\n");
        for (Vertex vertex : this.arrayVertex) {
            sb.append(vertex).append(": ");
            if (this.adjVertices.containsKey(vertex)) {
                for (Vertex adjacentVertex : this.adjVertices.get(vertex)) {
                    sb.append(adjacentVertex).append(" ");
                }
            }

            sb.append("\n");

        }
        sb.append("Degrees of Vertices: ").append(this.degreeOrder);

        return sb.toString();
    }

    public boolean isEmpty() {
        for (Vertex vertex : this.adjVertices.keySet()) {

            if(!this.adjVertices.get(vertex).isEmpty()){
                return false;
            }

        }
        return true;
    }

    HashSet<Vertex> removeVertex(Vertex vertexToRemove) {

        this.arrayVertex.get(vertexToRemove.label).active = false;


        HashSet<Vertex> adjacentVertices = new HashSet<>();

        this.degreeOrder.removeVertex(this.arrayVertex.get(vertexToRemove.label));

        Iterator<Vertex> iterator = this.adjVertices.keySet().iterator();

        while (iterator.hasNext()) {
            Vertex tmpVertex = iterator.next();

            if (this.adjVertices.get(tmpVertex).remove(vertexToRemove)) {

                this.arrayVertex.get(tmpVertex.label).degree--;
                this.degreeOrder.decreaseDegreeOfVertex(this.arrayVertex.get(tmpVertex.label), 1);

                adjacentVertices.add(tmpVertex);

            }
            if (this.adjVertices.get(tmpVertex).isEmpty()) {
                iterator.remove();
                this.degreeOrder.removeVertex(tmpVertex);
            }
        }

        if (adjVertices.containsKey(vertexToRemove)) {

            adjacentVertices.addAll(adjVertices.get(vertexToRemove));
            this.arrayVertex.get(vertexToRemove.label).degree = 0;

            adjVertices.remove(vertexToRemove);
            this.arrayVertex.get(vertexToRemove.label).active=false;

        }

        return adjacentVertices;

    }

    String getVertexMapping(Vertex vertex) {
        return vertexStringMap.get(vertex.label);

    }

    void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors) {

        this.arrayVertex.get(originalVertex.label).active = true;

        if (!adjVertices.containsKey(originalVertex))
            adjVertices.put(originalVertex, new HashSet<>());

        for (Vertex neighbor : neighbors) {

            adjVertices.get(originalVertex).add(neighbor);
            this.arrayVertex.get(originalVertex.label).degree++;
            // this.degreeOrder.increaseDegreeOfVertex(this.arrayVertex.get(originalVertex.label),1);

            if (!adjVertices.containsKey(neighbor)){
                adjVertices.put(neighbor, new HashSet<>());
            }
            adjVertices.get(neighbor).add(originalVertex);
            this.arrayVertex.get(neighbor.label).degree++;
            this.degreeOrder.increaseDegreeOfVertex(this.arrayVertex.get(neighbor.label), 1);

        }

        this.degreeOrder.putBack(this.arrayVertex.get(originalVertex.label), neighbors.size());

    }

    HashMap<Vertex, HashSet<Vertex>> removeSetofVertices(HashSet<Vertex> verticesToRemove) {

        HashMap<Vertex, HashSet<Vertex>> results = new HashMap<>();

        Iterator<Vertex> it = verticesToRemove.iterator();
        Vertex tmpVertex;
        while (it.hasNext()) {
            tmpVertex = it.next();
            results.put(tmpVertex, this.removeVertex(tmpVertex));
        }
        return results;

    }

    void putManyVerticesBack(HashMap<Vertex, HashSet<Vertex>> verticesBack) {

        for (Map.Entry<Vertex, HashSet<Vertex>> entry : verticesBack.entrySet()) {
            putVertexBack(entry.getKey(), entry.getValue());
        }

    }

    Vertex getNextNode() {
        // System.out.println(this);
        return this.degreeOrder.getVertexWithMaxDegree();
    }


    Graph getCopy() {
        Graph copy = new Graph();
        //
        for (Vertex vertex : this.arrayVertex) {
            Vertex vertexCopy = new Vertex(vertex.label);
            vertexCopy.degree = vertex.degree;
            copy.arrayVertex.add(vertexCopy);
            copy.vertexStringMap.add(this.vertexStringMap.get(vertexCopy.label));
            copy.degreeOrder.addVertex(vertex);

        }

        for (Vertex vertex : this.adjVertices.keySet()) {
            for (Vertex neighbor : this.adjVertices.get(vertex)) {
                if (copy.adjVertices.containsKey(vertex)) {
                    copy.adjVertices.get(copy.arrayVertex.get(vertex.label)).add(copy.arrayVertex.get(neighbor.label));
                } else {
                    HashSet<Vertex> tmpNeighbors = new HashSet<>();
                    tmpNeighbors.add(copy.arrayVertex.get(neighbor.label));
                    copy.adjVertices.put(copy.arrayVertex.get(vertex.label), tmpNeighbors);

                }

            }

        }

        return copy;

    }

    HashSet<Vertex> getMaximalCliqueFromVertex(Vertex firstVertex) {
        HashSet<Vertex> clique = new HashSet<>();

        clique.add(firstVertex);
        ArrayList<Vertex> vertices = new ArrayList<>(this.adjVertices.keySet());
        Collections.shuffle(vertices);
        for (Vertex vertex : this.adjVertices.keySet()) {
            if (!clique.contains(vertex) && this.adjVertices.get(this.arrayVertex.get(vertex.label)).containsAll(clique) ) {


                    clique.add(vertex);

            }

        }

        return clique;

    }

    int getCliqueLowerBound() {
        Graph copyGraph = this.getCopy();
        HashSet<HashSet<Vertex>> result = new HashSet<>();
        HashSet<Vertex> maxClique;
        int usedVertices = 0;

        while (!copyGraph.degreeOrder.isEmpty()) {
            maxClique = copyGraph.getMaximalCliqueFromVertex(copyGraph.getNextNode());
            result.add(maxClique);
            usedVertices+=maxClique.size();

            copyGraph.removeSetofVertices(maxClique);

        }
        return this.adjVertices.size() - result.size()- (this.adjVertices.size()-usedVertices);

    }


    public ArrayList<Vertex> getVertices() {

        return this.arrayVertex;
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
