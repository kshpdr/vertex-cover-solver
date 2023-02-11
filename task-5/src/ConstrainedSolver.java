import java.io.IOException;
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
    public static boolean lpReductionBeginning = false; // still not working

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
    public static int recursionDepth = 0;

    public static int applyReductionDepth = 1;
    public static int applyBoundsDepth = 1;

    public static boolean useSMAC = true;

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
        if (recursionDepth%applyReductionDepth == 0){
            if (dominationRuleIteration) reducedEdges.putAll(graph.applyDominationRule());
            if (unconfinedRuleIteration) reducedEdges.putAll(graph.applyUnconfinedRule());
            if (lpReductionIteration) reducedEdges.putAll(graph.applyLpReduction());
        }
        if(highDegreeRuleIteration) reducedEdges.putAll(graph.applyHighDegreeRule(lowerbound));
        return reducedEdges;
    }

    public static HashSet<Vertex> solve(Graph graph, HashSet<Constraint> constraints, HashSet<Vertex> solution, HashSet<Vertex> bestFoundSolution){

        if (!constraintsSatisfied(graph, solution, constraints)){
            return bestFoundSolution;
        }

        int lowerbound = recursionDepth%applyBoundsDepth == 0 ? graph.getMaxLowerBound(cliqueBoundIteration, lpBoundIteration) : 0;
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
        recursionDepth++;
        HashSet<Vertex> tempSolution = solve(graph, constraints, new HashSet<>(solution), bestFoundSolution);
        recursionDepth--;
        bestFoundSolution = tempSolution.size() <= bestFoundSolution.size() ? tempSolution : bestFoundSolution;
        graph.putVertexBack(vertex, eliminatedNeighbors);
        solution.remove(vertex);

        // Branch vertices N(v)
        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);
        solution.addAll(eliminatedNeighbors);
        recursionDepth++;
        tempSolution = solve(graph, constraints, new HashSet<>(solution), bestFoundSolution);
        recursionDepth--;
        bestFoundSolution = tempSolution.size() <= bestFoundSolution.size() ? tempSolution : bestFoundSolution;
        graph.putManyVerticesBack(eliminatedNeighborsMap);
        solution.removeAll(eliminatedNeighbors);

        graph.putManyVerticesBack(reducedEdges);

        return bestFoundSolution;
    }

    public static void parseArguments(String[] args){
        for (int i=0;i<args.length-1;i++){
            String key = args[i];
            String val = args[i+1];
            if (key.equals("-oneDegreeRulePre")) oneDegreeRulePre = Boolean.parseBoolean(val);
            else if (key.equals("-twoDegreeRulePre")) twoDegreeRulePre = Boolean.parseBoolean(val);
            else if (key.equals("-dominationRulePre")) dominationRulePre = Boolean.parseBoolean(val);
            //else if (key.equals("-lpBoundBeginning")) lpBoundBeginning = Boolean.parseBoolean(val);
            //else if (key.equals("-cliqueBoundBeginning")) cliqueBoundBeginning = Boolean.parseBoolean(val);
            else if (key.equals("-unconfinedRuleBeginning")) unconfinedRuleBeginning = Boolean.parseBoolean(val);
            else if (key.equals("-highDegreeRuleBeginning")) highDegreeRuleBeginning = Boolean.parseBoolean(val);
            else if (key.equals("-lpReductionBeginning")) lpReductionBeginning = Boolean.parseBoolean(val);
            
            else if (key.equals("-cliqueBoundIteration")) cliqueBoundIteration = Boolean.parseBoolean(val);
            else if (key.equals("-lpBoundIteration")) lpBoundIteration = Boolean.parseBoolean(val);
            
            else if (key.equals("-dominationRuleIteration")) dominationRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("-unconfinedRuleIteration")) unconfinedRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("-highDegreeRuleIteration")) highDegreeRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("-oneDegreeRuleIteration")) oneDegreeRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("-twoDegreeRuleIteration")) twoDegreeRuleIteration = Boolean.parseBoolean(val);
            else if (key.equals("-lpReductionIteration")) lpReductionIteration = Boolean.parseBoolean(val);
            else if (key.equals("-applyReductionDepth")) applyReductionDepth = Integer.parseInt(val);
            else if (key.equals("-applyBoundsDepth")) applyBoundsDepth = Integer.parseInt(val);
            //else if (key.equals("-depthThresholdReduction")) depthThresholdReduction = Integer.parseInt(val);
            //else if (key.equals("-depthThresholdBounds")) depthThresholdBounds = Integer.parseInt(val);
            //else if (key.equals("-unconfinedRuleBeginningExhaustively")) unconfinedRuleBeginningExhaustively = Boolean.parseBoolean(val);
            //else if (key.equals("-highDegreeRuleBeginningExhaustively")) highDegreeRuleBeginningExhaustively = Boolean.parseBoolean(val);
            //else if (key.equals("-lpReductionBeginningExhaustively")) lpReductionBeginningExhaustively = Boolean.parseBoolean(val);
            //else if (key.equals("-dominationRuleIterationExhaustively")) dominationRuleIterationExhaustively = Boolean.parseBoolean(val);
            //else if (key.equals("-unconfinedRuleIterationExhaustively")) unconfinedRuleIterationExhaustively = Boolean.parseBoolean(val);
            //else if (key.equals("-highDegreeRuleIterationExhaustively")) highDegreeRuleIterationExhaustively = Boolean.parseBoolean(val);
            //else if (key.equals("-lpReductionIterationExhaustively")) lpReductionIterationExhaustively = Boolean.parseBoolean(val);
        }
    }


    public static void main(String[] args) throws IOException { 
        // Parse command-line arguemnts
        parseArguments(args);
        String filePath = null;
        if (useSMAC && args.length > 0) filePath = args[0];

        // get input for the graph
        InputParser inputParser = new InputParser(filePath);
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

        recursionDepth++;
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
