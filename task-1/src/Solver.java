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


            if (!line.contains("#") && !line.isEmpty()){
                String[] nodes = line.split(" ");
                if (!graph.containsKey(nodes[0])){
                    graph.put(nodes[0],new HashSet<>());
                }
                graph.get(nodes[0]).add(nodes[1]);
            }


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


    static LinkedList<String> vc_branch(HashMap<String,HashSet<String>> graph,int k){

        if (k<0) return null;
        if (graph.isEmpty()) return new LinkedList<>();

        // Get random vertex and random neighbor (not some random since it is the first one :)

        String firstVertex = graph.keySet().iterator().next();
        String secondVertex = graph.get(firstVertex).iterator().next();

        // HashSet to store eliminated vertices to add them after the recursive call and avoid copying the graph
        HashSet<String> eliminatedVertices = eliminateVertex(graph,firstVertex);
        LinkedList<String> s = vc_branch(graph,k-1);


        //Putting back the eliminated vertices

        graph.put(firstVertex,eliminatedVertices);
        if (s != null) {
            s.add(firstVertex);
            return s;
        }

        eliminatedVertices = eliminateVertex(graph,secondVertex);
        s = vc_branch(graph,k-1);

        //Putting back the eliminated vertices

        graph.put(secondVertex,eliminatedVertices);
        if (s != null) {
            s.add(secondVertex);
            return s;
        }


        return null;

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

    // main function which increases the cover vertex size k every iteration

    public static LinkedList<String> vc(HashMap<String,HashSet<String>> graph){
        LinkedList<String> s;
        int k = 0;
        while ((s = vc_branch(graph,k++))==null);
        return s;
    }

}


