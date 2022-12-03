import java.util.*;

public class Graph {
    private int verticesAmount = 0;
    private int edgesAmount = 0;

    private final Map<Vertex, HashSet<Vertex>> adjVertices = new HashMap<>();
    private final ArrayList<Vertex> arrayVertex = new ArrayList<>();

    private final VertexDegreeOrder degreeOrder = new VertexDegreeOrder();
    private final ArrayList<Edge> listEdges = new ArrayList<>();

    public Graph(int verticesAmount, int edgesAmount, HashSet<String[]> edges) {
        this.verticesAmount = verticesAmount;
        this.edgesAmount = edgesAmount;

        for (String[] edge : edges) {
            Vertex vertex1 = new Vertex(edge[0], Integer.parseInt(edge[0]));
            Vertex vertex2 = new Vertex(edge[1], Integer.parseInt(edge[1]));

            if (!this.adjVertices.containsKey(vertex1)){
                this.adjVertices.put(vertex1, new HashSet<>());
                this.arrayVertex.add(vertex1);
                this.degreeOrder.addVertex(vertex1);
            }

            if (!this.adjVertices.containsKey(vertex2)){
                this.adjVertices.put(vertex2, new HashSet<>());
                this.arrayVertex.add(vertex2);
                this.degreeOrder.addVertex(vertex2);
            }

            this.adjVertices.get(vertex1).add(vertex2);
            this.adjVertices.get(vertex2).add(vertex1);

            // increasing degrees of vertices
            vertex1.degree++;
            vertex2.degree++;
            this.degreeOrder.increaseDegreeOfVertex(vertex1, 1);
            this.degreeOrder.increaseDegreeOfVertex(vertex2, 1);
            this.listEdges.add(new Edge(vertex1, vertex2));
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
        vertexToRemove.active = false;
        HashSet<Vertex> adjacentVertices = new HashSet<>();
        this.degreeOrder.removeVertex(vertexToRemove);
        Iterator<Vertex> iterator = this.adjVertices.keySet().iterator();
        while (iterator.hasNext()) {
            Vertex tmpVertex = iterator.next();
            if (this.adjVertices.get(tmpVertex).remove(vertexToRemove)) {
                listEdges.remove(new Edge(vertexToRemove, tmpVertex));
                tmpVertex.degree--;
                this.degreeOrder.decreaseDegreeOfVertex(tmpVertex, 1);
                adjacentVertices.add(tmpVertex);
            }
            if (this.adjVertices.get(tmpVertex).isEmpty()) {
                iterator.remove();
                this.degreeOrder.removeVertex(tmpVertex);
                tmpVertex.active=false;
            }
        }

        if (adjVertices.containsKey(vertexToRemove)) {
            adjacentVertices.addAll(adjVertices.get(vertexToRemove));
            vertexToRemove.degree = 0;
            adjVertices.remove(vertexToRemove);
            vertexToRemove.active=false;
        }

        verticesAmount--;
        edgesAmount -= adjacentVertices.size();

        return adjacentVertices;
    }

    void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors) {
        originalVertex.active = true;
        if (!adjVertices.containsKey(originalVertex))
            adjVertices.put(originalVertex, new HashSet<>());
        for (Vertex neighbor : neighbors) {
            listEdges.add(new Edge(originalVertex, neighbor));
            adjVertices.get(originalVertex).add(neighbor);
            originalVertex.degree++;
            // this.degreeOrder.increaseDegreeOfVertex(this.arrayVertex.get(originalVertex.label),1);

            if (!adjVertices.containsKey(neighbor)){
                adjVertices.put(neighbor, new HashSet<>());
                neighbor.active=true;
            }
            adjVertices.get(neighbor).add(originalVertex);
            neighbor.degree++;
            this.degreeOrder.increaseDegreeOfVertex(neighbor, 1);
        }
        this.degreeOrder.putBack(originalVertex, neighbors.size());

        verticesAmount++;
        edgesAmount += neighbors.size();
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
        return this.degreeOrder.getVertexWithMaxDegree();
    }


    Graph getCopy() {
        Graph copy = new Graph();
        for (Vertex vertex : this.arrayVertex) {
            Vertex vertexCopy = new Vertex(vertex.name, vertex.id);
            vertexCopy.degree = vertex.degree;
            copy.arrayVertex.add(vertexCopy);
            copy.degreeOrder.addVertex(vertex);
        }

        for (Vertex vertex : this.adjVertices.keySet()) {
            for (Vertex neighbor : this.adjVertices.get(vertex)) {
                if (copy.adjVertices.containsKey(vertex)) {
                    copy.adjVertices.get(vertex).add(neighbor);
                } else {
                    HashSet<Vertex> tmpNeighbors = new HashSet<>();
                    tmpNeighbors.add(neighbor);
                    copy.adjVertices.put(vertex, tmpNeighbors);
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
            if (!clique.contains(vertex) && this.adjVertices.get(vertex).containsAll(clique) ) {
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

    public int getMaxLowerBound(boolean cliqueBound, boolean lpBound) {
        if (cliqueBound && lpBound){
            return Math.max(this.getCliqueLowerBound(), this.getLpBound());
        }
        else if (!cliqueBound && lpBound) {
            return this.getLpBound();
        }
        else if (cliqueBound) {
            return this.getCliqueLowerBound();
        }
        else {
            return 0;
        }
    }

    public HashMap<Vertex,HashSet<Vertex>> applyHighDegreeRule(int k){
        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
        HashSet<Vertex> verticesToDelete = new HashSet<>();
        for (Vertex vertex : adjVertices.keySet()){
            if (adjVertices.get(vertex).size() > k){
                edges.put(vertex, adjVertices.get(vertex));
                verticesToDelete.add(vertex);
            }
        }
        removeSetofVertices(verticesToDelete);
        return edges;
    }

    public HashMap<Vertex,HashSet<Vertex>> applyZeroDegreeRule(){
        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
        HashSet<Vertex> verticesToDelete = new HashSet<>();
        for (Vertex vertex : adjVertices.keySet()){
            if (adjVertices.get(vertex).size() == 0){
                edges.put(vertex, adjVertices.get(vertex));
                verticesToDelete.add(vertex);
            }
        }
        removeSetofVertices(verticesToDelete);
        return edges;
    }

    public boolean applyBussRule(int k){
        if (verticesAmount > (k*k + k) || edgesAmount > k*k){
            return false;
        }
        return true;
    }

    public HashMap<Vertex,HashSet<Vertex>> applyDominationRule(){
        HashMap<Vertex,HashSet<Vertex>> verticesInVertexCover = new HashMap<>();
        while(true){
            boolean reduced = false;
            for (Vertex currentVertex : this.arrayVertex) {
                if (currentVertex.active) {
                    boolean delete = false;
                    for (Vertex n : this.adjVertices.get(currentVertex)) {
                        HashSet<Vertex> tmpNeighbors = new HashSet<>(this.adjVertices.get(currentVertex));
                        tmpNeighbors.add(currentVertex);
                        if (tmpNeighbors.containsAll(this.adjVertices.get(n))) {
                            delete = true;
                            reduced = true;
                            break;
                        }
                    }
                    if (delete) {
                        verticesInVertexCover.put(currentVertex, this.removeVertex(currentVertex));
                    }
                }
            }
            if(!reduced) break;
            }
        return verticesInVertexCover;
        }
}
