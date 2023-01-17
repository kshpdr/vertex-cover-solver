import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConstrainedSolver {

    public static int solve(Graph graph, Constraints constraints, int solsize, int upperbound){
//        LinkedList<String> solution;

        if (solsize + graph.getMaxLowerBound(false, true) >= upperbound){
            return upperbound;
        }

        if (graph.isEmpty()) return solsize;

        List<Graph> components = graph.getComponents();
        if (components.size() > 1){
            for (Graph component : components){
                solsize += solve(component, constraints, 0, upperbound - solsize);
            }
            return Math.min(solsize, upperbound);
        }

        Vertex vertex = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(vertex);
        upperbound = solve(graph, constraints, solsize + 1, upperbound);
        graph.putVertexBack(vertex, eliminatedNeighbors);

//        if (solution != null) {
//            solution.add(vertex.name);
//            return solution;
//        }

        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);
        upperbound = solve(graph, constraints, solsize + eliminatedNeighbors.size(), upperbound);
        graph.putManyVerticesBack(eliminatedNeighborsMap);
        return upperbound;
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
        LinkedList<String> solution = FastVC.fastVertexCover(lightGraph, 50);
        int upperbound = solve(graph, null,0, solution.size());
        System.out.println(upperbound);
    }
}
