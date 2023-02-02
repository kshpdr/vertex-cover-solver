import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConstrainedSolver {
    public static boolean findComponents = true;

    public static boolean constraintsSatisfied(HashSet<Vertex> solution, HashSet<Constraint> constraints){
        for (Constraint constraint : constraints){
            if (!constraint.isSatisfied(solution)){
                return false;
            }
        }
        return true;
    }

    public static HashSet<Constraint> createConstraints(HashMap<Vertex, HashSet<Vertex>> initialAdjList){
        HashSet<Constraint> constraints = new HashSet<>();
        constraints.add(new NeighborsConstraint(initialAdjList));
        return constraints;
    }

    public static HashSet<Vertex> solve(Graph graph, HashSet<Constraint> constraints, HashSet<Vertex> solution, HashSet<Vertex> bestFoundSolution){

        if (!constraintsSatisfied(solution, constraints)) return bestFoundSolution;

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
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        // Storing edges to call the graph constructor afterwards
        HashSet<String[]> edges = new HashSet<>();

        String line;
        LightGraph lightGraph = new LightGraph();
        HashMap<Integer, Vertex> vertices = new HashMap<>();
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                edges.add(nodes);
                Vertex vertex = new Vertex(nodes[0], Integer.parseInt(nodes[0]));
                Vertex neighbor = new Vertex(nodes[1], Integer.parseInt(nodes[1]));
                vertices.putIfAbsent(Integer.parseInt(nodes[0]), vertex);
                vertices.putIfAbsent(Integer.parseInt(nodes[1]), neighbor);
                lightGraph.addEdge(vertices.get(Integer.parseInt(nodes[0])), vertices.get(Integer.parseInt(nodes[1])));
            }
        }
        Graph graph = new Graph(edges);
        HashSet<Vertex> heuristicSolution = FastVC.fastVertexCoverHashset(lightGraph, 50);

        HashSet<Constraint> constraints = new HashSet<>();
        constraints.add(new NeighborsConstraint(graph.getAdjVertices()));

        HashSet<Vertex> solution = solve(graph, constraints, new HashSet<>(), new HashSet<>(heuristicSolution));
        LinkedList<String> stringSolution = FastVC.getStringSolution(solution);

        StringBuilder sb = new StringBuilder();
        for (String v : stringSolution){
            sb.append(v);
            sb.append("\n");
        }
        sb.append("#sol size: ").append(solution.size()).append("\n");
        String resultStr = sb.toString();
        System.out.print(resultStr);
    }
}
