import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static void main(String[] args) throws IOException {

        // Use buffer reader for stdin since the input does not require validation, and it is much faster than scanner

        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        //Data structure used to store graph

        HashMap<String,HashSet<String>> graph = new HashMap<>();

        String line;
        while (((line = bi.readLine()) !=null)){

            // Ignore lines starting with # and empty lines

            if (!line.contains("#") && !line.isEmpty()){

                //Striping line from whitespaces

                String[] nodes = line.split("\\s+");

                if (!graph.containsKey(nodes[0])){
                    graph.put(nodes[0],new HashSet<>());
                }
                graph.get(nodes[0]).add(nodes[1]);
            }


        }

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


    static SolverResult vc_branch(HashMap<String,SolverResult> MEM, HashMap<String,HashSet<String>> graph, int k, SolverResult solverResult){
        if (k<0) return solverResult;
        if (graph.isEmpty()){
            solverResult.setEmptyResultsList();
            return solverResult;
        }

        solverResult.increaseRecursiveSteps();

        // Get random vertex and random neighbor (not some random since it is the first one :)

        String firstVertex = graph.keySet().iterator().next();
        String secondVertex = graph.get(firstVertex).iterator().next();



        // HashSet to store eliminated vertices to add them after the recursive call and avoid copying the graph

        HashSet<String> eliminatedVertices = eliminateVertex(graph,firstVertex);
        SolverResult s = memorization(MEM,graph,k-1, solverResult);
        //SolverResult s = vc_branch(graph,k-1, solverResult);


        //Putting back the eliminated vertices

        graph.put(firstVertex,eliminatedVertices);
        if (s.resultsList != null) {
            s.addVertexToResult(firstVertex);
            return s;
        }

        eliminatedVertices = eliminateVertex(graph,secondVertex);
        s = memorization(MEM,graph,k-1, solverResult);
        //s = vc_branch(graph,k-1, solverResult);

        //Putting back the eliminated vertices

        graph.put(secondVertex,eliminatedVertices);
        if (s.resultsList != null) {
            solverResult.addVertexToResult(secondVertex);
            return solverResult;
        }


        return new SolverResult();

    }

    // Function to eliminate a given vertex of a graph in O(E) It outputs a hashset with all removed vertices

    static HashSet<String> eliminateVertex(HashMap<String,HashSet<String>> graph, String vertex){

        // HashSet to store the vertices we remove to put them back after the recursive call in vc_branch ends
        HashSet<String> removedVertices = new HashSet<>();
        HashSet<String> removedHashSet = graph.remove(vertex);
        if(removedHashSet !=null) removedVertices.addAll(removedHashSet);

        Set<String> set = graph.keySet();
        Iterator<String> iterator = set.iterator();

        while(iterator.hasNext()){
            String v = iterator.next();
            HashSet<String> list = graph.get(v);
            if(list.remove(vertex)) removedVertices.add(v);
            if (list.isEmpty()){
                iterator.remove();
            }
        }

        return removedVertices;

    }

    // Memorization method stores (some) partial results instead of recomputing them again
    public static SolverResult memorization(HashMap<String,SolverResult> MEM, HashMap<String,HashSet<String>> graph, int k, SolverResult r){
        String id = encodeGraph(graph,k);
        SolverResult s = MEM.get(id);
        if (s == null){
            s = vc_branch(MEM,graph, k, r);
            MEM.put(id,s);
        }
        return s;
    }

    // Encode (G,k) as String for memorization
    public static String encodeGraph(HashMap<String,HashSet<String>> graph, int k){
        StringBuilder sb = new StringBuilder();
        ArrayList<String> V = new ArrayList<String>(graph.keySet());
        Collections.sort(V);
        for (String v : V){
            sb.append(v);
            sb.append(":");
            ArrayList<String> neighbours = new ArrayList<String>(graph.get(v));
            Collections.sort(neighbours);
            for (String v2 : neighbours){
                sb.append(v2);
                sb.append("-");
            }
            sb.append(",");
        }
        sb.append("#"+k);
        return sb.toString();
    }

    // main function which increases the cover vertex size k every iteration

    public static SolverResult vc(HashMap<String,HashSet<String>> graph){
        SolverResult  s;
        int k = 0;
        HashMap<String,SolverResult> MEM = new HashMap<String,SolverResult>();  // Memory object for memorization method
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


