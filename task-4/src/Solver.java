import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static boolean oneDegreeRulePre = false;
    public static boolean twoDegreeRulePre = false;
    public static boolean dominationRulePre = false;

    public static boolean lpBoundBeginning  = true;
    public static boolean cliqueBoundBeginning = true;
    public static boolean unconfinedRuleBeginning = false;
    public static boolean highDegreeRuleBeginning = false;
    public static boolean lpReductionBeginning = false;

    public static boolean cliqueBoundIteration= true;
    public static boolean lpBoundIteration= true;
    public static boolean dominationRuleIteration = true;
    public static boolean unconfinedRuleIteration = true;
    public static boolean highDegreeRuleIteration = true;
    public static boolean oneDegreeRuleIteration = true;
    public static boolean twoDegreeRuleIteration = true;
    public static boolean lpReductionIteration = true;

    public static int recursiveSteps = 0;
    public static int recursionDepth = 0;
    public static int depthThreshold = 5;

    static LinkedList<String> vc_branch(Graph graph, int k) {
        HashMap<Vertex, HashSet<Vertex>> reducedNeighborsMap = new HashMap<>();
        recursionDepth++;
        recursiveSteps++;

        if (oneDegreeRuleIteration){
            reducedNeighborsMap.putAll(graph.applyOneDegreeRule());
        }

        if (twoDegreeRuleIteration){
            reducedNeighborsMap.putAll(graph.applyTwoDegreeRule());
        }

        if(highDegreeRuleIteration){
            reducedNeighborsMap.putAll(graph.applyHighDegreeRule(k));
            if (graph.applyBussRule(k - reducedNeighborsMap.size())){
                graph.putManyVerticesBack(reducedNeighborsMap);
                recursionDepth--;
                return null;
            }
        }

        if(dominationRuleIteration) {
            reducedNeighborsMap.putAll(graph.applyDominationRule());
        }

        if (recursionDepth % depthThreshold == 0){
            if(unconfinedRuleIteration) {
                reducedNeighborsMap.putAll(graph.applyUnconfinedRule());
            }

            if(lpReductionIteration) {
                reducedNeighborsMap.putAll(graph.applyLpReduction());
            }
        }

        k -= reducedNeighborsMap.size();

        if (k < 0) {

            // Putting back the reduced vertices
            graph.putManyVerticesBack(reducedNeighborsMap);
            recursionDepth--;
            return null;
        }
        if (graph.isEmpty()){
            LinkedList<String> result = new LinkedList<>();
            for (Vertex v : reducedNeighborsMap.keySet()){
                result.add(v.name);
            }
            recursionDepth--;
            return result;
        }

        if(k < graph.getMaxLowerBound(cliqueBoundIteration  && graph.getVertices().size()<90, lpBoundIteration)) {
            // Putting back the reduced vertices
            graph.putManyVerticesBack(reducedNeighborsMap);
            recursionDepth--;
            return null;
        }


        //System.out.println("k: " + k + " Clique Lower Bound: " + graph.getCliqueLowerBound());

        LinkedList<String> solution;

        // Get vertex with the highest degree
        Vertex v = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(v);

        solution = vc_branch(graph, k - 1);
        graph.putVertexBack(v, eliminatedNeighbors);


        if (solution!= null) {
            solution.add(v.name);
            for (Vertex neighbor : reducedNeighborsMap.keySet()){
                solution.add(neighbor.name);
            }
            recursionDepth--;
            return solution;
        }

        // Eliminating the neighbors of the vertex with the highest degree and storing
        // the neighbors of the neighbors with a hashmap
        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);

        // Branching with the neighbors
        solution = vc_branch(graph, k - eliminatedNeighbors.size());
        graph.putManyVerticesBack(eliminatedNeighborsMap);

        // Putting back the eliminated vertices
        if (solution != null) {
            for (Vertex neighbor : eliminatedNeighborsMap.keySet()){
                solution.add(neighbor.name);
            }
            for (Vertex neighbor : reducedNeighborsMap.keySet()){
                solution.add(neighbor.name);
            }
            recursionDepth--;
            return solution;
        }
        
        // Putting back the reduced vertices
        graph.putManyVerticesBack(reducedNeighborsMap);
        recursionDepth--;
        return null;
    }

    // main function which increases the cover vertex size k every iteration
    public static LinkedList<String> vc(Graph graph, int lowerBound) {
        while (true) {
            LinkedList<String> solution = vc_branch(graph, lowerBound);
            if (solution != null){
                return solution;
            }
            lowerBound++;
        }
    }

    public static LinkedList<String> fastVc(Graph graph){
        long startTime = System.nanoTime();
        int step = 1;
        HashSet<Vertex> newC = new HashSet<>();
        HashSet<Vertex> C = constructVc(graph);
        for (Vertex vertex : graph.getVertices()){
            if (!C.contains(vertex)){
                vertex.gain = 0;
            }
        }
        while (true){
            if (step % 10 == 0) {
                long finishTime = System.nanoTime();
                long elapsedTime = finishTime - startTime;
                long cutoffTimeInNanos = (long) (60 * 1e9);
                if (elapsedTime >= cutoffTimeInNanos) {
                    break;
                }
            }

            if (graph.isVertexCover(C)){
                newC = C;
                removeMinLossVertex(C);
                continue;
            }
            Vertex u = chooseRmVertex(C);
            C.remove(u);
            Vertex v = graph.getVertexWithGreaterGain(C);
            C.add(v);
            step++;
        }
        return getStringSolution(newC);
    }

    public static void removeMinLossVertex(HashSet<Vertex> vertices){
        Vertex minLossVertex = vertices.iterator().next();
        for (Vertex vertex : vertices){
            if (vertex.loss < minLossVertex.loss){
                minLossVertex = vertex;
            }
        }
        vertices.remove(minLossVertex);
    }

    public static LinkedList<String> getStringSolution(HashSet<Vertex> vertices){
        LinkedList<String> solution = new LinkedList<>();
        for (Vertex vertex : vertices){
            solution.add(vertex.name);
        }
        return solution;
    }

    public static Vertex chooseRmVertex(HashSet<Vertex> vertices){
        Vertex bestVertex = vertices.iterator().next();
        for (int i = 0; i < 50; i++){
            Vertex vertex = vertices.iterator().next();
            if (bestVertex.loss < vertex.loss){
                bestVertex = vertex;
            }
        }
        return bestVertex;
    }

    public static HashSet<Vertex> constructVc(Graph graph){
        HashSet<Vertex> solution = new HashSet<>();
        for (Vertex vertex : graph.getVertices()){
            for (Vertex neighbor : graph.getAdjVertices().get(vertex)){
                if (!solution.contains(vertex) && !solution.contains(neighbor)){
                    if (vertex.degree > neighbor.degree){
                        solution.add(vertex);
                    }
                    else{
                        solution.add(neighbor);
                    }
                }
            }
        }
        for (Vertex vertex : graph.getVertices()) {
            for (Vertex neighbor : graph.getAdjVertices().get(vertex)) {
                if (solution.contains(vertex) && !solution.contains(neighbor)){
                    vertex.loss++;
                }
                else if (!solution.contains(vertex) && solution.contains(neighbor)){
                    neighbor.loss++;
                }
            }
        }
        for (Vertex vertex : solution){
            if (vertex.loss == 0){
                solution.remove(vertex);
                for (Vertex neighbor : graph.getAdjVertices().get(vertex)){
                    neighbor.loss--;
                }
            }
        }
        return solution;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        // Storing edges to call the graph constructor afterwards
        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
//                if(nodes.length==1){
//                    System.exit(0);
//                }
                edges.add(nodes);

            }
        }

        long start = System.currentTimeMillis();

        // Apply reduction rules before instatiating graph (+ internally used
        // datastructure(s))
        ReductionRules preReduction = new ReductionRules(oneDegreeRulePre,twoDegreeRulePre,dominationRulePre);

        LinkedList<String> reductionResult = preReduction.applyReductionRules(edges);

        // Instantiate graph
        Graph graph = new Graph(edges);


        HashMap<Vertex, HashSet<Vertex>> edgesAfterRules = new HashMap<>();

        if(unconfinedRuleBeginning) {
            edgesAfterRules.putAll(graph.applyUnconfinedRule());
        }

        if(lpReductionBeginning){
            edgesAfterRules.putAll(graph.applyLpReduction());
        }



        // Call method with the clique lower bound
        int lowerbound = graph.getMaxLowerBound(cliqueBoundBeginning && graph.getVertices().size()<12000, lpBoundBeginning);

        if (highDegreeRuleBeginning){
            edgesAfterRules.putAll(graph.applyHighDegreeRule(lowerbound));
            while (graph.applyBussRule(lowerbound)){
                lowerbound++;
            }
        }
//        for(Vertex vertex: graph.getVertices()){
//            System.out.println(vertex.name);
//        };




//        LinkedList<String> result = vc(graph, lowerbound);
        LinkedList<String> result = fastVc(graph);
        // Putting it all together in one String to only use one I/O operation
        StringBuilder sb = new StringBuilder();
        int solutionSize = 0;

        // Save all results in one list
        LinkedList<String> allResults = new LinkedList<>();

        //Add results from reduction rules
        if (!reductionResult.isEmpty()) {
            allResults.addAll(reductionResult);
        }

        //Add results from Domination rule
        for (Vertex v : edgesAfterRules.keySet()){
            allResults.add(v.name);
        }

        // Add results from actual branching algorithm
        if (!result.isEmpty()) {
            allResults.addAll(result);
        }

        if (twoDegreeRulePre){
            preReduction.undoMerge(allResults);
        }

        for (String v : allResults){
            sb.append(v);
            sb.append("\n");
            solutionSize++;
        }

        sb.append("#recursive steps: ").append(recursiveSteps).append("\n");
        sb.append("#sol size: ").append(solutionSize).append("\n");

        String resultStr = sb.toString();
        System.out.print(resultStr);

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F;
        System.out.println("#time: " + sec);
    }
}
