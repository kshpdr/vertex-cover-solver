import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static void main(String[] args) throws IOException {

        // Use buffer reader for stdin since the input does not require validation, and
        // it is much faster than scanner

        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        // Storing edges to call the graph constructor afterwards

        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) != null)) {

            // Ignore lines starting with # and empty lines

            if (!line.contains("#") && !line.isEmpty()) {

                // Striping line from whitespaces

                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }

        }

        // Apply reduction rules before instatiating graph (+ internally used datastructure(s))
        SolverResult reductionResult = ReductionRules.applyReductionRules(edges);
        
        // Instantiate graph
        Graph graph = new Graph(edges);

        // Call method with the clique lower bound

        SolverResult result = vc(graph, graph.getMaxLowerBound());

        // Putting it all together in one String to only use one I/O operation

        StringBuilder sb = new StringBuilder();

         //Add results from reduction rules
        if (!reductionResult.resultsList.isEmpty()) {
            for (String s : reductionResult.resultsList) {
                sb.append(s).append("\n");
            }
        }
        // Add results from actual branching algorithm
        if (result.resultsList != null && !result.resultsList.isEmpty()) {
            for (String s : result.resultsList) {

                sb.append(s).append("\n");
            }
        }

        sb.append("#recursive steps: ").append(result.recursiveSteps).append("\n");

        String resultStr = sb.toString();
        System.out.print(resultStr);
    }

    static SolverResult vc_branch(Graph graph, int k, SolverResult solverResult) {
        // Initialize solver result for reduction rules
        SolverResult reductionResult = new SolverResult();
        reductionResult.setEmptyResultsList();
        // Apply Reduction rules (save deleted vertices+neighbors) and reduce k accordingly
        HashMap<Vertex,HashSet<Vertex>> removedReductionVerticesMap = graph.applyReductionRules(reductionResult);
        k -= reductionResult.resultsList.size();
        
        //System.out.println("k: " + k + " Clique Lower Bound: " + graph.getCliqueLowerBound());
        if(k < graph.getCliqueLowerBound() || k < graph.getLpBound() || k < 0) {
            // Undo changes done from reduction rules
            graph.putManyVerticesBack(removedReductionVerticesMap);
            return solverResult;
        }
        
        if (graph.isEmpty()) {
            solverResult.setEmptyResultsList();
            // Add vertices reduced (via reduction rules) to result list
            for (String v : reductionResult.resultsList) solverResult.addVertexToResult(v);
            return solverResult;
        }

        solverResult.increaseRecursiveSteps();

        // Get vertex with the highest degree
        Vertex v = graph.getNextNode();

        // Branch Case: G\v
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(v);
        SolverResult s = vc_branch(graph, k - 1, solverResult);
        graph.putVertexBack(v, eliminatedNeighbors);

        if (s.resultsList != null) {
            s.addVertexToResult(graph.getVertexMapping(v));
            // Add vertices reduced (via reduction rules) to result list
            for (String r : reductionResult.resultsList) s.addVertexToResult(r);
            return s;
        }

        // Branch Case: G\N(v)
        // Eliminating the neighbors of the vertex with the highest degree and storing
        // the neighbors of the neighbors with a hashmap
        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);
        // Branching with the neighbors
        s = vc_branch(graph, k - eliminatedNeighbors.size(), solverResult);
        // Putting back the eliminated vertices
        graph.putManyVerticesBack(eliminatedNeighborsMap);

        if (s.resultsList != null) {
            solverResult.addMultipleVertexToResult(graph.getMultipleMappings(eliminatedNeighbors));
            // Add vertices reduced (via reduction rules) to result list
            for (String r : reductionResult.resultsList) solverResult.addVertexToResult(r);
            return solverResult;
        }

        // Undo changes done from reduction rules
        graph.putManyVerticesBack(removedReductionVerticesMap);

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

    static class SolverResult {
        private LinkedList<String> resultsList = null;
        private int recursiveSteps;

        SolverResult() {

        }

        protected void addVertexToResult(String vertexToAdd) {
            this.resultsList.add(vertexToAdd);
        }

        private void addMultipleVertexToResult(String[] verticesToAdd) {

            Collections.addAll(this.resultsList, verticesToAdd);

        }

        private void increaseRecursiveSteps() {
            this.recursiveSteps++;
        }

        protected void setEmptyResultsList() {
            this.resultsList = new LinkedList<>();
        }

    }

}
