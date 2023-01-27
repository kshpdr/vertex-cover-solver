import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConstrainedSolver {

    public static HashSet<Vertex> solve(Graph graph, Constraints constraints, HashSet<Vertex> solution, HashSet<Vertex> bestFoundSolution){

        if (solution.size() + graph.getMaxLowerBound(false, true) >= bestFoundSolution.size()){
            return bestFoundSolution;
        }

        if (graph.isEmpty()) return solution;

        // It doesn't work yet
//        List<Graph> components = graph.getComponents();
//        if (components.size() > 1){
//            for (Graph component : components){
//                HashSet<Vertex> componentSolution = new HashSet<>(bestFoundSolution);
//                componentSolution.removeAll(solution);
//                solution.addAll(solve(component, constraints, new HashSet<>(), componentSolution));
//            }
//            return bestFoundSolution.size() >= solution.size() ? solution : bestFoundSolution;
//        }

        Vertex vertex = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(vertex);

        solution.add(vertex);
        HashSet<Vertex> tempSolution = solve(graph, constraints, new HashSet<>(solution), bestFoundSolution);
        bestFoundSolution = tempSolution.size() <= bestFoundSolution.size() ? tempSolution : bestFoundSolution;
        graph.putVertexBack(vertex, eliminatedNeighbors);
        solution.remove(vertex);

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
        List<Graph> components = graph.getComponents();
        HashSet<Vertex> heuristicSolution = FastVC.fastVertexCoverHashset(lightGraph, 50);
        HashSet<Vertex> solution = solve(graph, null, new HashSet<>(), new HashSet<>(heuristicSolution));
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
