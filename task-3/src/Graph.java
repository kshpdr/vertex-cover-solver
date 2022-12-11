import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Graph  {
    private final Map<Vertex, HashSet<Vertex>> adjVertices = new HashMap<>();
    private final HashSet<Vertex> vertices = new HashSet<>();
    private final HashSet<Edge> edges = new HashSet<>();
    public boolean completeReduced = true;


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
        for(Edge edge: this.edges){
            copy.edges.add(new Edge(findVertex(copy.vertices, edge.getFirstVertex()), findVertex(copy.vertices, edge.getSecondVertex())));
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


    int getCliqueLowerBound() {

        return this.vertices.size() - this.getHeuristicCliqueCover();

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

    public HashMap<Vertex,HashSet<Vertex>> applyOneDegreeRule(){
        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
        while (true){
            boolean reduced = false;
            for (Vertex vertex : adjVertices.keySet()){
                HashSet<Vertex> neighbors = adjVertices.get(vertex);
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

    public HashMap<Vertex,HashSet<Vertex>> applyTwoDegreeRule(){
        HashMap<Vertex,HashSet<Vertex>> edges = new HashMap<>();
        while (true){
            boolean reduced = false;
            for (Vertex vertex : adjVertices.keySet()){
                HashSet<Vertex> neighbors = adjVertices.get(vertex);
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
                        // TODO: Implement merging for degree-rule
                    }
                }
            }
            if (!reduced) break;
        }
        return edges;
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
             this.completeReduced = false;
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
                this.completeReduced = true;
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

    public HashMap<Vertex,HashSet<Vertex>> lpReduction(){
        HashMap<Vertex,HashSet<Vertex>> verticesInVertexCover = new HashMap<>();
        int originalLpSolution = this.getLpBound();
        ArrayList<Vertex> tmpVertices;
        boolean reduced;
        boolean changedGraph = false;
        do {
            reduced = false;
            tmpVertices = new ArrayList<>(this.vertices);
            for (Vertex v : tmpVertices) {
                if (changedGraph) {
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
        return verticesInVertexCover;


    }

    public static int reducedVertices =0;





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
            int numEdges = 0;
            //Need a Hashmap here to not put 1 2 and 2 1 as an edge but just once
            HashMap<Vertex,HashSet<Vertex>> edgesSeen = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            if(graph.completeReduced){
                for(Vertex vertex1: graph.vertices){
                    for (Vertex vertex2: graph.adjVertices.get(vertex1)){
                        if(!edgesSeen.containsKey(vertex1)){
                            edgesSeen.put(vertex1, new HashSet<>());
                        }
                        if(!edgesSeen.containsKey(vertex2)){
                            edgesSeen.put(vertex2, new HashSet<>());
                        }
                        if(!edgesSeen.get(vertex1).contains(vertex2) && !edgesSeen.get(vertex2).contains(vertex1)){
                            sb.append(vertex1.name).append(" ").append(vertex2.name).append("\n");
                            edgesSeen.get(vertex1).add(vertex2);
                            edgesSeen.get(vertex2).add(vertex1);
                            numEdges++;
                        }


                    }
                }
                System.out.println("# " + graph.vertices.size() + " "+ numEdges);
                System.out.println(sb);
                System.out.println("#difference: "+ reducedVertices);

            }else {
                    for(Vertex vertex1: copyGraph.vertices){
                        for (Vertex vertex2: copyGraph.adjVertices.get(vertex1)){
                            if(!edgesSeen.containsKey(vertex1)){
                                edgesSeen.put(vertex1, new HashSet<>());
                            }
                            if(!edgesSeen.containsKey(vertex2)){
                                edgesSeen.put(vertex2, new HashSet<>());
                            }
                            if(!edgesSeen.get(vertex1).contains(vertex2) && !edgesSeen.get(vertex2).contains(vertex1)){
                                sb.append(vertex1.name).append(" ").append(vertex2.name).append("\n");
                                edgesSeen.get(vertex1).add(vertex2);
                                edgesSeen.get(vertex2).add(vertex1);
                                numEdges++;
                            }


                        }
                    }
                    System.out.println("# " + copyGraph.vertices.size() + " "+ numEdges);
                    System.out.println(sb);
                    System.out.println("#difference: "+ reducedVertices);


        }}));




        reducedVertices = graph.applyDominationRule().size();
        reducedVertices+= graph.applyUnconfinedRule().size();




    }
}
