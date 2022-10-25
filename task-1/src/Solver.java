// Very bad implementation of vertex cover algorithm using bad data structure for graphs and using deep copies for recursion.
// To see the results of the benchmark script look for file results.csv. Many of the tests could not be finished in the time constraint
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static void main(String[] args) throws IOException {

        // Scan the stdin and put it in a Hashmap

//        Scanner scanner = new Scanner(System.in);
//        scanner.nextLine();


        // Use buffer reader since the input does not require validation, and it is much faster
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
        bi.readLine();
        // TODO implement graph with an incidence list

        HashMap<String,LinkedList<String>> graph = new HashMap<>();
        String line;
        while ((line = bi.readLine()) !=null){
            String[] nodes = line.split(" ");
            if (!graph.containsKey(nodes[0])){
                graph.put(nodes[0],new LinkedList<>());
            }
            graph.get(nodes[0]).add(nodes[1]);

        }


        // storing the results in a LinkedList
        LinkedList<String> result = vc(graph);

        // Putting it all together in one String to only use one I/O operation

        StringBuilder sb = new StringBuilder();

        if (!result.isEmpty()){
            for (String s : result) {

                sb.append(s).append("\n");
            }
        }

        String resultStr = sb.toString();
        System.out.print(resultStr);
    }

    static LinkedList<String> vc_branch(HashMap<String,LinkedList<String>> graph,int k){

        if (k<0) return null;
        if (graph.isEmpty()) return new LinkedList<>();
        String firstVertex = graph.keySet().stream().findFirst().get();
        String secondVertex = graph.get(firstVertex).getFirst();

        LinkedList<String> s = vc_branch(eliminateVertex(copyGraph(graph),firstVertex),k-1);
        if (s != null) {
            s.add(firstVertex);
            return s;
        }

        s = vc_branch(eliminateVertex(copyGraph(graph),secondVertex),k-1);
        if (s != null) {
            s.add(secondVertex);
            return s;
        }

        return null;

    }

    // TODO Find a way to eliminateVertex and return graphs to original state efficiently

    // Function to eliminate a given vertex of a graph, removing the vertex is just O(1) but looking for the appearances of the vertex is O (E), being E edges

    static HashMap<String,LinkedList<String>> eliminateVertex(HashMap<String,LinkedList<String>> graph, String vertex){

        graph.remove(vertex);

        Set<String> set = graph.keySet();
        Iterator<String> iterator = set.iterator();

        while(iterator.hasNext()){
            Object v = iterator.next();
            LinkedList<String> list = graph.get(v);
            for (int i = 0; i <list.size() ; i++) {

                list.remove(vertex);
            }
            if (list.isEmpty()){
                iterator.remove();
            }
        }

        return graph;

    }

    // Create a deep copy of the graph to pass it to the recursive function (Worst implementation ever since it has to loop every time to copy the Hashmap. Todo: Find a way to remove and restore a previous state of graph without copying

    public static HashMap<String,LinkedList<String>> copyGraph(HashMap<String,LinkedList<String>> original){
        HashMap<String, LinkedList<String>> copy = new HashMap<>();
        for (Map.Entry<String, LinkedList<String>> entry : original.entrySet())
        {
            copy.put(entry.getKey(),
                    new LinkedList<>(entry.getValue()));
        }
        return copy;
    }

    // main function which increases the cover vertex size k every iteration

    public static LinkedList<String> vc(HashMap<String,LinkedList<String>> graph){
        int k = 0;
        while (true){
            LinkedList<String> s = vc_branch(graph,k);
            if (s!=null) return s;
            k++;

        }
    }

}
