import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Graph {
    private final Map<Vertex, HashSet<Vertex>> adjVertices = new HashMap<>();
    private final HashSet<Vertex> vertices = new HashSet<>();
    private final HashSet<Edge> edges = new HashSet<>();

    public Graph(HashSet<String[]> edges) {

        for (String[] edge : edges) {
            Vertex vertex1 = new Vertex(edge[0], Integer.parseInt(edge[0]));
            Vertex vertex2 = new Vertex(edge[1], Integer.parseInt(edge[1]));

            if (!adjVertices.containsKey(vertex1)) {
                this.adjVertices.put(vertex1, new HashSet<>());
                this.vertices.add(vertex1);
            }
            if (!adjVertices.containsKey(vertex2)) {
                this.adjVertices.put(vertex2, new HashSet<>());
                this.vertices.add(vertex2);
            }

            this.adjVertices.get(vertex1).add(vertex2);

            this.adjVertices.get(vertex2).add(vertex1);

            // increasing degrees of vertices
            vertex1.degree++;
            vertex2.degree++;
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

    public void deleteEdgesWith(Vertex vertex){
        HashSet<Edge> edgesToRemove = new HashSet<>();
        for (Edge edge : edges){
            if (edge.contains(vertex)){
                edgesToRemove.add(edge);
            }
        }
        edges.removeAll(edgesToRemove);
    }

    public void addEdges(Vertex vertex, HashSet<Vertex> neighbors){
        for (Vertex neighbor : neighbors){
            edges.add(new Edge(vertex, neighbor));
        }
    }

    HashSet<Vertex> removeVertex(Vertex vertexToRemove) {
//        vertexToRemove.active = false;
        vertices.remove(vertexToRemove);
        this.deleteEdgesWith(vertexToRemove);
        HashSet<Vertex> adjacentVertices = new HashSet<>();
        Iterator<Vertex> iterator = this.adjVertices.keySet().iterator();
        while (iterator.hasNext()) {
            Vertex tmpVertex = iterator.next();
            if (this.adjVertices.get(tmpVertex).remove(vertexToRemove)) {
                tmpVertex.degree--;
                adjacentVertices.add(tmpVertex);
            }
            if (this.adjVertices.get(tmpVertex).isEmpty()) {
                iterator.remove();
//                tmpVertex.active=false;
                vertices.remove(tmpVertex);
                this.deleteEdgesWith(tmpVertex);
            }
        }

        if (adjVertices.containsKey(vertexToRemove)) {
            adjacentVertices.addAll(adjVertices.get(vertexToRemove));
            vertexToRemove.degree = 0;
            adjVertices.remove(vertexToRemove);
//            vertexToRemove.active=false;
            vertices.remove(vertexToRemove);
        }
        return adjacentVertices;
    }

    void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors) {
//        originalVertex.active = true;
        vertices.add(originalVertex);
        this.addEdges(originalVertex, neighbors);

        if (!adjVertices.containsKey(originalVertex))
            adjVertices.put(originalVertex, new HashSet<>());
        for (Vertex neighbor : neighbors) {
            adjVertices.get(originalVertex).add(neighbor);
            originalVertex.degree++;

            if (!adjVertices.containsKey(neighbor)) {
                adjVertices.put(neighbor, new HashSet<>());
//                neighbor.active = true;
                vertices.add(neighbor);
            }
            adjVertices.get(neighbor).add(originalVertex);
            neighbor.degree++;
        }
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
            // new
//            copy.adjVertices.put(vertexCopy, new HashSet<>(this.adjVertices.get(vertexCopy)));
        }

        // why do that now?
        for (Vertex vertex : this.adjVertices.keySet()) {
            for (Vertex neighbor : this.adjVertices.get(vertex)) {
                if (copy.adjVertices.containsKey(vertex)) {
                    copy.adjVertices.get(this.findVertex(copy.vertices, vertex)).add(this.findVertex(copy.vertices, neighbor));
                } else {
                    HashSet<Vertex> tmpNeighbors = new HashSet<>();
                    tmpNeighbors.add(findVertex(copy.vertices, neighbor));
                    copy.adjVertices.put(findVertex(copy.vertices, vertex), tmpNeighbors);
                }
            }
        }
        return copy;
    }

    public Vertex findVertex(HashSet<Vertex> vertices, Vertex vertex){
        for (Vertex v : vertices){
            if (v.equals(vertex)){
                return vertex;
            }
        }
        return null;
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



        while (!copyGraph.adjVertices.keySet().isEmpty()) {
            maxClique = copyGraph.getMaximalCliqueFromVertex(copyGraph.getNextNode());
            result.add(maxClique);
            usedVertices+=maxClique.size();

            copyGraph.removeSetofVertices(maxClique);

        }
        return this.adjVertices.size() - result.size()- (this.adjVertices.size()-usedVertices);

    }


    public HashSet<Vertex> getVertices() {
        return this.vertices;
    }

    public HashSet<Edge> getListEdges() {
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
        return vertices.size() <= (k * k + k) && edges.size() <= k * k;
    }

    public HashMap<Vertex,HashSet<Vertex>> applyDominationRule(){
        HashMap<Vertex,HashSet<Vertex>> verticesInVertexCover = new HashMap<>();
        while(true){
            boolean reduced = false;
            for (Vertex currentVertex : new HashSet<>(this.vertices)) {
                    boolean delete = false;
                    if (this.vertices.contains(currentVertex)){
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
            for (Vertex currentVertex : new ArrayList<>(this.vertices)) {
                if (this.vertices.contains(currentVertex)){
                    if (isUnconfined(currentVertex)) {
                            verticesInVertexCover.put(currentVertex, this.removeVertex(currentVertex));
                            reduced = true;
                    }
                }
            }
            if(!reduced) break;
        }
        return verticesInVertexCover;
    }

    public int reducedGraph(){
        //this.applyHighDegreeRule(0);
        int numOfReducedVertices = this.applyDominationRule().size();
        numOfReducedVertices += this.applyUnconfinedRule().size();
        return numOfReducedVertices;
    }

    public void printReducedGraph(){
        int numReducedVertices = this.reducedGraph();
        System.out.println("# "+ this.vertices.size() + " " + this.edges.size());
        for (Edge edge: this.edges){
            System.out.println(edge.getFirstVertex().name + " " + edge.getSecondVertex().name);
        }
        System.out.println("#difference: "+ numReducedVertices);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }
        }


        Graph graph = new Graph(edges);
        graph.printReducedGraph();
    }
}
