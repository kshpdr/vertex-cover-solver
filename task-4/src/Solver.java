import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solver {
    public static boolean oneDegreeRulePre =false;
    public static boolean twoDegreeRulePre = false;
    public static boolean dominationRulePre = true;

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

    public static int recursiveSteps = 0;

    static LinkedList<String> vc_branch(Graph graph, int k) {
        HashMap<Vertex, HashSet<Vertex>> reducedNeighborsMap = new HashMap<>();

        if(highDegreeRuleIteration){
            reducedNeighborsMap.putAll(graph.applyHighDegreeRule(k));
            if (graph.applyBussRule(k - reducedNeighborsMap.size())){
                graph.putManyVerticesBack(reducedNeighborsMap);
                return null;
            }
        }

        if(dominationRuleIteration) {
            reducedNeighborsMap.putAll(graph.applyDominationRule());
        }

        if(unconfinedRuleIteration) {
            reducedNeighborsMap.putAll(graph.applyUnconfinedRule());
        }

        if (oneDegreeRuleIteration){
            reducedNeighborsMap.putAll(graph.applyOneDegreeRule());
        }

        if (twoDegreeRuleIteration){
            reducedNeighborsMap.putAll(graph.applyTwoDegreeRule());
        }


        k -= reducedNeighborsMap.size();

        if (k < 0) {

            // Putting back the reduced vertices
            graph.putManyVerticesBack(reducedNeighborsMap);
            return null;
        }
        if (graph.isEmpty()){
            LinkedList<String> result = new LinkedList<>();
            for (Vertex v : reducedNeighborsMap.keySet()){
                result.add(v.name);
            }
            return result;
        }

        if(k < graph.getMaxLowerBound(cliqueBoundIteration  && graph.getVertices().size()<90, lpBoundIteration)) {
            // Putting back the reduced vertices
            graph.putManyVerticesBack(reducedNeighborsMap);
            return null;
        }


        //System.out.println("k: " + k + " Clique Lower Bound: " + graph.getCliqueLowerBound());

        LinkedList<String> solution;
        recursiveSteps++;

        // Get vertex with the highest degree
        Vertex v = graph.getNextNode();
        HashSet<Vertex> eliminatedNeighbors = graph.removeVertex(v);

        solution = vc_branch(graph, k - 1);
        graph.putVertexBack(v, eliminatedNeighbors);


        if (solution!= null) {
            solution.add(v.name);
            for (Vertex neighbor : reducedNeighborsMap.keySet()){
                solution.add(neighbor.name);
            }
            return solution;
        }

        // Eliminating the neighbors of the vertex with the highest degree and storing
        // the neighbors of the neighbors with a hashmap
        HashMap<Vertex, HashSet<Vertex>> eliminatedNeighborsMap = graph.removeSetofVertices(eliminatedNeighbors);

        // Branching with the neighbors
        solution = vc_branch(graph, k - eliminatedNeighbors.size());
        graph.putManyVerticesBack(eliminatedNeighborsMap);

        // Putting back the eliminated vertices
        if (solution != null) {
            for (Vertex neighbor : eliminatedNeighborsMap.keySet()){
                solution.add(neighbor.name);
            }
            for (Vertex neighbor : reducedNeighborsMap.keySet()){
                solution.add(neighbor.name);
            }
            return solution;
        }

        // Putting back the reduced vertices
        graph.putManyVerticesBack(reducedNeighborsMap);

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

    public static void savageAlgorithmIterative(ArrayList<ArrayList<Integer>> graph, ArrayList<String> verticesMapping, StringBuilder sb){
        boolean[] visited = new boolean[graph.size()];
        Stack<Integer> vertexStack = new Stack<>();
        if(!graph.isEmpty()){
            vertexStack.add(0);
        }
        int numberVisited=0;

        while(!vertexStack.isEmpty() || numberVisited<graph.size()){


            int vertex = 0;
            if(vertexStack.isEmpty()){
                for(int i=1; i<visited.length;i++){
                    if(!visited[i]){
                        vertex = i;
                        break;
                    }
                }
            }else{
             vertex = vertexStack.pop();
            }

            if(visited[vertex]){
                continue;
            }
            numberVisited+=1;
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

        ArrayList<ArrayList<Integer>> graphForDFS = new ArrayList<>();
        ArrayList<String> verticesMapping = new ArrayList<>();
        HashMap<String,Integer> stringVertexMapping = new HashMap<>();


        // Storing edges to call the graph constructor afterwards
        HashSet<String[]> edges = new HashSet<>();

        String line;
        int index =0;

        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }
        }


        long start = System.currentTimeMillis();

        // Apply reduction rules before instatiating graph (+ internally used
        // datastructure(s))
        ReductionRules preReduction;
        if(edges.size()<=600000) {
           preReduction = new ReductionRules(false,false,true);
        } else{
            preReduction = new ReductionRules(true,false,false);
        }
        LinkedList<String> reductionResult;

        reductionResult = preReduction.applyReductionRules(edges);


        for(String[] nodes: edges){
            int index1;
            if(stringVertexMapping.containsKey(nodes[0])){
                index1 = stringVertexMapping.get(nodes[0]);
            }else {
                stringVertexMapping.put(nodes[0],index);
                graphForDFS.add(new ArrayList<>());
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
                graphForDFS.add(new ArrayList<>());
                verticesMapping.add(nodes[1]);
                index++;
            }
            graphForDFS.get(index1).add(index2);
            graphForDFS.get(index2).add(index1);




        }

        // Instantiate graph
       // Graph graph = new Graph(edges);



//        if(unconfinedRuleBeginning) {
//            edgesAfterRules.putAll(graph.applyUnconfinedRule());
//        }
//
//        if(lpReductionBeginning){
//            edgesAfterRules.putAll(graph.applyLpReduction());
//        }
//
//
//
//        // Call method with the clique lower bound
//        int lowerbound = graph.getMaxLowerBound(cliqueBoundBeginning && graph.getVertices().size()<12000, lpBoundBeginning);
//
//        if (highDegreeRuleBeginning){
//            edgesAfterRules.putAll(graph.applyHighDegreeRule(lowerbound));
//            while (graph.applyBussRule(lowerbound)){
//                lowerbound++;
//            }
//        }
//        for(Vertex vertex: graph.getVertices()){
//            System.out.println(vertex.name);
//        };




        //LinkedList<String> result = vc(graph, lowerbound);

        // Putting it all together in one String to only use one I/O operation
        StringBuilder sb = new StringBuilder();

        savageAlgorithmIterative(graphForDFS,verticesMapping,sb);

//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            if(graph.completeReduced){
//                graph.printReducedGraph();
//
//            } else {
//                copyGraph.printReducedGraph();
//            }
//
//        }));

        int solutionSize = 0;

        // Save all results in one list
        LinkedList<String> allResults = new LinkedList<>();

        //Add results from reduction rules
        if (!reductionResult.isEmpty()) {
            allResults.addAll(reductionResult);
        }
        

        // Add results from actual branching algorithm
//        if (!result.isEmpty()) {
//            allResults.addAll(result);
//        }

        if (twoDegreeRulePre){
            preReduction.undoMerge(allResults);
        }

        for (String v : allResults){
            sb.append(v);
            sb.append("\n");
            solutionSize++;
        }

        sb.append("#recursive steps: ").append(recursiveSteps).append("\n");
        sb.append("#sol size: ").append(solutionSize).append("\n");

        String resultStr = sb.toString();
        System.out.print(resultStr);

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F;
        System.out.println("#time: " + sec);
    }
}
