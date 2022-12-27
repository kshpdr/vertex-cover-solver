import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static boolean oneDegreeRulePre =false;
    public static boolean twoDegreeRulePre = false;
    public static boolean dominationRulePre = false;

    public static boolean lpBoundBeginning  = false;
    public static boolean cliqueBoundBeginning =false;
    public static boolean unconfinedRuleBeginning = false;
    public static boolean highDegreeRuleBeginning = false;
    public static boolean lpReductionBeginning = false;

    public static boolean cliqueBoundIteration= false;
    public static boolean lpBoundIteration= false;
    public static boolean dominationRuleIteration = false;
    public static boolean unconfinedRuleIteration = false;
    public static boolean highDegreeRuleIteration = false;
    public static boolean oneDegreeRuleIteration = false;
    public static boolean twoDegreeRuleIteration = false;
    public static StringBuilder sb = new StringBuilder();

    public static int recursiveSteps = 0;

    // main function which increases the cover vertex size k every iteration
    public static void vc_greedy_heuristic(Graph graph) {
        while (!graph.isEmpty()){
            // Get vertex with the highest degree
            Vertex v = graph.getNextNode();
            // Remove it from graph
            graph.removeVertexVoid(v);
            // Add it to result
            sb.append(v.name).append("\n");
            recursiveSteps++;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));

        // Storing edges to call the graph constructor afterwards
        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
//                if(nodes.length==1){
//                    System.exit(0);
//                }
                edges.add(nodes);

            }
        }

        long start = System.currentTimeMillis();
    
        // Instantiate graph
        Graph graph = new Graph(edges);

        vc_greedy_heuristic(graph);

        sb.append("#recursive steps: ").append(recursiveSteps).append("\n");

        String resultStr = sb.toString();
        System.out.print(resultStr);

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F;
        System.out.println("#time: " + sec);
    }
}
