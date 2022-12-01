import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static boolean cliqueBound = false;
    public static boolean lpBound = false;
    public static boolean zeroDegreeRule = true;
    public static boolean oneDegreeRule = true;
    public static boolean twoDegreeRule = true;
    public static boolean preGraphReduction = true;
    public static boolean highDegreeRule = false;
    public static boolean bussRule = false;
    public static boolean dominationRule = false;
    public static int recursiveSteps = 0;

    static LinkedList<String> vc_branch(Graph graph, int k) {
        //System.out.println("k: " + k + " Clique Lower Bound: " + graph.getCliqueLowerBound());
        if(k < graph.getMaxLowerBound(false, false)) return null;
//        if(k < graph.getLpBound()) return null;

        if (k < 0) return null;
        if (graph.isEmpty())
            return new LinkedList<>();

        LinkedList<String> solution;
        recursiveSteps++;

        // Get vertex with the highest degree
        Vertex v = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(v);
        HashMap<Vertex, HashSet<Vertex>> reducedNeighborsMap = dominationRule ? graph.applyDominationRule() : new HashMap<>();

        solution = vc_branch(graph, k - 1-reducedNeighborsMap.keySet().size());
        graph.putManyVerticesBack(reducedNeighborsMap);
        graph.putVertexBack(v, eliminatedNeighbors);


        if (solution!= null) {
            solution.add(graph.getVertexMapping(v));
            for (Vertex neighbor : reducedNeighborsMap.keySet()){
                solution.add(graph.getVertexMapping(neighbor));
            }
            return solution;
        }

        // Eliminating the neighbors of the vertex with the highest degree and storing
        // the neighbors of the neighbors with a hashmap
        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);
        if (dominationRule) reducedNeighborsMap = graph.applyDominationRule();

        // Branching with the neighbors
        solution = vc_branch(graph, k - eliminatedNeighbors.size()-reducedNeighborsMap.keySet().size());
        graph.putManyVerticesBack(eliminatedNeighborsMap);
        graph.putManyVerticesBack(reducedNeighborsMap);

        // Putting back the eliminated vertices
        if (solution != null) {
            for (Vertex neighbor : eliminatedNeighborsMap.keySet()){
                solution.add(graph.getVertexMapping(neighbor));
            }
            for (Vertex neighbor : reducedNeighborsMap.keySet()){
                solution.add(graph.getVertexMapping(neighbor));
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
        int verticesAmount = 0;
        int edgesAmount = 0;
        while (((line = bi.readLine()) != null)) {
            if (line.contains("#")){
                String[] info = line.split("\\s+");
                verticesAmount = Integer.parseInt(info[0].substring(1));
                edgesAmount = Integer.parseInt(info[1]);
            }
            else if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }
        }

        LinkedList<String> reductionResult = null;
        if (preGraphReduction) {
            // Apply reduction rules before instatiating graph (+ internally used datastructure(s))
            ReductionRules r = new ReductionRules(zeroDegreeRule,oneDegreeRule,twoDegreeRule);
            reductionResult = r.applyReductionRules(edges);
            // Decrease counter variables for vertices and edges
            verticesAmount -= reductionResult.size();
            for (String[] edge : edges){
                if (reductionResult.contains(edge[0]) || reductionResult.contains(edge[1])){
                    edgesAmount -= 1;
                }
            }
        }

        // Instantiate graph
        Graph graph = new Graph(verticesAmount, edgesAmount, edges);

        HashMap<Vertex, HashSet<Vertex>> edgesAfterRules = new HashMap<>();
        HashMap<Vertex, HashSet<Vertex>> edgesAfterDominationRule = dominationRule ? graph.applyDominationRule() : new HashMap<>();
        edgesAfterRules.putAll(edgesAfterDominationRule);

        // Call method with the clique lower bound
        int lowerbound = graph.getMaxLowerBound(cliqueBound, lpBound);

        if (highDegreeRule){
            HashMap<Vertex, HashSet<Vertex>> edgesAfterHighDegreeRule = graph.applyHighDegreeRule(lowerbound);
            edgesAfterRules.putAll(edgesAfterHighDegreeRule);
        }

        if (zeroDegreeRule){
            HashMap<Vertex, HashSet<Vertex>> edgesAfterZeroDegreeRule = graph.applyZeroDegreeRule();
            edgesAfterRules.putAll(edgesAfterZeroDegreeRule);
        }

        LinkedList<String> result = vc(graph, lowerbound);
        // Putting it all together in one String to only use one I/O operation
        StringBuilder sb = new StringBuilder();
        int solutionSize = 0;

        //Add results from reduction rules
        if (reductionResult != null) {
            for (String s : reductionResult) {
                sb.append(s).append("\n");
                solutionSize++;
            }
        }

        //Add results from Domination rule
        for(Vertex vertex: edgesAfterRules.keySet()){
            sb.append(graph.getVertexMapping(vertex)).append("\n");
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
