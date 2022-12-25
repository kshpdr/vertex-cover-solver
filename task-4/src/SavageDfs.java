import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class SavageDfs {
    public static void savageAlgorithmIterative(ArrayList<ArrayList<Integer>> graph, ArrayList<String> verticesMapping, StringBuilder sb){
        boolean[] visited = new boolean[graph.size()];
        Stack<Integer> vertexStack = new Stack<>();
        if(!graph.isEmpty()){
            for (int vertex = graph.size()-1;vertex>=0;vertex--){
                    vertexStack.add(vertex);
            }
        }
        while(!vertexStack.isEmpty()){
            int vertex;
            vertex = vertexStack.pop();
            if(visited[vertex]){
                continue;
            }
            visited[vertex] = true;
            boolean toPrint = false;

            for (int neighborIndex = graph.get(vertex).size()-1;neighborIndex>=0;neighborIndex--){
                if(!visited[graph.get(vertex).get(neighborIndex)]){
                    vertexStack.add(graph.get(vertex).get(neighborIndex));
                    toPrint = true;
                }
            }
            if(toPrint){
                //System.out.println(verticesMapping.get(vertex));
                sb.append(verticesMapping.get(vertex));
                sb.append(System.getProperty("line.separator"));
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

        StringBuilder sb = new StringBuilder();
        savageAlgorithmIterative(graph,verticesMapping,sb);
        String resultStr = sb.toString();
        System.out.print(resultStr);


    }
}
