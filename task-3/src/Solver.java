import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static int recursiveSteps = 0;

    public static boolean cliqueBoundOnce = false;
    public static boolean lpBoundOnce = false;
    public static boolean highDegreeRuleOnce = false; // implies buss rule
    public static boolean dominationRuleOnce = false;

    public static boolean highDegreeRuleAlways = false; // implies buss rule
    public static boolean cliqueBoundAlways = false;
    public static boolean lpBoundAlways = false;
    public static boolean dominationRuleAlways = false;

    static LinkedList<String> vc_branch(Graph graph, int k) {
        if (k < graph.getMaxLowerBound(cliqueBoundAlways, lpBoundAlways)) return null;
        if (k < 0) return null;
        if (graph.isEmpty())
            return new LinkedList<>();

        LinkedList<String> solution;
        recursiveSteps++;

        // Get vertex with the highest degree
        Vertex v = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(v);
        HashMap<Vertex, HashSet<Vertex>> reducedNeighborsMap = graph.applyDominationRule();

        solution = vc_branch(graph, k - 1-reducedNeighborsMap.keySet().size());
        graph.putManyVerticesBack(reducedNeighborsMap);
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
        reducedNeighborsMap = graph.applyDominationRule();

        // Branching with the neighbors
        solution = vc_branch(graph, k - eliminatedNeighbors.size()-reducedNeighborsMap.keySet().size());
        graph.putManyVerticesBack(eliminatedNeighborsMap);
        graph.putManyVerticesBack(reducedNeighborsMap);

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

        // Apply reduction rules before instatiating graph (+ internally used
        // datastructure(s))
//        LinkedList<String> reductionResult = ReductionRules.applyReductionRules(edges);

        // Instantiate graph
        Graph graph = new Graph(edges);

        HashMap<Vertex, HashSet<Vertex>> edgesAfterRules = new HashMap<>();

        if (dominationRuleOnce){
            HashMap<Vertex, HashSet<Vertex>> edgesAfterDominationRule = graph.applyDominationRule();
            edgesAfterRules.putAll(edgesAfterDominationRule);
        }

        // Call method with the clique lower bound
        int lowerbound = graph.getMaxLowerBound(cliqueBoundOnce, lpBoundOnce);

        if (highDegreeRuleOnce){
            HashMap<Vertex, HashSet<Vertex>> edgesAfterHighDegreeRule = graph.applyHighDegreeRule(3);
            edgesAfterRules.putAll(edgesAfterHighDegreeRule);
            boolean vcExists = graph.applyBussRule(lowerbound);
            if (!vcExists){
                lowerbound++;
            }
        }

        LinkedList<String> result = vc(graph, lowerbound);
        // Putting it all together in one String to only use one I/O operation
        StringBuilder sb = new StringBuilder();
        int solutionSize = 0;

        //Add results from reduction rules
//        if (!reductionResult.isEmpty()) {
//            for (String s : reductionResult) {
//                sb.append(s).append("\n");
//                solutionSize++;
//            }
//        }

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
    }

}
