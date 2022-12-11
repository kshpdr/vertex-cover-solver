import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Graph  {
//    private BipartiteGraph bipartiteGraph;
    private final HashMap<Vertex, HashSet<Vertex>> adjVertices = new HashMap<>();
    private final HashSet<Vertex> vertices = new HashSet<>();
    private int indexCounter = 0;

    public int difference = 0;
    public boolean completeReduced = true;

    public Graph(HashSet<String[]> edges) {
        HashMap<String, Vertex> idxMap = new HashMap<>();
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
//        bipartiteGraph = new BipartiteGraph(this);
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
                vertices.remove(tmpVertex);
            }
        }

        if (adjVertices.containsKey(vertexToRemove)) {
            adjacentVertices.addAll(adjVertices.get(vertexToRemove));
            vertexToRemove.degree = 0;
            adjVertices.remove(vertexToRemove);
            vertices.remove(vertexToRemove);
        }
//        bipartiteGraph.removeVertex(vertexToRemove);
        return adjacentVertices;
    }

    void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors) {
        vertices.add(originalVertex);

        if (!adjVertices.containsKey(originalVertex))
            adjVertices.put(originalVertex, new HashSet<>());
        for (Vertex neighbor : neighbors) {
            adjVertices.get(originalVertex).add(neighbor);
            originalVertex.degree++;

            if (!adjVertices.containsKey(neighbor)) {
                adjVertices.put(neighbor, new HashSet<>());
                vertices.add(neighbor);
            }
            adjVertices.get(neighbor).add(originalVertex);
            neighbor.degree++;

//            bipartiteGraph.addEdge(originalVertex, neighbor);
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
        }

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


    int getCliqueLowerBound() {
        return this.vertices.size() - this.getHeuristicCliqueCover();
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
        this.completeReduced = false;
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
        this.completeReduced = true;
        return edges;
    }

    public HashMap<Vertex,HashSet<Vertex>> applyTwoDegreeRule(){
        this.completeReduced = false;
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
        this.completeReduced = true;
        return edges;
    }

    // public void undoMerge(LinkedList<MergeElement> mergeList, LinkedList<String> resultList){
    //     // TODO
    // }

    public HashMap<Vertex,HashSet<Vertex>> applyHighDegreeRule(int k){
        this.completeReduced = false;
        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
        while (true) {
            HashSet<Vertex> verticesToDelete = new HashSet<>();
            boolean reducable = false;
            for (Vertex vertex : adjVertices.keySet()) {
                if (adjVertices.get(vertex).size() > k) {
                    edges.put(vertex, adjVertices.get(vertex));
                    verticesToDelete.add(vertex);
                    reducable = true;
                }
            }
            removeSetofVertices(verticesToDelete);
            if (!reducable) break;
        }
        this.completeReduced = true;
        return edges;
    }


    public boolean applyBussRule(int k){
        HashSet<Edge> edges = new HashSet<>();
        for (Vertex v : adjVertices.keySet()){
            for (Vertex neighbor : adjVertices.get(v)){
                if (!edges.contains(new Edge(v, neighbor)) && !edges.contains(new Edge(neighbor, v))){
                    edges.add(new Edge(v, neighbor));
                }
            }
        }
        return vertices.size() <= (k * k + k) && edges.size() <= k * k;
    }

    public HashMap<Vertex,HashSet<Vertex>> applyDominationRule(){
        this.completeReduced = false;
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
        this.completeReduced=true;
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
        this.completeReduced = false;
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
        this.completeReduced = true;
        return verticesInVertexCover;
    }

    public HashMap<Vertex,HashSet<Vertex>> getComplementGraph(){
        HashMap<Vertex,HashSet<Vertex>> complementGraph = new HashMap<>();
            for(Vertex vertex1: this.vertices){
                for(Vertex vertex2:this.vertices){
                    if(!vertex1.equals(vertex2)) {
                        if (!complementGraph.containsKey(vertex1)) {
                            complementGraph.put(vertex1, new HashSet<>());

                        }
                        if (!this.adjVertices.get(vertex1).contains(vertex2)) {
                            //Reinitializing colors in case they come from another iteration
                            vertex1.color=-1;
                            vertex2.color=-1;
                            complementGraph.get(vertex1).add(vertex2);
                        }
                    }
                }
            }
            return complementGraph;
    }

    public ArrayList<Vertex> getOrderForColouring(HashMap<Vertex,HashSet<Vertex>> graph, ArrayList<Vertex> vertices){
        for(Vertex v: vertices){
            v.degree = graph.get(v).size();
        }
        vertices.sort(Comparator.comparingInt(o -> o.degree));
        return vertices;
    }

    public void getGraphColoring(HashMap<Vertex,HashSet<Vertex>> graph,ArrayList<Vertex> order){
            for(Vertex vertex: order){
                vertex.color = this.getNextColorForColouring(graph.get(vertex));
            }
    }

    public int getNextColorForColouring(Set<Vertex> neighbors){
        Set<Integer> setOfColors = new HashSet<>();
        for(Vertex neighbor: neighbors){
                setOfColors.add(neighbor.color);

        }

        int color = 1;
        while(true){
            if(setOfColors.contains(color)){
                color++;
            }else {
                break;
            }
        }
        return color;
    }

    public int getHeuristicCliqueCover(){
        HashMap<Vertex,HashSet<Vertex>> complementGraph = this.getComplementGraph();
        ArrayList<Vertex> vertices = new ArrayList<>(complementGraph.keySet());
        ArrayList<Vertex> orderedVertices = this.getOrderForColouring(complementGraph, vertices);
        this.getGraphColoring(complementGraph,orderedVertices);
        Set<Integer> setColors= new HashSet<>();
        for(Vertex vertex: orderedVertices){
            setColors.add(vertex.color);
        }
        return setColors.size();

    }

    public HashMap<Vertex,HashSet<Vertex>> applyLpReduction(){
        this.completeReduced = false;
        HashMap<Vertex,HashSet<Vertex>> verticesInVertexCover = new HashMap<>();
        int originalLpSolution = this.getLpBound();
        ArrayList<Vertex> tmpVertices;
        boolean reduced;
        boolean changedGraph = false;
        do {
            reduced = false;
            tmpVertices = new ArrayList<>(this.vertices);
            for (Vertex v : tmpVertices) {
                originalLpSolution = this.getLpBound();
                changedGraph = false;
                HashSet<Vertex> removedVertices;
                removedVertices = this.removeVertex(v);
                int tmpLpSolution = this.getLpBound() + 1;
                if (tmpLpSolution <= originalLpSolution) {
                    verticesInVertexCover.put(v, removedVertices);
                    reduced = true;
                    changedGraph = true;
                } else {
                    this.putVertexBack(v, removedVertices);
                }
            }
        } while (reduced);
        this.completeReduced = true;
        return verticesInVertexCover;
    }

    public void printReducedGraph(){
        int numEdges = 0;
        StringBuilder sb = new StringBuilder();
        for (Vertex vertex1: this.vertices) {
            for (Vertex vertex2: this.adjVertices.get(vertex1)){
                 if (vertex1.id > vertex2.id) continue;

                 sb.append(vertex1.name).append(" ").append(vertex2.name).append("\n");
                 numEdges++;
            }
        }
        System.out.println("# " + this.vertices.size() + " "+ numEdges);
        System.out.println(sb);
        System.out.println("#difference: "+ difference);

    }

    public static void main(String[] args) throws InterruptedException, IOException {


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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(graph.completeReduced){
                graph.printReducedGraph();

            } else {
                copyGraph.printReducedGraph();
            }

        }));

//        ReductionRules reductionRules = new ReductionRules(true,true,true);
//        reducedVertices = reductionRules.applyReductionRules();

        graph.difference += graph.applyOneDegreeRule().size();
        graph.difference += graph.applyTwoDegreeRule().size();
        int lowerBound = graph.getMaxLowerBound(true, true);
        graph.difference += graph.applyHighDegreeRule(lowerBound).size();
        graph.difference += graph.applyDominationRule().size();
        graph.difference += graph.applyUnconfinedRule().size();
        if (graph.getVertices().size() < 150) {
            graph.difference += graph.applyLpReduction().size();
        }

    }
}
