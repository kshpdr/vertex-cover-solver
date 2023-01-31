import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConstrainedSolver {

    public static boolean oneDegreeRuleIteration = false; //does not work yet
    public static boolean findComponents = false;
    public static int recursiveSteps = 0;

    public static HashSet<Vertex> solve(Graph graph, Constraints constraints, HashSet<Vertex> solution, HashSet<Vertex> bestFoundSolution){
        HashMap<Vertex, HashSet<Vertex>> reducedNeighborsMap = new HashMap<>();

        if (oneDegreeRuleIteration){
            reducedNeighborsMap.putAll(graph.applyOneDegreeRule());
        }

        if (solution.size() + reducedNeighborsMap.size()+graph.getMaxLowerBound(true, true) >= bestFoundSolution.size()){
            graph.putManyVerticesBack(reducedNeighborsMap);
            return bestFoundSolution;
        }

        if (graph.isEmpty()){
            solution.addAll(reducedNeighborsMap.keySet());
            return solution;
        }

        // It doesn't work yet
        if (findComponents){
            List<Graph> components = graph.getComponents();
            if (components.size() > 1){
                for (Graph component : components) {
                    HashSet<Vertex> componentSolution = new HashSet<>(bestFoundSolution);
                    componentSolution.removeAll(solution);
                    componentSolution = solve(component, constraints, new HashSet<>(), componentSolution);
                    solution.addAll(componentSolution);
                    solution.addAll(reducedNeighborsMap.keySet());
                }
                return bestFoundSolution.size() > solution.size() ? solution : bestFoundSolution;
            }
        }
        recursiveSteps++;

        Vertex vertex = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(vertex);

        solution.add(vertex);
        solution.addAll(reducedNeighborsMap.keySet());
        HashSet<Vertex> tempSolution = solve(graph, constraints, new HashSet<>(solution), bestFoundSolution);
        bestFoundSolution = tempSolution.size() <= bestFoundSolution.size() ? tempSolution : bestFoundSolution;
        graph.putVertexBack(vertex, eliminatedNeighbors);
        solution.remove(vertex);
        for (Vertex neighbor : reducedNeighborsMap.keySet()){
            solution.remove(neighbor);
        }

        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);
        solution.addAll(eliminatedNeighbors);
        solution.addAll(reducedNeighborsMap.keySet());
        tempSolution = solve(graph, constraints, new HashSet<>(solution), bestFoundSolution);
        bestFoundSolution = tempSolution.size() <= bestFoundSolution.size() ? tempSolution : bestFoundSolution;
        graph.putManyVerticesBack(eliminatedNeighborsMap);
        solution.removeAll(eliminatedNeighbors);
        for (Vertex neighbor : reducedNeighborsMap.keySet()){
            solution.remove(neighbor);
        }

        return bestFoundSolution;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        // Storing edges to call the graph constructor afterwards
        HashSet<String[]> edges = new HashSet<>();

        String line;
//        LightGraph lightGraph = new LightGraph();

        //min to min Graph
        HashMap<Vertex,HashSet<Vertex>> adjMap = new HashMap<>();
        HashMap<String,Vertex> idMap = new HashMap<>();
        int idCounter = 0;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                Vertex u = idMap.get(nodes[0]);
                if (u == null){
                    u = new Vertex(nodes[0],idCounter++);
                    idMap.put(nodes[0],u);
                }
                Vertex v = idMap.get(nodes[1]);
                if (v == null){
                    v = new Vertex(nodes[1],idCounter++);
                    idMap.put(nodes[1],v);
                }

                // Add (u -> v) to graph
                HashSet<Vertex> neighbors = adjMap.computeIfAbsent(u, k -> new HashSet<>());
                neighbors.add(v);
                // Add (v -> u) to graph
                neighbors = adjMap.computeIfAbsent(v, k -> new HashSet<>());
                neighbors.add(u);
                edges.add(nodes);
//                Vertex vertex = new Vertex(nodes[0], Integer.parseInt(nodes[0]));
//                Vertex neighbor = new Vertex(nodes[1], Integer.parseInt(nodes[1]));
//                vertices.putIfAbsent(Integer.parseInt(nodes[0]), vertex);
//                vertices.putIfAbsent(Integer.parseInt(nodes[1]), neighbor);
//                lightGraph.addEdge(vertices.get(Integer.parseInt(nodes[0])), vertices.get(Integer.parseInt(nodes[1])));
            }
        }
        HashSet<Vertex> heuristicSolution = MinToMinHeuristic.getUpperBoundMinToMin(adjMap);
        Graph graph = new Graph(edges);

//        HashSet<Vertex> solution = solve(graph, null, new HashSet<>(),heuristicSolution);
        HashSet<Vertex> solution = solve(graph, null, new HashSet<>(),new HashSet<>(heuristicSolution));
        LinkedList<String> stringSolution = FastVC.getStringSolution(solution);

        StringBuilder sb = new StringBuilder();
        for (String v : stringSolution){
            sb.append(v);
            sb.append("\n");
        }
        sb.append("#sol size: ").append(solution.size()).append("\n");
        sb.append("#recursive steps: ").append(recursiveSteps).append("\n");
        String resultStr = sb.toString();
        System.out.print(resultStr);
    }
}
