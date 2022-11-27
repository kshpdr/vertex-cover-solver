import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {

    public static int recursiveSteps = 0;

    public static LinkedList<String> vc_branch(Graph graph, int k) {
        if (k < 0)
            return null;
        if (graph.isEmpty())
            return new LinkedList<>();
        recursiveSteps++;

        LinkedList<String> solution = new LinkedList<>();
        Vertex vertex = graph.getNextNode();
        HashSet<Vertex> neighbors = graph.removeVertex(vertex);

        solution = vc_branch(graph, k - 1);
        graph.putVertexBack(vertex, neighbors);
        if (solution != null) {
            solution.add(vertex.name);
            return solution;
        }

        HashMap<Vertex, HashSet<Vertex>> neighborsVertices = graph.removeVertices(neighbors);
        solution = vc_branch(graph, k - neighbors.size());
        graph.putVertices(neighborsVertices);
        if (solution != null) {
            for (Vertex neighbor : neighbors){
                solution.add(neighbor.name);
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
        HashSet<String[]> edges = new HashSet<>();

        String line;
        while ((!(line = bi.readLine()).isEmpty())){
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }
        }

        // Apply reduction rules before instatiating graph (+ internally used
        // datastructure(s))
//        SolverResult reductionResult = ReductionRules.applyReductionRules(edges);
        Graph graph = new Graph(edges);
        LinkedList<String> solution = vc(graph, 0);

        // Putting it all together in one String to only use one I/O operation
        StringBuilder sb = new StringBuilder();

        // Add results from reduction rules
//        if (!reductionResult.resultsList.isEmpty()) {
//            for (String s : reductionResult.resultsList) {
//                sb.append(s).append("\n");
//            }
//        }
        // Add results from actual branching algorithm
        if (!solution.isEmpty()) {
            for (String s : solution) {
                sb.append(s).append("\n");
            }
        }
        sb.append("#recursive steps: ").append(recursiveSteps).append("\n");
        String resultStr = sb.toString();
        System.out.print(resultStr);
    }
}
