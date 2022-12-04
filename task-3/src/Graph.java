import java.util.*;

public class Graph {
    private int verticesAmount = 0;
    private int edgesAmount = 0;

    private final Map<Vertex, HashSet<Vertex>> adjVertices = new HashMap<>();
//    private final ArrayList<String> vertexStringMap = new ArrayList<>();
    private final ArrayList<Vertex> vertices = new ArrayList<>();

//    private final VertexDegreeOrder degreeOrder = new VertexDegreeOrder();
    private final ArrayList<Edge> edges = new ArrayList<>();

    public Graph(HashSet<String[]> edges) {
//        int index = 0;
//        Map<String, Vertex> stringVertexMap = new HashMap<>();

        for (String[] edge : edges) {
            Vertex vertex1 = new Vertex(edge[0], Integer.parseInt(edge[0]));
            Vertex vertex2 = new Vertex(edge[1], Integer.parseInt(edge[1]));

            if (!adjVertices.containsKey(vertex1)) {
//                this.vertexStringMap.add(edge[0]);
                this.adjVertices.put(vertex1, new HashSet<>());
//                stringVertexMap.put(edge[0], vertex1);
                this.vertices.add(vertex1);
//                this.degreeOrder.addVertex(vertex1);
//                index++;
            }
            if (!adjVertices.containsKey(vertex2)) {
//                this.vertexStringMap.add(edge[1]);
//                stringVertexMap.put(edge[1], vertex2);
                this.adjVertices.put(vertex2, new HashSet<>());
                this.vertices.add(vertex2);
//                this.degreeOrder.addVertex(vertex2);
//                index++;
            }

            this.adjVertices.get(vertex1).add(vertex2);

            this.adjVertices.get(vertex2).add(vertex1);

            // increasing degrees of vertices
            vertex1.degree++;
            vertex2.degree++;
//            this.degreeOrder.increaseDegreeOfVertex(stringVertexMap.get(edge[0]), 1);
//            this.degreeOrder.increaseDegreeOfVertex(stringVertexMap.get(edge[1]), 1);
            this.edges.add(new Edge(vertex1, vertex2));
        }


    }

    public Graph() {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // sb.append("Vertices: ").append("\n");
        for (Vertex vertex : this.vertices) {
            sb.append(vertex).append(": ");
            if (this.adjVertices.containsKey(vertex)) {
                for (Vertex adjacentVertex : this.adjVertices.get(vertex)) {
                    sb.append(adjacentVertex).append(" ");
                }
            }

            sb.append("\n");

        }
//        sb.append("Degrees of Vertices: ").append(this.degreeOrder);

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
//        this.degreeOrder.removeVertex(this.arrayVertex.get(vertexToRemove.label));
        Iterator<Vertex> iterator = this.adjVertices.keySet().iterator();
        while (iterator.hasNext()) {
            Vertex tmpVertex = iterator.next();
            if (this.adjVertices.get(tmpVertex).remove(vertexToRemove)) {
                tmpVertex.degree--;
//                this.degreeOrder.decreaseDegreeOfVertex(this.arrayVertex.get(tmpVertex.label), 1);
                adjacentVertices.add(tmpVertex);
            }
            if (this.adjVertices.get(tmpVertex).isEmpty()) {
                iterator.remove();
//                this.degreeOrder.removeVertex(tmpVertex);
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

//    String getVertexMapping(Vertex vertex) {
//        return vertexStringMap.get(vertex.label);
//    }

    void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors) {
        originalVertex.active = true;
        if (!adjVertices.containsKey(originalVertex))
            adjVertices.put(originalVertex, new HashSet<>());
        for (Vertex neighbor : neighbors) {
            adjVertices.get(originalVertex).add(neighbor);
            originalVertex.degree++;
//             this.degreeOrder.increaseDegreeOfVertex(this.arrayVertex.get(originalVertex.label),1);

            if (!adjVertices.containsKey(neighbor)) {
                adjVertices.put(neighbor, new HashSet<>());
                neighbor.active = true;
            }
            adjVertices.get(neighbor).add(originalVertex);
            neighbor.degree++;
//            this.degreeOrder.increaseDegreeOfVertex(this.arrayVertex.get(neighbor.label), 1);
        }
//        this.degreeOrder.putBack(this.arrayVertex.get(originalVertex.label), neighbors.size());

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
//        return this.degreeOrder.getVertexWithMaxDegree();
        Vertex maxDegreeVertex = adjVertices.keySet().iterator().next();
        for (Vertex vertex : adjVertices.keySet()){
            if (adjVertices.get(vertex).size() > adjVertices.get(maxDegreeVertex).size()){
                maxDegreeVertex = vertex;
            }
        }
        return maxDegreeVertex;
    }


    Graph getCopy() {
        Graph copy = new Graph();
        for (Vertex vertex : this.vertices) {
            Vertex vertexCopy = new Vertex(vertex.name, vertex.id);
            vertexCopy.degree = vertex.degree;
            copy.vertices.add(vertexCopy);
//            copy.vertexStringMap.add(this.vertexStringMap.get(vertexCopy.label));
//            copy.degreeOrder.addVertex(vertex);
        }

        for (Vertex vertex : this.adjVertices.keySet()) {
            for (Vertex neighbor : this.adjVertices.get(vertex)) {
                if (copy.adjVertices.containsKey(vertex)) {
                    copy.adjVertices.get(copy.vertices.get(copy.vertices.indexOf(vertex))).add(copy.vertices.get(copy.vertices.indexOf(neighbor)));
                } else {
                    HashSet<Vertex> tmpNeighbors = new HashSet<>();
                    tmpNeighbors.add(copy.vertices.get(copy.vertices.indexOf(neighbor)));
                    copy.adjVertices.put(copy.vertices.get(copy.vertices.indexOf(vertex)), tmpNeighbors);
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

//        while (!copyGraph.degreeOrder.isEmpty()) {
        while (!copyGraph.adjVertices.keySet().isEmpty()) {
            maxClique = copyGraph.getMaximalCliqueFromVertex(copyGraph.getNextNode());
            result.add(maxClique);
            usedVertices+=maxClique.size();

            copyGraph.removeSetofVertices(maxClique);

        }
        return this.adjVertices.size() - result.size()- (this.adjVertices.size()-usedVertices);

    }


    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    public ArrayList<Edge> getListEdges() {
        return edges;
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
        return verticesAmount <= (k * k + k) && edgesAmount <= k * k;
    }

    public HashMap<Vertex,HashSet<Vertex>> applyDominationRule(){
        HashMap<Vertex,HashSet<Vertex>> verticesInVertexCover = new HashMap<>();
        while(true){
            boolean reduced = false;
            for (Vertex currentVertex : this.vertices) {

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


        public Vertex findUForUnconfinedRule(Set<Vertex> possibleUnconfinedSet) {
                HashSet<Vertex> neighborhoodOfSet = new HashSet<>();
                HashSet<Vertex> neighborhoodWithOwnVertices = new HashSet<>();
                for (Vertex vertex: possibleUnconfinedSet){
                    neighborhoodOfSet.addAll(this.adjVertices.get(vertex));
                    neighborhoodWithOwnVertices.addAll(this.adjVertices.get(vertex));
                    neighborhoodWithOwnVertices.add(vertex);
                }

                if (neighborhoodOfSet.isEmpty()) return null;
                Vertex minVertex = null;
                int minSubtractionSeen = Integer.MAX_VALUE;

                for (Vertex u: neighborhoodOfSet){
                    Set<Vertex> intersection = new HashSet<>(this.adjVertices.get(u));
                    intersection.retainAll(possibleUnconfinedSet);
                    if(intersection.size()==1){
                        HashSet<Vertex> setSubtraction = new HashSet<>(this.adjVertices.get(u));
                        setSubtraction.removeAll(neighborhoodWithOwnVertices);
                        if(setSubtraction.size()<minSubtractionSeen){
                            minVertex = u;
                            minSubtractionSeen=setSubtraction.size();
                        }
                    }
                }

                return minVertex;
        }

        public boolean isUnconfined(Vertex v){
            HashSet<Vertex> unconfinedSet = new HashSet<>();
            unconfinedSet.add(v);
            while(true){
                Vertex u = this.findUForUnconfinedRule(unconfinedSet);
                if (u==null) return false;
                HashSet<Vertex> neighborhoodWithOwnVertices = new HashSet<>();
                for (Vertex vertex: unconfinedSet){
                    neighborhoodWithOwnVertices.addAll(this.adjVertices.get(vertex));
                    neighborhoodWithOwnVertices.add(vertex);
                }

                HashSet<Vertex> setSubtraction = new HashSet<>(this.adjVertices.get(u));
                setSubtraction.removeAll(neighborhoodWithOwnVertices);
                if(setSubtraction.isEmpty()) return true;
                if(setSubtraction.size()==1){
                    unconfinedSet.add(setSubtraction.iterator().next());
                }else{
                    break;
                }


            }
            return false;
        }

    public HashMap<Vertex,HashSet<Vertex>> applyUnconfinedRule(){
        HashMap<Vertex,HashSet<Vertex>> verticesInVertexCover = new HashMap<>();
        while(true){
            boolean reduced = false;
            for (Vertex currentVertex : this.vertices) {

                if (currentVertex.active && isUnconfined(currentVertex) ) {
                        verticesInVertexCover.put(currentVertex, this.removeVertex(currentVertex));
                        reduced = true;
                }

            }
            if(!reduced) break;
        }
        return verticesInVertexCover;
    }

}
