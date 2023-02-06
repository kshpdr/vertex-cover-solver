import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static boolean useSmac = true;

    public static boolean oneDegreeRulePre = true;
    public static boolean twoDegreeRulePre = true;
    public static boolean dominationRulePre = true;
    public static boolean min2maxHeuristicPre = false;

    public static boolean lpBoundBeginning  = true;
    public static boolean cliqueBoundBeginning = true;
    public static boolean unconfinedRuleBeginning = true;
    public static boolean highDegreeRuleBeginning = true;
    public static boolean lpReductionBeginning = true;

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
    public static int depthThresholdReduction = 5;
    public static int depthThresholdBounds = 5;

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

        if (recursionDepth % depthThresholdReduction == 0){
            if(dominationRuleIteration) {
                reducedNeighborsMap.putAll(graph.applyDominationRule());
            }

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

        if (recursionDepth % depthThresholdBounds == 0 && k < graph.getMaxLowerBound(cliqueBoundIteration  && graph.getVertices().size()<90, lpBoundIteration)) {
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
    public static LinkedList<String> vc(Graph graph, int lowerBound, int upperBound) {
        // TODO: make use of upperBound => actually not used here ... but in "other branching strategy"

        while (true) {
            LinkedList<String> solution = vc_branch(graph, lowerBound);
            if (solution != null){
                return solution;
            }
            lowerBound++;
        }
    }

    public static void parseArguments(String[] args){
        for (int i=0;i<args.length-1;i++){
            String key = args[i];
            String val = args[i+1];
            if (key.equals("oneDegreeRulePre")) oneDegreeRulePre = Boolean.parseBoolean(val);
            else if (key.equals("twoDegreeRulePre")) twoDegreeRulePre = Boolean.parseBoolean(val);
            else if (key.equals("dominationRulePre")) dominationRulePre = Boolean.parseBoolean(val);
            else if (key.equals("lpBoundBeginning")) lpBoundBeginning = Boolean.parseBoolean(val);
            else if (key.equals("cliqueBoundBeginning")) cliqueBoundBeginning = Boolean.parseBoolean(val);
            else if (key.equals("unconfinedRuleBeginning")) unconfinedRuleBeginning = Boolean.parseBoolean(val);
            else if (key.equals("highDegreeRuleBeginning")) highDegreeRuleBeginning = Boolean.parseBoolean(val);
            else if (key.equals("lpReductionBeginning")) lpReductionBeginning = Boolean.parseBoolean(val);
            else if (key.equals("cliqueBoundIteration")) cliqueBoundIteration = Boolean.parseBoolean(val);
            else if (key.equals("lpBoundIteration")) lpBoundIteration = Boolean.parseBoolean(val);
            else if (key.equals("dominationRuleIteration")) dominationRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("unconfinedRuleIteration")) unconfinedRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("highDegreeRuleIteration")) highDegreeRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("oneDegreeRuleIteration")) oneDegreeRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("twoDegreeRuleIteration")) twoDegreeRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("lpReductionIteration")) lpReductionIteration = Boolean.parseBoolean(val);
            else if (key.equals("depthThreshold")) depthThresholdReduction = Integer.parseInt(val);
        }
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        BufferedReader bi;

        if (useSmac && args.length >= 1){
            // Parse command-line arguments from SMAC-Tool
            parseArguments(args);
            // Parse tescase from file
            String testcase = args[0];
            bi = new BufferedReader(new FileReader(testcase));
        }
        // Parse testcase from stdin
        else bi = new BufferedReader(new InputStreamReader(System.in));

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

        // Apply reduction rules before instatiating graph (+ internally used
        // datastructure(s))
        ReductionRules preReduction = new ReductionRules(oneDegreeRulePre,twoDegreeRulePre,dominationRulePre);

        LinkedList<String> reductionResult = preReduction.applyReductionRules(edges);

        // Find initial upper-bound for (possibly reduced) graph instance (represented by edges)
        int upperBound = preReduction.remainingVertices;
        if (min2maxHeuristicPre){
            upperBound = MinToMaxHeuristic.getUpperBound(edges);
            System.out.println("#upper-bound (min2max): "+upperBound);
        }
        else System.out.println("#upper-bound (default): "+upperBound);

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




        LinkedList<String> result = vc(graph, lowerbound, upperBound);
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
        
        if (useSmac){
            // Print output for SMAC-Tool
            System.out.println("Result of this algorithm run: SUCCESS, "+sec+", 0, "+sec+", 0");
        }
    }
}
