import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SavageDfs {
    public static void savageAlgorithm(ArrayList<ArrayList<Integer>> graph, ArrayList<String> verticesMapping){
        boolean[] visited = new boolean[graph.size()];
        boolean[] printed = new boolean[graph.size()];
        for(int index = 0; index < graph.size(); index++){
            dfsSavage(graph,index,verticesMapping, visited,printed);
        }
    }

    private static void dfsSavage(ArrayList<ArrayList<Integer>> graph, int vertex, ArrayList<String> verticesMapping, boolean[] visited, boolean[] printed) {

        if(!visited[vertex]){
            visited[vertex] = true;
                for (int neighbor: graph.get(vertex)){
                    if(!visited[neighbor]){
                        if(!printed[vertex]){
                            System.out.println(verticesMapping.get(vertex));
                            printed[vertex] = true;
                        }
                        dfsSavage(graph,neighbor,verticesMapping,visited,printed);
                    }
                }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        ArrayList<ArrayList<Integer>> graph = new ArrayList<>();
        ArrayList<String> verticesMapping = new ArrayList<>();
        HashMap<String,Integer> stringVertexMapping = new HashMap<>();

        String line;
        int index =0;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                    int index1;
                    if(stringVertexMapping.containsKey(nodes[0])){
                        index1 = stringVertexMapping.get(nodes[0]);
                    }else {
                        stringVertexMapping.put(nodes[0],index);
                        graph.add(new ArrayList<>());
                        verticesMapping.add(nodes[0]);
                        index1=index;
                        index++;
                    }
                int index2;
                if(stringVertexMapping.containsKey(nodes[1])){
                    index2 = stringVertexMapping.get(nodes[1]);
                }else {
                    stringVertexMapping.put(nodes[1],index);
                    index2=index;
                    graph.add(new ArrayList<>());
                    verticesMapping.add(nodes[1]);
                    index++;
                }
                graph.get(index1).add(index2);
                graph.get(index2).add(index1);




            }
        }
        savageAlgorithm(graph,verticesMapping);


    }
    }
