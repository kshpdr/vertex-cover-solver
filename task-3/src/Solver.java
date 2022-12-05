import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static boolean lpBoundBeginning  = true;
    public static boolean cliqueBoundBeginning = true;
    public static boolean dominationRuleBeginning = true;
    public static boolean unconfinedRuleBeginning = true;
    public static boolean highDegreeRuleBeginning = true;

    public static boolean cliqueBoundIteration= true;
    public static boolean lpBoundIteration= true;
    public static boolean dominationRuleIteration = true;
    public static boolean unconfinedRuleIteration = true;
    public static boolean highDegreeRuleIteration = true;

    public static int recursiveSteps = 0;

    static LinkedList<String> vc_branch(Graph graph, int k) {
        HashMap<Vertex, HashSet<Vertex>> reducedNeighborsMap = new HashMap<>();
        if(dominationRuleIteration) {
            reducedNeighborsMap.putAll(graph.applyDominationRule());
        }

        if(unconfinedRuleIteration) {
            reducedNeighborsMap.putAll(graph.applyUnconfinedRule());
        }

        k -= reducedNeighborsMap.size();

        if (k < 0) {
            // Putting back the reduced vertices
            graph.putManyVerticesBack(reducedNeighborsMap);
            return null;
        }
        if (graph.isEmpty()){
            LinkedList<String> result = new LinkedList<>();
            for (Vertex v : reducedNeighborsMap.keySet()){
                result.add(v.name);
            }
            return result;
        }

        if(k < graph.getMaxLowerBound(cliqueBoundBeginning, lpBoundBeginning)) {
            // Putting back the reduced vertices
            graph.putManyVerticesBack(reducedNeighborsMap);
            return null;
        }

        //System.out.println("k: " + k + " Clique Lower Bound: " + graph.getCliqueLowerBound());

        LinkedList<String> solution;
        recursiveSteps++;

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
            return solution;
        }
        
        // Putting back the reduced vertices
        graph.putManyVerticesBack(reducedNeighborsMap);

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

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        // Storing edges to call the graph constructor afterwards
        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }
        }

        long start = System.currentTimeMillis();


        // Apply reduction rules before instatiating graph (+ internally used
        // datastructure(s))
        LinkedList<String> reductionResult = ReductionRules.applyReductionRules(edges);

        // Instantiate graph
        Graph graph = new Graph(edges);




        HashMap<Vertex, HashSet<Vertex>> edgesAfterRules = new HashMap<>();



        if(dominationRuleBeginning){
            edgesAfterRules.putAll(graph.applyDominationRule());
        }

        if(unconfinedRuleBeginning) {
            edgesAfterRules.putAll(graph.applyUnconfinedRule());
        }

        // Call method with the clique lower bound
        int lowerbound = graph.getMaxLowerBound(cliqueBoundIteration, lpBoundIteration);

        if (highDegreeRuleBeginning){
            HashMap<Vertex, HashSet<Vertex>> edgesAfterHighDegreeRule = graph.applyHighDegreeRule(lowerbound);
            edgesAfterRules.putAll(edgesAfterHighDegreeRule);
        }

        LinkedList<String> result = vc(graph, lowerbound);
        // Putting it all together in one String to only use one I/O operation
        StringBuilder sb = new StringBuilder();
        int solutionSize = 0;

        //Add results from reduction rules
        if (!reductionResult.isEmpty()) {
            for (String s : reductionResult) {
                sb.append(s).append("\n");
                solutionSize++;
            }
        }

        //Add results from Domination rule
        for(Vertex vertex: edgesAfterRules.keySet()){
            sb.append(vertex.name).append("\n");
            solutionSize++;
        }

        // Add results from actual branching algorithm
        if (!result.isEmpty()) {
            for (String s : result) {
                solutionSize++;
                sb.append(s).append("\n");
            }
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
