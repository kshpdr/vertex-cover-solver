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

        // Apply reduction rules before instatiating graph (+ internally used
        // datastructure(s))
        LinkedList<String> reductionResult = ReductionRules.applyReductionRules(edges);
//
        // Instantiate graph
        Graph graph = new Graph(edges);
        HashMap<Vertex, HashSet<Vertex>> reducedNeighborsMap = graph.applyDominationRule();

        // Call method with the clique lower bound
       int lowerbound = graph.getMaxLowerBound();
//
        LinkedList<String> result = vc(graph, lowerbound);

        // Putting it all together in one String to only use one I/O operation

        StringBuilder sb = new StringBuilder();
        int solutionSize = 0;

//         //Add results from reduction rules
        if (!reductionResult.isEmpty()) {
            for (String s : reductionResult) {
                sb.append(s).append("\n");
                solutionSize++;
            }
        }
//
//
//        //Add results from Domination rule
        for(Vertex vertex: reducedNeighborsMap.keySet()){
            sb.append(graph.getVertexMapping(vertex)).append("\n");
            solutionSize++;
        }
        // Add results from actual branching algorithm
        if (!result.isEmpty()) {
            for (String s : result) {
                solutionSize++;
                sb.append(s).append("\n");
            }
        }

        sb.append("#recursive steps: ").append(recursiveSteps).append("\n");
        sb.append("#sol size: ").append(solutionSize).append("\n");

        String resultStr = sb.toString();
        System.out.print(resultStr);
    }

    public static int recursiveSteps = 0;

    static LinkedList<String> vc_branch(Graph graph, int k) {
        //System.out.println("k: " + k + " Clique Lower Bound: " + graph.getCliqueLowerBound());
        if(k < graph.getCliqueLowerBound()) return null;
//        if(k < graph.getLpBound()) return null;
//
        if (k < 0) return null;
        if (graph.isEmpty())
            return new LinkedList<>();

        LinkedList<String> solution;

        recursiveSteps++;

        // Get vertex with the highest degree
        Vertex v = graph.getNextNode();
        //
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(v);
        HashMap<Vertex, HashSet<Vertex>> reducedNeighborsMap = graph.applyDominationRule();
//
        solution = vc_branch(graph, k - 1-reducedNeighborsMap.keySet().size());
        graph.putManyVerticesBack(reducedNeighborsMap);
        graph.putVertexBack(v, eliminatedNeighbors);
//

        if (solution!= null) {
            solution.add(graph.getVertexMapping(v));
            for (Vertex neighbor : reducedNeighborsMap.keySet()){
                solution.add(graph.getVertexMapping(neighbor));
            }
            return solution;
        }


        // Eliminating the neighbors of the vertex with the highest degree and storing
        // the neighbors of the neighbors with a hashmap

        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);
        reducedNeighborsMap = graph.applyDominationRule();

        // Branching with the neighbors
        solution = vc_branch(graph, k - eliminatedNeighbors.size()-reducedNeighborsMap.keySet().size());

        graph.putManyVerticesBack(eliminatedNeighborsMap);
        graph.putManyVerticesBack(reducedNeighborsMap);

        // Putting back the eliminated vertices



        if (solution != null) {
            for (Vertex neighbor : eliminatedNeighborsMap.keySet()){
                solution.add(graph.getVertexMapping(neighbor));
            }
            for (Vertex neighbor : reducedNeighborsMap.keySet()){
                solution.add(graph.getVertexMapping(neighbor));
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

}
