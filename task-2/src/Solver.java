import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static void main(String[] args) throws IOException {

        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }
        }

        // Apply reduction rules before instatiating graph (+ internally used
        // datastructure(s))
        SolverResult reductionResult = ReductionRules.applyReductionRules(edges);
        Graph graph = new Graph(edges);
        SolverResult result = vc(graph, graph.getMaxLowerBound());

        // Putting it all together in one String to only use one I/O operation
        StringBuilder sb = new StringBuilder();

        // Add results from reduction rules
        if (!reductionResult.resultsList.isEmpty()) {
            for (String s : reductionResult.resultsList) {
                sb.append(s).append("\n");
            }
        }
        // Add results from actual branching algorithm
        if (!result.resultsList.isEmpty()) {
            for (String s : result.resultsList) {

                sb.append(s).append("\n");
            }
        }
        sb.append("#recursive steps: ").append(result.recursiveSteps).append("\n");
        String resultStr = sb.toString();
        System.out.print(resultStr);
    }

    static SolverResult vc_branch(Graph graph, int k, SolverResult solverResult) {
        if (k < 0)
            return solverResult;
        if (graph.isEmpty()) {
            solverResult.setEmptyResultsList();
            return solverResult;
        }

        solverResult.increaseRecursiveSteps();
        Vertex v = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(v);

        SolverResult s = vc_branch(graph, k - 1, solverResult);
        graph.putVertexBack(v, eliminatedNeighbors);
        if (s.resultsList != null) {
            s.addVertexToResult(graph.getVertexMapping(v));
            return s;
        }

        // Eliminating the neighbors of the vertex with the highest degree and storing
        // the neighbors of the neighbors with a hashmap
        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);

        // Branching with the neighbors
        s = vc_branch(graph, k - eliminatedNeighbors.size(), solverResult);

        // Putting back the eliminated vertices
        graph.putManyVerticesBack(eliminatedNeighborsMap);
        if (s.resultsList != null) {
            solverResult.addMultipleVertexToResult(graph.getMultipleMappings(eliminatedNeighbors));
            return solverResult;
        }
        return new SolverResult();
    }

    // main function which increases the cover vertex size k every iteration
    public static SolverResult vc(Graph graph, int lowerBound) {
        SolverResult s = new SolverResult();
        int k = lowerBound;
        while ((vc_branch(graph, k++, s)).resultsList == null) {
        }
        return s;
    }
}
