import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConstrainedSolver {

    public static HashSet<Vertex> solve(Graph graph, Constraints constraints, HashSet<Vertex> solution, int upperbound){

        if (solution.size() + graph.getMaxLowerBound(false, true) >= upperbound){
            return null;
        }

        if (graph.isEmpty()) return new HashSet<>();

        List<Graph> components = graph.getComponents();
//        if (components.size() > 1){
//            for (Graph component : components){
//                HashSet<Vertex> componentSolution = solve(component, constraints, new HashSet<>(), upperbound - solution.size());
//                if (componentSolution != null) solution.addAll(componentSolution);
//            }
//            if (solution.size() > upperbound){
//                return null;
//            }
//            }
//            return solution;
//        }

        Vertex vertex = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(vertex);

        HashSet<Vertex> firstTempSolution = new HashSet<>(solution);
        HashSet<Vertex> secondTempSolution = new HashSet<>(solution);
        firstTempSolution.add(vertex);
        secondTempSolution.addAll(eliminatedNeighbors);

        firstTempSolution = solve(graph, constraints, firstTempSolution, upperbound);
        graph.putVertexBack(vertex, eliminatedNeighbors);

        if (firstTempSolution != null) {
//            solution.add(vertex);
            solution = firstTempSolution;
            return solution;
        }

        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);
        secondTempSolution = solve(graph, constraints, secondTempSolution, upperbound);
        graph.putManyVerticesBack(eliminatedNeighborsMap);

        if (secondTempSolution != null && secondTempSolution.size() < solution.size()) {
            solution = secondTempSolution;
            return solution;
        }

        return null;
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
        List<Graph> components = graph.getComponents();
        LinkedList<String> heuristicSolution = FastVC.fastVertexCover(lightGraph, 50);
        HashSet<Vertex> solution = solve(graph, null, new HashSet<>(), heuristicSolution.size());

        StringBuilder sb = new StringBuilder();
        assert solution != null;
        for (Vertex vertex : solution){
            sb.append(vertex.name);
            sb.append("\n");
        }
        sb.append("#sol size: ").append(solution.size()).append("\n");
        String resultStr = sb.toString();
        System.out.print(resultStr);
    }
}
