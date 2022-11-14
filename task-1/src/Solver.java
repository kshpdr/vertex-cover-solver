import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static void main(String[] args) throws IOException {

        // Use buffer reader for stdin since the input does not require validation, and it is much faster than scanner

        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));


        //Storing edges to call the graph constructor afterwards


        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) !=null)){

            // Ignore lines starting with # and empty lines

            if (!line.contains("#") && !line.isEmpty()){

                //Striping line from whitespaces

                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }


        }

        // Instantiate graph
        Graph graph = new Graph(edges);

        // Call method with the clique lower bound


        SolverResult result = vc(graph, graph.getCliqueLowerBound());

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


    static SolverResult vc_branch(Graph graph, int k, SolverResult solverResult){
        if (k<0) return solverResult;
        if (graph.isEmpty()){
            solverResult.setEmptyResultsList();
            return solverResult;
        }

        solverResult.increaseRecursiveSteps();


        // Get vertex with the highest degree
        Vertex v = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(v);




        SolverResult s = vc_branch(graph,k-1, solverResult);


        graph.putVertexBack(v,eliminatedNeighbors);



        if (s.resultsList != null) {
            s.addVertexToResult(graph.getVertexMapping(v));
            return s;
        }


        //Eliminating the neighbors of the vertex with the highest degree and storing the neighbors of the neighbors with a hashmap

        HashMap<Vertex,HashSet<Vertex>> eliminatedNeighborsMap= graph.removeSetofVertices(eliminatedNeighbors);


        //Branching with the neighbors
        s = vc_branch(graph,k-eliminatedNeighbors.size(), solverResult);

        //Putting back the eliminated vertices

        graph.putManyVerticesBack(eliminatedNeighborsMap);


        if (s.resultsList != null) {
            solverResult.addMultipleVertexToResult(graph.getMultipleMappings(eliminatedNeighbors));
            return solverResult;
        }


        return new SolverResult();

    }


    // main function which increases the cover vertex size k every iteration

    public static SolverResult vc(Graph graph,int lowerBound){
        SolverResult  s = new SolverResult();
        int k = lowerBound;
        //HashMap<String,SolverResult> MEM = new HashMap<>();  // Memory object for memorization method
        while ((vc_branch(graph,k++,s)).resultsList==null){}
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

        private void addMultipleVertexToResult(String[] verticesToAdd){

            Collections.addAll(this.resultsList, verticesToAdd);

        }

        private void increaseRecursiveSteps(){
            this.recursiveSteps++;
        }

        private void setEmptyResultsList(){
            this.resultsList = new LinkedList<>();
        }

    }

}












