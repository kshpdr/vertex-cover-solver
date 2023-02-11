import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConstrainedSolver {
    // Pre-processing 1
    public static boolean oneDegreeRulePre = true;
    public static boolean twoDegreeRulePre = true;
    public static boolean dominationRulePre = true;

    // Pre-processing 2
    public static boolean unconfinedRuleBeginning = true;
    public static boolean highDegreeRuleBeginning = true;
    public static boolean lpReductionBeginning = true; // still not working

    // Reduction rules
    public static boolean dominationRuleIteration = true;
    public static boolean unconfinedRuleIteration = true;
    public static boolean highDegreeRuleIteration = true;
    public static boolean oneDegreeRuleIteration = true;
    public static boolean twoDegreeRuleIteration = true;
    public static boolean lpReductionIteration = false; // still not working

    // Solver params
    public static boolean findComponents = false; // currently slow
    public static boolean neighborsConstraint = true;
    public static boolean satelliteConstraint = true;

    public static boolean cliqueBoundIteration = true;
    public static boolean lpBoundIteration= true;

    // Tracking params
    public static int recursiveSteps = 0;

    public static boolean constraintsSatisfied(Graph graph, HashSet<Vertex> solution, HashSet<Constraint> constraints){
        for (Constraint constraint : constraints){
            if (!constraint.isSatisfied(graph, solution)){
                return false;
            }
        }
        return true;
    }

    // Think about using SatelliteConstraint only on dense graphs (social-networks)
    public static HashSet<Constraint> createConstraints(HashMap<Vertex, HashSet<Vertex>> initialAdjList){
        HashSet<Constraint> constraints = new HashSet<>();
        if (neighborsConstraint) constraints.add(new NeighborsConstraint(initialAdjList));
        if (satelliteConstraint) constraints.add(new SatelliteConstraint(initialAdjList));
        return constraints;
    }

    public static HashMap<Vertex, HashSet<Vertex>> reduceGraph(Graph graph, int lowerbound){
        HashMap<Vertex, HashSet<Vertex>> reducedEdges = new HashMap<>();
        if (oneDegreeRuleIteration) reducedEdges.putAll(graph.applyOneDegreeRule());
        if (twoDegreeRuleIteration) reducedEdges.putAll(graph.applyTwoDegreeRule());
        if (dominationRuleIteration) reducedEdges.putAll(graph.applyDominationRule());
        if (unconfinedRuleIteration) reducedEdges.putAll(graph.applyUnconfinedRule());
        if (lpReductionIteration) reducedEdges.putAll(graph.applyLpReduction());
        if(highDegreeRuleIteration) reducedEdges.putAll(graph.applyHighDegreeRule(lowerbound));
        return reducedEdges;
    }

    public static HashSet<Vertex> solve(Graph graph, HashSet<Constraint> constraints, HashSet<Vertex> solution, HashSet<Vertex> bestFoundSolution){

        if (!constraintsSatisfied(graph, solution, constraints)){
            return bestFoundSolution;
        }

        int lowerbound = graph.getMaxLowerBound(cliqueBoundIteration, lpBoundIteration);
        if (solution.size() + lowerbound >= bestFoundSolution.size()) {
            return bestFoundSolution;
        }

        HashMap<Vertex, HashSet<Vertex>> reducedEdges = reduceGraph(graph, lowerbound);
        solution.addAll(reducedEdges.keySet());

        if (graph.isEmpty()) {
            graph.putManyVerticesBack(reducedEdges);
            return solution;
        }

        if (findComponents){
            List<Graph> components = graph.getComponents();
            if (components.size() > 1){
                for (Graph component : components) {
                    HashSet<Vertex> componentSolution = new HashSet<>(bestFoundSolution);
                    componentSolution.removeAll(solution);
                    componentSolution = solve(component, createConstraints(component.getAdjVertices()), new HashSet<>(), componentSolution);
                    solution.addAll(componentSolution);
                }
                return bestFoundSolution.size() > solution.size() ? solution : bestFoundSolution;
            }
        }
        recursiveSteps++;

        // update vertices in the constraints
        constraints = createConstraints(graph.getAdjVertices());

        // Branch vertex (v)
        Vertex vertex = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(vertex);

        solution.add(vertex);
        HashSet<Vertex> tempSolution = solve(graph, constraints, new HashSet<>(solution), bestFoundSolution);
        bestFoundSolution = tempSolution.size() <= bestFoundSolution.size() ? tempSolution : bestFoundSolution;
        graph.putVertexBack(vertex, eliminatedNeighbors);
        solution.remove(vertex);

        // Branch vertices N(v)
        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);
        solution.addAll(eliminatedNeighbors);
        tempSolution = solve(graph, constraints, new HashSet<>(solution), bestFoundSolution);
        bestFoundSolution = tempSolution.size() <= bestFoundSolution.size() ? tempSolution : bestFoundSolution;
        graph.putManyVerticesBack(eliminatedNeighborsMap);
        solution.removeAll(eliminatedNeighbors);

        graph.putManyVerticesBack(reducedEdges);

        return bestFoundSolution;
    }

    public static void main(String[] args) throws IOException {
        // get input for the graph
        InputParser inputParser = new InputParser();
        HashSet<String[]> edges = inputParser.getEdges();
        HashMap<Vertex,HashSet<Vertex>> adjMap = inputParser.getAdjMap();

        // complete preprocessing phase 1
        ReductionRules preReduction = new ReductionRules(oneDegreeRulePre,twoDegreeRulePre,dominationRulePre);
        LinkedList<String> reductionResult = preReduction.applyReductionRules(edges);
        Graph graph = new Graph(edges);

        // complete preprocessing phase 2
        HashMap<Vertex, HashSet<Vertex>> reducedEdges = new HashMap<>();
        if(unconfinedRuleBeginning) {
            reducedEdges.putAll(graph.applyUnconfinedRule());
        }
        if(lpReductionBeginning){
            reducedEdges.putAll(graph.applyLpReduction());
        }
        if (highDegreeRuleBeginning){
            int lowerbound = graph.getMaxLowerBound(false, true);
            reducedEdges.putAll(graph.applyHighDegreeRule(lowerbound));
        }

        // get params for the algorithm
        HashSet<Vertex> heuristicSolution = MinToMinHeuristic.getUpperBoundMinToMin(adjMap);
        HashSet<Constraint> constraints = createConstraints(graph.getAdjVertices());

        HashSet<Vertex> solution = solve(graph, constraints, new HashSet<>(), new HashSet<>(heuristicSolution));
        LinkedList<String> stringSolution = FastVC.getStringSolution(solution);

        // merge all results
        if (!reductionResult.isEmpty()) {
            stringSolution.addAll(reductionResult);
        }
        for (Vertex v : reducedEdges.keySet()){
            stringSolution.add(v.name);
        }
        if (twoDegreeRulePre){ // must be last to undo merge for all merged cases
            preReduction.undoMerge(stringSolution);
        }

        inputParser.printResult(stringSolution, recursiveSteps);
    }
}
