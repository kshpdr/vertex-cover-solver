import java.util.*;

public class Graph {
    private final HashMap<Vertex, HashSet<Vertex>> adjacentMap = new HashMap<>();
    private final ArrayList<Edge> listEdges = new ArrayList<>();



    public Graph(HashSet<String[]> edges) {
        for (String[] edge : edges) {
            Vertex vertex1 = new Vertex(edge[0].hashCode(), edge[0]);

            if (!this.adjacentMap.containsKey(vertex1)){
                this.adjacentMap.put(vertex1, new HashSet<>());
            }


            Vertex vertex2 = new Vertex(edge[1].hashCode(), edge[1]);
            if (!this.adjacentMap.containsKey(vertex2)){
                this.adjacentMap.put(vertex2, new HashSet<>());
            }

            this.adjacentMap.get(vertex1).add(vertex2);
            this.adjacentMap.get(vertex2).add(vertex1);

            this.listEdges.add(new Edge(vertex1, vertex2));
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
                this.arrayVertex.get(tmpVertex.label).active=false;
            }
        }

        if (adjVertices.containsKey(vertexToRemove)) {

            adjacentVertices.addAll(adjVertices.get(vertexToRemove));
            this.arrayVertex.get(vertexToRemove.label).degree = 0;

            adjVertices.remove(vertexToRemove);
            this.arrayVertex.get(vertexToRemove.label).active=false;

        }
        adjacentMap.remove(vertexToRemove);
        return neighbors;
    }

//    public void deleteEmptyAdjacentLists(){
//        HashSet<Vertex> verticesToDelete = new HashSet<>();
//        for (Vertex vertex : adjacentMap.keySet()){
//            if (adjacentMap.get(vertex).isEmpty()){
//                verticesToDelete.add(vertex);
//            }
//        }
//        for (Vertex vertex : verticesToDelete){
//            adjacentMap.remove(vertex);
//        }
//    }

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
                this.arrayVertex.get(neighbor.label).active=true;
            }
            adjVertices.get(neighbor).add(originalVertex);
            this.arrayVertex.get(neighbor.label).degree++;
            this.degreeOrder.increaseDegreeOfVertex(this.arrayVertex.get(neighbor.label), 1);

        }
    }

    public HashMap<Vertex, HashSet<Vertex>> removeVertices(HashSet<Vertex> verticesToRemove) {
        HashMap<Vertex, HashSet<Vertex>> adjacentVertices = new HashMap<>();
        HashSet<Vertex> copy = new HashSet<>();
        for (Vertex vertexToRemove : verticesToRemove){
            copy.add(new Vertex(vertexToRemove.id, vertexToRemove.name));
        }

        for (Vertex vertex : copy) {
            if(this.adjacentMap.containsKey(vertex)) {
                adjacentVertices.put(vertex, this.removeVertex(vertex));
            }
        }
        return adjacentVertices;
    }

    void putVertices(HashMap<Vertex, HashSet<Vertex>> verticesBack) {
        for (Map.Entry<Vertex, HashSet<Vertex>> entry : verticesBack.entrySet()) {
            putVertexBack(entry.getKey(), entry.getValue());
        }
    }

    Vertex getNextNode() {
        // System.out.println(this);
        return this.degreeOrder.getVertexWithMaxDegree();
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
        HashSet<HashSet<Vertex>> cliqueCover = new HashSet<>();
        HashSet<Vertex> maxClique;
        int usedVertices = 0;

        while (!copyGraph.isEmpty()) {
            maxClique = copyGraph.getMaximalCliqueFromVertex(copyGraph.getNextNode());
            result.add(maxClique);
            usedVertices+=maxClique.size();

            copyGraph.removeSetofVertices(maxClique);

        }
        return this.adjVertices.size() - result.size()- (this.adjVertices.size()-usedVertices);

        return cliqueCover;
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
       // return this.getLpBound();
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
