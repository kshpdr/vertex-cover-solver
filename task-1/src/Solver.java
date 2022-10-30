import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static void main(String[] args) throws IOException {

        // Use buffer reader for stdin since the input does not require validation, and it is much faster than scanner

        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        //Data structure used to store graph


        Set<String> vertices = new HashSet<>();
        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) !=null)){

            // Ignore lines starting with # and empty lines

            if (!line.contains("#") && !line.isEmpty()){

                //Striping line from whitespaces

                String[] nodes = line.split("\\s+");
                vertices.add(nodes[0]);
                vertices.add(nodes[1]);
                edges.add(nodes);
            }


        }
        Graph graph = new Graph(vertices,edges);
        // storing the results in a LinkedList

        SolverResult result = vc(graph);

        // Putting it all together in one String to only use one I/O operation

        StringBuilder sb = new StringBuilder();

        if (!result.resultsList.isEmpty()){
            for (String s : result.resultsList) {

                sb.append(s).append("\n");
            }
        }

        sb.append("#recursive steps: ").append(result.recursiveSteps).append("\n");

        String resultStr = sb.toString();
        System.out.print(resultStr);
    }


    static SolverResult vc_branch(HashMap<String,SolverResult> MEM, Graph graph, int k, SolverResult solverResult){
        if (k<0) return solverResult;
        if (graph.isEmpty()){
            solverResult.setEmptyResultsList();
            return solverResult;
        }

        solverResult.increaseRecursiveSteps();

        // Get random vertex and random neighbor (not some random since it is the first one :)

        Vertex[] randomEdge = graph.getRandomEdge();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(randomEdge[0]);




        SolverResult s = memorization(MEM,graph,k-1, solverResult);

        //Putting back the eliminated vertices

        graph.putVertexBack(randomEdge[0],eliminatedNeighbors);
        if (s.resultsList != null) {
            s.addVertexToResult(graph.getVertexMapping(randomEdge[0]));
            return s;
        }

        eliminatedNeighbors = graph.removeVertex(randomEdge[1]);
        s = memorization(MEM,graph,k-1, solverResult);
        //s = vc_branch(graph,k-1, solverResult);

        //Putting back the eliminated vertices

        graph.putVertexBack(randomEdge[1],eliminatedNeighbors);
        if (s.resultsList != null) {
            solverResult.addVertexToResult(graph.getVertexMapping(randomEdge[1]));
            return solverResult;
        }


        return new SolverResult();

    }



    // Memorization method stores (some) partial results instead of recomputing them again
    public static SolverResult memorization(HashMap<String,SolverResult> MEM, Graph graph, int k, SolverResult r){
        String id = graph.encodeGraph(k);
        SolverResult s = MEM.get(id);
        if (s == null){
            s = vc_branch(MEM,graph, k, r);
            MEM.put(id,s);
        }
        return s;
    }

    // Encode (G,k) as String for memorization
//    public static String encodeGraph(HashMap<String,HashSet<String>> graph, int k){
//        StringBuilder sb = new StringBuilder();
//        ArrayList<String> V = new ArrayList<String>(graph.keySet());
//        Collections.sort(V);
//        for (String v : V){
//            sb.append(v);
//            sb.append(":");
//            ArrayList<String> neighbours = new ArrayList<String>(graph.get(v));
//            Collections.sort(neighbours);
//            for (String v2 : neighbours){
//                sb.append(v2);
//                sb.append("-");
//            }
//            sb.append(",");
//        }
//        sb.append("#"+k);
//        return sb.toString();
//    }

    // main function which increases the cover vertex size k every iteration

    public static SolverResult vc(Graph graph){
        SolverResult  s;
        int k = 0;
        HashMap<String,SolverResult> MEM = new HashMap<>();  // Memory object for memorization method
        while ((s = memorization(MEM,graph,k++,new SolverResult())).resultsList==null);
        return s;
    }

    static class SolverResult{
        private LinkedList<String> resultsList = null;
        private int recursiveSteps;

        SolverResult() {

        }

        private void addVertexToResult(String vertexToAdd){
            this.resultsList.add(vertexToAdd);
        }

        private void increaseRecursiveSteps(){
            this.recursiveSteps++;
        }

        private void setEmptyResultsList(){
            this.resultsList = new LinkedList<>();
        }

    }
}












