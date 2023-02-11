import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Solver {
    public static boolean oneDegreeRulePre = true;
    public static boolean twoDegreeRulePre = true;
    public static boolean dominationRulePre = true;

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
        // TODO: make use of upperBound => actually not used here ... but in "other branching strategy"

        while (true) {
            LinkedList<String> solution = vc_branch(graph, lowerBound);
            if (solution != null){
                return solution;
            }
            lowerBound++;
        }
    }

    public static void main(String[] args) throws IOException {
        ConstrainedSolver.main(args);
    }
}
