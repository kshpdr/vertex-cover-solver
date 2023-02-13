import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Graph  {
//    private HashMap<Vertex,HashSet<Vertex>> complementGraph = new HashMap<>();
    private BipartiteGraph bipartiteGraph;
    private final VertexDegreeOrder degreeOrder = new VertexDegreeOrder();
    private int edgesNumber;
    private int indexCounter;
    private final int twinCounter = 0;
    private final HashMap<Vertex, HashSet<Vertex>> adjVertices = new HashMap<>();
    private final HashSet<Vertex> vertices = new HashSet<>();

    public int difference = 0;
    public boolean completeReduced = true;
    private final Stack<Vertex> mergedVertices = new Stack<>();

    public Graph(HashSet<String[]> edges) {
        HashMap<String, Vertex> idxMap = new HashMap<>();
        indexCounter = 0;
        for (String[] edge : edges) {
            edgesNumber++;
            Vertex vertex1 = idxMap.get(edge[0]);
            if (vertex1 == null) {
                vertex1 = new Vertex(edge[0], indexCounter++);
                this.adjVertices.put(vertex1, new HashSet<>());
                this.vertices.add(vertex1);
                this.degreeOrder.addVertex(vertex1);
                idxMap.put(edge[0],vertex1);
            }

            Vertex vertex2 = idxMap.get(edge[1]);
            if (vertex2 == null) {
                vertex2 = new Vertex(edge[1], indexCounter++);
                this.adjVertices.put(vertex2, new HashSet<>());
                this.vertices.add(vertex2);
                this.degreeOrder.addVertex(vertex2);
                idxMap.put(edge[1],vertex2);
            }

            this.adjVertices.get(vertex1).add(vertex2);

            this.adjVertices.get(vertex2).add(vertex1);

            // increasing degrees of vertices
            vertex1.degree++;
            vertex2.degree++;
            this.degreeOrder.increaseDegreeOfVertex(vertex1, 1);
            this.degreeOrder.increaseDegreeOfVertex(vertex2, 1);
        }
        bipartiteGraph = new BipartiteGraph(this);
//        this.getComplementGraph();
    }

    public Graph() {

    }

    public Graph(HashMap<Vertex, HashSet<Vertex>> reducedGraph,HashMap<Vertex,HashSet<Vertex>> reducedComplementGraph) {

//        this.complementGraph = reducedComplementGraph;
        for(Vertex vertex: reducedGraph.keySet()){
            this.adjVertices.put(vertex,new HashSet<>(reducedGraph.get(vertex)));
            degreeOrder.addVertex(vertex);
            this.edgesNumber += this.adjVertices.get(vertex).size();
        }
        this.edgesNumber /=2;
        this.vertices.addAll(reducedGraph.keySet());
        this.bipartiteGraph = new BipartiteGraph(this);


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

//    public List<Graph> getComponents(){
//        HashSet<Vertex> visited = new HashSet<>();
//
//        List<Graph> components = new ArrayList<>();
//        for (Vertex vertex : vertices){
//            if (!visited.contains(vertex)){
//                HashMap<Vertex, HashSet<Vertex>> visitedVertices = getVerticesFromDFS(vertex, visited);
////                Graph component = new Graph(visitedVertices,getReducedComplementGraph(visitedVertices.keySet()));
//
////                components.add(component);
//            }
//        }
//        return components;
//    }


//    private HashMap<Vertex, HashSet<Vertex>> getReducedComplementGraph( Set<Vertex> verticesToKeep) {
//        HashMap<Vertex,HashSet<Vertex>> reducedComplementGraph = new HashMap<>();
//        HashSet<Vertex> verticesToRemove = new HashSet<>(this.vertices);
//        verticesToRemove.removeAll(verticesToKeep);
//
//
//        for(Vertex v: verticesToKeep){
//            reducedComplementGraph.put(v, new HashSet<>(this.complementGraph.get(v)));
//            reducedComplementGraph.get(v).removeAll(verticesToRemove);
//        }
//
//        return reducedComplementGraph;
//
//
//
//    }

    public HashMap<Vertex, HashSet<Vertex>> getVerticesFromDFS(Vertex vertex, HashSet<Vertex> visited){
        HashMap<Vertex, HashSet<Vertex>> visitedVertices = new HashMap<>();
        visited.add(vertex);
        visitedVertices.put(vertex, getAdjVertices().get(vertex));
        for (Vertex neighbor : getAdjVertices().get(vertex)){
            if (!visited.contains(neighbor)) {
                visitedVertices.putIfAbsent(neighbor, getAdjVertices().get(neighbor));
                visitedVertices.putAll(getVerticesFromDFS(neighbor, visited));
            }
        }
        return visitedVertices;
    }

    public HashMap<Vertex, HashSet<Vertex>> getAdjVertices() {
        return adjVertices;
    }

    HashSet<Vertex> removeVertex(Vertex vertexToRemove) {
        vertices.remove(vertexToRemove);
        HashSet<Vertex> adjacentVertices = new HashSet<>();
        this.degreeOrder.removeVertex(vertexToRemove);
        Iterator<Vertex> iterator = this.adjVertices.keySet().iterator();
        while (iterator.hasNext()) {
            Vertex tmpVertex = iterator.next();
            if (this.adjVertices.get(tmpVertex).remove(vertexToRemove)) {
                edgesNumber--;
                tmpVertex.degree--;
                this.degreeOrder.decreaseDegreeOfVertex(tmpVertex, 1);
//                if(!this.complementGraph.containsKey(tmpVertex)) this.complementGraph.put(tmpVertex, new HashSet<>());
//                if(!this.complementGraph.containsKey(vertexToRemove)) this.complementGraph.put(vertexToRemove, new HashSet<>());
//                this.complementGraph.get(tmpVertex).add(vertexToRemove);
//                this.complementGraph.get(vertexToRemove).add(tmpVertex);
            }
            if (this.adjVertices.get(tmpVertex).isEmpty()) {
                iterator.remove();
                vertices.remove(tmpVertex);
                this.degreeOrder.removeVertex(tmpVertex);
            }
        }

        if (adjVertices.containsKey(vertexToRemove)) {
            adjacentVertices.addAll(adjVertices.get(vertexToRemove));
            vertexToRemove.degree = 0;
            adjVertices.remove(vertexToRemove);
            vertices.remove(vertexToRemove);
        }
        bipartiteGraph.removeVertex(vertexToRemove);
        return adjacentVertices;
    }

    void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors) {
        vertices.add(originalVertex);

        if (!adjVertices.containsKey(originalVertex))
            adjVertices.put(originalVertex, new HashSet<>());
        for (Vertex neighbor : neighbors) {
            edgesNumber++;
            adjVertices.get(originalVertex).add(neighbor);
//            this.complementGraph.get(neighbor).remove(originalVertex);
//            if(this.complementGraph.get(neighbor).isEmpty()) this.complementGraph.remove(neighbor);
//            this.complementGraph.get(originalVertex).remove(neighbor);

            originalVertex.degree++;

            if (!adjVertices.containsKey(neighbor)) {
                adjVertices.put(neighbor, new HashSet<>());
                vertices.add(neighbor);
            }
            adjVertices.get(neighbor).add(originalVertex);
            neighbor.degree++;
            this.degreeOrder.increaseDegreeOfVertex(neighbor, 1);

            bipartiteGraph.addEdge(originalVertex, neighbor);
        }
//        if(this.complementGraph.get(originalVertex).isEmpty()) this.complementGraph.remove(originalVertex);
        this.degreeOrder.putBack(originalVertex, neighbors.size());
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
        for (Vertex vertex : this.vertices) {
            Vertex vertexCopy = new Vertex(vertex.name, vertex.id);
            vertexCopy.degree = vertex.degree;
            vertexCopy.complementDegree = vertex.complementDegree;
            copy.vertices.add(vertexCopy);
            copy.degreeOrder.addVertex(vertexCopy);
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
        //BipartiteGraph bipartiteGraph = new BipartiteGraph(this);
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
            HashSet<Vertex> degreeOneVertices = degreeOrder.getDegreeVertices(1);
            if (degreeOneVertices == null) break;
            for (Vertex vertex : new HashSet<>(degreeOneVertices)){
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
            HashSet<Vertex> degreeTwoVertices = degreeOrder.getDegreeVertices(2);
            if (degreeTwoVertices == null) break;
            for (Vertex vertex : new HashSet<>(degreeTwoVertices)){
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

                }
            }
            if (!reduced) break;
        }
        this.completeReduced = true;
        return edges;
    }

//    public HashMap<Vertex, HashSet<Vertex>> applyTwinRule(){
//        boolean reduced;
//        HashMap<Vertex, HashSet<Vertex>> edges = new HashMap<>();
//        do {
//            reduced = false;
//            HashSet<Vertex> bucket = this.degreeOrder.getDegreeVertices(3);
//            if (bucket == null) break;
//            ArrayList<Vertex> degreeThreeBucket = new ArrayList<>(bucket);
//            for (int i=0;i<degreeThreeBucket.size();i++){
//                Vertex v = degreeThreeBucket.get(i);
//                HashSet<Vertex> Nv = adjVertices.get(v);
//                for (int j=i+1;j<degreeThreeBucket.size();j++){
//                    Vertex u = degreeThreeBucket.get(j);
//                    HashSet<Vertex> Nu = adjVertices.get(u);
//
//                    boolean isTwin = true;
//                    boolean hasEdges = false;
//                    for (Vertex n : Nv){
//                        if (!Nu.contains(n)) {
//                            isTwin = false;
//                            break;
//                        }
//                        HashSet<Vertex> Nn = new HashSet<>(adjVertices.get(n));
//                        for (Vertex n2 : Nv){
//                            if (n.id <= n2.id) continue;
//                            if (Nn.contains(n2)){
//                                hasEdges = true;
//                                break;
//                            }
//                        }
//                    }
//
//                    if (isTwin){
//                        System.out.println("#twin-rule: true");
//                        reduced = true;
//                        if (hasEdges) {
//                            // Remove v, u and N(u) from graph
//                            edges.putAll(removeSetofVertices(new HashSet<>(Nu)));
//                        }
//                        else {
//                            // Create new vertex w
//                            Vertex w = new Vertex("twin-"+twinCounter++, indexCounter++);
//                            HashSet<Vertex> twoNeighborhood = new HashSet<>();
//
//                            // Find 2-neighborhood from vertex u
//                            for (Vertex n1 : Nu) twoNeighborhood.addAll(adjVertices.get(n1));
//                            twoNeighborhood.remove(v);
//                            twoNeighborhood.remove(u);
//                            twoNeighborhood.removeAll(Nu);
//
//                            // Make vertex w adjacent to 2-neighborhood of u
//                            putVertexBack(w, twoNeighborhood);
//
//                            w.addMergeInformation(v, u, new HashSet<>(Nu));
//
//                            removeSetofVertices(new HashSet<>(Nu));
//
//                            // Create merge record (for later undoing ...)
//                            //MergeRecord merge = new MergeRecord(w, v, u, Nu);
//                            //mergeMap.put(w,merge);
//                            mergedVertices.push(w);
//
//                        }
//                        // Remove v, u and N(u) from graph
//                        removeVertex(v);
//                        removeVertex(u);
//                        break;
//                    }
//                }
//                if (reduced) break;
//            }
//        } while (reduced);
//        return edges;
//    }

//    public void undoMerges(LinkedList<String> solution){
//
//        System.out.println("#merged: "+mergedVertices.size());
//        while (!mergedVertices.isEmpty()){
//            Vertex w = mergedVertices.pop();
//            if (solution.contains(w.name)) {
//                solution.remove(w.name);
//                for (Vertex n : w.Nu){
//                    solution.add(n.name);
//                }
//            }
//            else {
//                solution.add(w.u.name);
//                solution.add(w.v.name);
//            }
//        }
//
//
//    }

    public HashMap<Vertex,HashSet<Vertex>> applyHighDegreeRule(int k){
        int newK = k;
        this.completeReduced = false;
        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
        while (true) {
            HashMap<Vertex,HashSet<Vertex>> edgesToAdd = new HashMap<>();
            HashSet<Vertex> verticesToDelete = new HashSet<>();
            boolean reducable = false;
            for (int degree : degreeOrder.sortedAvailableDegrees){
                for (Vertex vertex : degreeOrder.getDegreeVertices(degree)) {
                    if (adjVertices.get(vertex).size() > newK) {
                        edgesToAdd.put(vertex, adjVertices.get(vertex));
                        verticesToDelete.add(vertex);
                        reducable = true;
                    }
                }
                if (verticesToDelete.size() <= newK && !verticesToDelete.isEmpty()){
                    removeSetofVertices(verticesToDelete);
                    edges.putAll(edgesToAdd);
                    newK -= edgesToAdd.size();
                }
                else{
                    reducable = false;
                }
            }
            if (!reducable) break;
        }
        this.completeReduced = true;
        return edges;
//        this.completeReduced = false;
//        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
//        HashSet<Vertex> verticesToDelete = new HashSet<>();
//        for (Vertex vertex : adjVertices.keySet()) {
//            if (adjVertices.get(vertex).size() > k) {
//                edges.put(vertex, adjVertices.get(vertex));
//                verticesToDelete.add(vertex);
//            }
//        }
//        if (verticesToDelete.size() <= k && !verticesToDelete.isEmpty()){
//            removeSetofVertices(verticesToDelete);
//        }
//        this.completeReduced = true;
//        return edges;
    }


    public boolean applyBussRule(int k){
//        HashSet<Edge> edges = new HashSet<>();
//        for (Vertex v : adjVertices.keySet()){
//            for (Vertex neighbor : adjVertices.get(v)){
//                if (!edges.contains(new Edge(v, neighbor)) && !edges.contains(new Edge(neighbor, v))){
//                    edges.add(new Edge(v, neighbor));
//                }
//            }
//        }
        return vertices.size() > (k * k + k) || edgesNumber > k * k;
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
//                            HashSet<Vertex> tmpNeighbors = new HashSet<>(this.adjVertices.get(currentVertex));
//                            tmpNeighbors.add(currentVertex);
                            this.adjVertices.get(n).remove(currentVertex);
                            if (this.adjVertices.get(currentVertex).containsAll(this.adjVertices.get(n))) {
                                delete = true;
                                reduced = true;

                                this.adjVertices.get(n).add(currentVertex);

                                break;

                            }
                            this.adjVertices.get(n).add(currentVertex);

                        }
                        if (delete) {
                            verticesInVertexCover.put(currentVertex, this.removeVertex(currentVertex));
//                            for(HashSet<Vertex> collateralRemovedVertices: this.removeSetofVertices(removedVertices).values()){
//                                verticesInVertexCover.get(currentVertex).addAll(collateralRemovedVertices);
//                            }

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

    public void getComplementGraph() {
        for (Vertex v: vertices){
            HashSet<Vertex> verticesToKeep = new HashSet<>(vertices);
            verticesToKeep.removeAll(this.adjVertices.get(v));
//            this.complementGraph.put(v, verticesToKeep);
        }


//        HashMap<Vertex, HashSet<Vertex>> complement = new HashMap<>();
//
//        // get all vertices in the graph
//        Set<Vertex> vertices = this.adjVertices.keySet();
//
//        // loop through all vertices
//        for (Vertex u : vertices) {
//            // initialize an empty set for the complement edge
//            HashSet<Vertex> complementNeighbors = new HashSet<>();
//
//            // loop through all vertices again
//            for (Vertex v : vertices) {
//                // add the vertex to the complement edge if it is not adjacent to the current vertex
//                // and it is not equal to the current vertex
//                if (!this.adjVertices.get(u).contains(v) && !u.equals(v)) {
//                    complementNeighbors.add(v);
//                }
//            }
//
//            // add the complement edge to the complement graph
//            complement.put(u, complementNeighbors);
//        }
//
    }


    public void getGraphColoring(HashMap<Vertex,HashSet<Vertex>> graph,ArrayList<Vertex> order){
            this.setInitialColors();
            for(Vertex vertex: order){
                vertex.color = this.getNextColorForColouring(graph.get(vertex));
            }
    }

    private void setInitialColors() {
        for(Vertex vertex: this.vertices){
            vertex.color =-1;
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
        //ArrayList<Vertex> orderedVertices = this.getOrderForColouring(complementGraph, vertices);
        ArrayList<Vertex> orderedVertices = this.degreeOrder.getOrderedVerticesDegree();
//        this.getGraphColoring(complementGraph,orderedVertices);
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
                if(changedGraph){
                    originalLpSolution = this.getLpBound();
                }
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

    public static ArrayList<Object> getInput() throws IOException {
        HashSet<String[]> edges = new HashSet<>();
        HashMap<Vertex,HashSet<Vertex>> adjMap = new HashMap<>();

        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
        String line;
        //min to min Graph
        HashMap<String,Vertex> idMap = new HashMap<>();
        int idCounter = 0;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                Vertex u = idMap.get(nodes[0]);
                if (u == null){
                    u = new Vertex(nodes[0],idCounter++);
                    idMap.put(nodes[0],u);
                }
                Vertex v = idMap.get(nodes[1]);
                if (v == null){
                    v = new Vertex(nodes[1],idCounter++);
                    idMap.put(nodes[1],v);
                }

                // Add (u -> v) to graph
                HashSet<Vertex> neighbors = adjMap.computeIfAbsent(u, k -> new HashSet<>());
                neighbors.add(v);
                // Add (v -> u) to graph
                neighbors = adjMap.computeIfAbsent(v, k -> new HashSet<>());
                neighbors.add(u);
                edges.add(nodes);
            }
        }
        ArrayList<Object> input = new ArrayList<>();
        input.add(edges);
        input.add(adjMap);
        return input;
    }

    public HashMap<Vertex,HashSet<Vertex>> applyFlowLpReduction(){
        HashMap<Vertex,HashSet<Vertex>> reducedEdges = new HashMap<>();
        ResidualGraph residualGraph = new ResidualGraph(bipartiteGraph, bipartiteGraph.findMaximumMatching());
        HashSet<Vertex> verticesToRemove = new HashSet<>();
        residualGraph.computeLp();
        for (Vertex vertex : residualGraph.lpZero){
            residualGraph.removeEdgeWith(vertex, bipartiteGraph.leftToRight.get(vertex));
        }
        residualGraph.applyLpReduction();

        HashSet<Vertex> reducedVertices = residualGraph.lpOne;
        HashSet<Vertex> verticesToDelete = residualGraph.lpZero;

        for (Vertex right : reducedVertices){
            for (Vertex vertex : vertices){
                if (right.name.equals(vertex.name)){
                    reducedEdges.put(vertex, getAdjVertices().get(vertex));
                    verticesToRemove.add(vertex);
                }
            }
        }

//        for (Vertex left : verticesToDelete){
//            for (Vertex vertex : vertices){
//                if (left.name.equals(vertex.name)){
//                    verticesToRemove.add(vertex);
//                }
//            }
//        }

        removeSetofVertices(verticesToRemove);
        return reducedEdges;
    }

//    public HashMap<Vertex,HashSet<Vertex>> applyTwinRule(){
//        Vertex mergedVertex = new Vertex("merged-" + (vertices.size() + 1), (vertices.size() + 1));
//        HashSet<Vertex> neighborsOfMerged = new HashSet<>();
//
//        HashMap<Vertex, HashSet<Vertex>> reducedEdges = new HashMap<>();
//        HashSet<Vertex> degreeThreeVertices = this.degreeOrder.getDegreeVertices(3);
//        if (degreeThreeVertices == null) return new HashMap<>();
//
//        boolean merged = false;
//        for (Vertex u : degreeThreeVertices){
//            HashSet<Vertex> neighbors = this.getAdjVertices().get(u);
//            for (Vertex v : degreeThreeVertices){
//                if (u.equals(v)) continue;
//                if (!neighbors.equals(this.getAdjVertices().get(v))) continue;
//
//                boolean hasEdges = false;
//                for (Vertex neighbor : neighbors){
//                    for (Vertex neighbor2 : neighbors){
//                        if (neighbor.equals(neighbor2)) continue;
//
//                        if (this.getAdjVertices().get(neighbor).contains(neighbor2)){
//                            hasEdges = true;
//                            break;
//                        }
//                    }
//                    if (hasEdges) break;
//                }
//
//                if (hasEdges) {
//                    merged = true;
//                    mergedVertex = new Vertex("merged-" + (vertices.size() + 1), (vertices.size() + 1));
//                    mergedVertex.addMergeInfo(u, v, neighbors);
//                    mergedVertices.add(mergedVertex);
//                    for (Vertex vertex : neighbors) neighborsOfMerged.addAll(getAdjVertices().get(vertex));
//                }
//                else {
//                    for (Vertex vertex : neighbors) reducedEdges.put(vertex, getAdjVertices().get(vertex));
//                }
//            }
//        }
//
//        if (merged) {
//            for (Vertex vertex : mergedVertex.commonNeighbors) this.removeVertex(vertex);
//            this.removeVertex(mergedVertex.first);
//            this.removeVertex(mergedVertex.second);
//            this.putVertexBack(mergedVertex, neighborsOfMerged);
//        }
//        for (Vertex vertex : reducedEdges.keySet()){
//            this.removeVertex(vertex);
//        }
//
//        return reducedEdges;
//    }

//    public void undoMerges(LinkedList<String> solution){
//        HashSet<Vertex> unfoldedVertices = new HashSet<>();
//        while (!mergedVertices.isEmpty()){
//            Vertex mergedVertex = mergedVertices.pop();
//            if (solution.contains(mergedVertex.name)) {
//                solution.remove(mergedVertex.name);
//                for (Vertex vertex : mergedVertex.commonNeighbors) solution.add(vertex.name);
//            }
//            else{
//                if (!mergedVertex.first.merged) solution.add(mergedVertex.first.name);
//                if (!mergedVertex.second.merged) solution.add(mergedVertex.second.name);
//            }
//        }
//    }

//    public void undoLastMerge(int amountTwinReduced){
//        while (amountTwinReduced != 0){
//            System.out.println("FUCK");
//            Vertex mergedVertex = mergedVertices.pop();
//            removeVertex(mergedVertex);
//            putVertexBack(mergedVertex.first, mergedVertex.commonNeighbors);
//            putVertexBack(mergedVertex.second, mergedVertex.commonNeighbors);
//            amountTwinReduced--;
//        }
//
//    }

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
        if (graph.getVertices().size() < 301) {
            graph.difference += graph.applyLpReduction().size();
        }

    }
}
