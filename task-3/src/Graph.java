import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;

public class Graph  {
    private final HashMap<Vertex, HashSet<Vertex>> adjVertices = new HashMap<>();
    private final HashSet<Vertex> vertices = new HashSet<>();
    private final HashMap<String,Vertex> idxMap = new HashMap<>();
    private int indexCounter = 0;

    public Graph(HashSet<String[]> edges) {
        
        for (String[] edge : edges) {
            Vertex vertex1 = idxMap.get(edge[0]);
            if (vertex1 == null) {
                vertex1 = new Vertex(edge[0], indexCounter++);
                this.adjVertices.put(vertex1, new HashSet<>());
                this.vertices.add(vertex1);
                idxMap.put(edge[0],vertex1);
            }

            Vertex vertex2 = idxMap.get(edge[1]);
            if (vertex2 == null) {
                vertex2 = new Vertex(edge[1], indexCounter++);
                this.adjVertices.put(vertex2, new HashSet<>());
                this.vertices.add(vertex2);
                idxMap.put(edge[1],vertex2);
            }

            this.adjVertices.get(vertex1).add(vertex2);

            this.adjVertices.get(vertex2).add(vertex1);

            // increasing degrees of vertices
            vertex1.degree++;
            vertex2.degree++;
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

    public HashMap<Vertex, HashSet<Vertex>> getAdjVertices() {
        return adjVertices;
    }

    HashSet<Vertex> removeVertex(Vertex vertexToRemove) {
//        vertexToRemove.active = false;
        vertices.remove(vertexToRemove);
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

    HashSet<Vertex> getCliqueFromVertex(Vertex firstVertex) {
        HashSet<Vertex> clique = new HashSet<>();
        clique.add(firstVertex);
        ArrayList<Vertex> vertices = new ArrayList<>(this.adjVertices.get(firstVertex));
        Collections.shuffle(vertices);
        for (Vertex vertex : vertices) {
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
            maxClique = copyGraph.getCliqueFromVertex(copyGraph.getNextNode());
            result.add(maxClique);
            usedVertices+=maxClique.size();

            copyGraph.removeSetofVertices(maxClique);

        }
        return this.adjVertices.size() - result.size()- (this.adjVertices.size()-usedVertices);

    }


    public HashSet<Vertex> getVertices() {
        return this.vertices;
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

    public HashMap<Vertex,HashSet<Vertex>> applyOneDegreeRule(){
        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
        while (true){
            boolean reduced = false;
            for (Vertex vertex : new LinkedList<>(adjVertices.keySet())){
                HashSet<Vertex> neighbors = adjVertices.get(vertex);
                if (neighbors == null) continue;
                if (neighbors.size() == 1){
                    for (Vertex neighbor : adjVertices.get(vertex)){
                        edges.put(neighbor,removeVertex(neighbor));
                        reduced = true;
                        break;
                    }
                }
            }
            if (!reduced) break;
        }
        return edges;
    }

    public HashMap<Vertex,HashSet<Vertex>> applyTwoDegreeRule(LinkedList<MergeElement> mergeList){
        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
        while (true){
            boolean reduced = false;
            for (Vertex vertex : new LinkedList<>(adjVertices.keySet())){
                HashSet<Vertex> neighbors = adjVertices.get(vertex);
                if (neighbors == null) continue;
                if (neighbors.size() == 2){
                    ArrayList<Vertex> arr = new ArrayList<>(neighbors);
                    Vertex u = arr.get(0);
                    Vertex w = arr.get(1);
                    if (adjVertices.get(u).contains(w)){
                        edges.put(u,removeVertex(u));
                        edges.put(w,removeVertex(w));
                        reduced = true;
                    }
                    else {
                        // TODO
                    }
                }
            }
            if (!reduced) break;
        }
        return edges;
    }

    public void undoMerge(LinkedList<MergeElement> mergeList, LinkedList<String> resultList){
        // TODO
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
        int totalEdges = 0;
        for (Vertex v : adjVertices.keySet()){
            HashSet<Vertex> neighbors = adjVertices.get(v);
            if (neighbors == null) continue;
            totalEdges += neighbors.size();
        }
        return vertices.size() <= (k * k + k) && totalEdges <= k * k;
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



    public void printReducedGraph(int numOfReducedVertices){

        int totalEdges = 0;
        for (Vertex firstVertex: this.adjVertices.keySet()){

            HashSet<Vertex> neighbors = this.adjVertices.get(firstVertex);
            if (neighbors == null) continue;

            for (Vertex secondVertex : neighbors){
                if (firstVertex.id > secondVertex.id) continue;
                
                System.out.println(firstVertex.name + " " + secondVertex.name);
                totalEdges++;
            }
        }
        System.out.println("# "+ this.vertices.size() + " " + totalEdges);
        System.out.println("#difference: "+ numOfReducedVertices);
    }

    public static void main(String[] args) throws IOException {

        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));


        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split(" ");
                edges.add(nodes);
            }
        }



        Graph graph = new Graph(edges);
        Graph copyGraph = graph.getCopy();
        final int[] numReducedVertices = {0};




        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            numReducedVertices[0] += graph.applyDominationRule().size();
            numReducedVertices[0] += graph.applyUnconfinedRule().size();
        });
        try {
            future.get(300, TimeUnit.SECONDS);
            graph.printReducedGraph(numReducedVertices[0]);
        } catch (TimeoutException e) {
            future.cancel(true);
            if(future.isCancelled()) {
                copyGraph.printReducedGraph(0);
            }
        } catch (Exception e) {
            // handle other exceptions
        } finally {
            executor.shutdownNow();
        }




    }
}
