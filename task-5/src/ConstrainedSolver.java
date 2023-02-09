import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConstrainedSolver {
    public static boolean oneDegreeRulePre = true;
    public static boolean twoDegreeRulePre = true;
    public static boolean dominationRulePre = true;

    public static boolean findComponents = false;
    public static boolean neighborsConstraint = true;
    public static boolean satelliteConstraint = true;

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

    public static void updateSatelliteConstraint(Graph graph, HashSet<Constraint> constraints){
        for (Constraint constraint : constraints){
            if (constraint instanceof SatelliteConstraint){
                constraints.remove(constraint);
                constraints.add(new SatelliteConstraint(graph.getAdjVertices()));
                break;
            }
        }
    }

    public static HashSet<Vertex> solve(Graph graph, HashSet<Constraint> constraints, HashSet<Vertex> solution, HashSet<Vertex> bestFoundSolution){

        if (!constraintsSatisfied(graph, solution, constraints)) return bestFoundSolution;

        if (solution.size() + graph.getMaxLowerBound(false, true) >= bestFoundSolution.size()){
            return bestFoundSolution;
        }

        if (graph.isEmpty()) return solution;

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

        // update graph for satellite constraint
        updateSatelliteConstraint(graph, constraints);

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

        return bestFoundSolution;
    }

    public static void main(String[] args) throws IOException {
        // get input for the graph
        InputParser inputParser = new InputParser();
        HashSet<String[]> edges = inputParser.getEdges();
        HashMap<Vertex,HashSet<Vertex>> adjMap = inputParser.getAdjMap();

        // complete preprocessing
        ReductionRules preReduction = new ReductionRules(oneDegreeRulePre,twoDegreeRulePre,dominationRulePre);
        LinkedList<String> reductionResult = preReduction.applyReductionRules(edges);
        Graph graph = new Graph(edges);

        // get params for the algorithm
        HashSet<Vertex> heuristicSolution = MinToMinHeuristic.getUpperBoundMinToMin(adjMap);
        HashSet<Constraint> constraints = createConstraints(graph.getAdjVertices());

        HashSet<Vertex> solution = solve(graph, constraints, new HashSet<>(), new HashSet<>(heuristicSolution));
        LinkedList<String> stringSolution = FastVC.getStringSolution(solution);

        // merge all results
        if (!reductionResult.isEmpty()) {
            stringSolution.addAll(reductionResult);
        }
        if (twoDegreeRulePre){
            preReduction.undoMerge(stringSolution);
        }

        inputParser.printResult(stringSolution);
    }
}
