import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class InputParser {
    public long start;
    public HashSet<String[]> edges = new HashSet<>();
    public HashMap<Vertex,HashSet<Vertex>> adjMap = new HashMap<>();

    public InputParser (String inputFile) throws IOException {
        start = System.currentTimeMillis();
        String line;
        BufferedReader bi;
        if (inputFile != null) bi = new BufferedReader(new FileReader(inputFile));
        else bi = new BufferedReader(new InputStreamReader(System.in));
        //min to min Graph
        HashMap<String,Vertex> idMap = new HashMap<>();
        int idCounter = 0;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                Vertex u = idMap.get(nodes[0]);
                if (u == null){
                    u = new Vertex(nodes[0],idCounter++);
                    idMap.put(nodes[0],u);
                }
                Vertex v = idMap.get(nodes[1]);
                if (v == null){
                    v = new Vertex(nodes[1],idCounter++);
                    idMap.put(nodes[1],v);
                }

                // Add (u -> v) to graph
                HashSet<Vertex> neighbors = adjMap.computeIfAbsent(u, k -> new HashSet<>());
                neighbors.add(v);
                // Add (v -> u) to graph
                neighbors = adjMap.computeIfAbsent(v, k -> new HashSet<>());
                neighbors.add(u);
                edges.add(nodes);
            }
        }
    }

    public HashSet<String[]> getEdges() {
        return edges;
    }

    public HashMap<Vertex, HashSet<Vertex>> getAdjMap() {
        return adjMap;
    }

    public void printResult(LinkedList<String> stringSolution, int recursiveSteps){
        StringBuilder sb = new StringBuilder();

        int solutionSize = 0;
        for (String v : stringSolution){
            solutionSize++;
            sb.append(v);
            sb.append("\n");
        }
        sb.append("#sol size: ").append(solutionSize).append("\n");
        sb.append("#recursive steps: ").append(recursiveSteps).append("\n");
        String resultStr = sb.toString();
        System.out.print(resultStr);

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F;
        System.out.println("#time: " + sec);
    }
}
