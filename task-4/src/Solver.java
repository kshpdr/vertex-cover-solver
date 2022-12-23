import java.util.*;

public class Solver {
    public static HashMap<String,HashSet<String>> parseInput(){
        // Store graph as adjacency-list
        HashMap<String,HashSet<String>> adjMap = new HashMap<>();

        // Parse input graph
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            // Line by line
            String line = scanner.nextLine();
            // Skip comments and empty lines
            if (line.startsWith("#") || line.length() == 0) continue;
            // Parse edge
            String[] edge = line.split(" ");
            String u = edge[0];
            String v = edge[1];

            // Save (u,v) in adjacency-list
            HashSet<String> neighbors = adjMap.get(u);
            if (neighbors == null){
                neighbors = new HashSet<>();
                adjMap.put(u,neighbors);
            }
            neighbors.add(v);
            // Save (v,u) in adjacency-list
            neighbors = adjMap.get(v);
            if (neighbors == null){
                neighbors = new HashSet<>();
                adjMap.put(v,neighbors);
            }
            neighbors.add(u);
        }
        scanner.close();

        return adjMap;
    }
    public static void greedy_vertex_cover_heuristic(HashMap<String,HashSet<String>> adjMap){
        StringBuilder sb = new StringBuilder();
        int lastMaxDegree = -2;
        // Repeat until graph completely covered ...
        while (!adjMap.isEmpty()){
            // Find max degree vertex
            String maxVertex = null;
            int maxDegree = -1;
            for (Map.Entry<String,HashSet<String>> entry : adjMap.entrySet()){
                String v = entry.getKey();
                int degree = entry.getValue().size();
                if (maxVertex == null || maxDegree < degree){
                    maxVertex = v;
                    maxDegree = degree;
                    // Speed-Up trick: if lastMaxDegree same as current maxDegree we can stop iterating ... there will be no vertex with higher degree!
                    if (lastMaxDegree == maxDegree) break;
                }
            }
            lastMaxDegree = maxDegree;
            // Add it to solution
            sb.append(maxVertex+"\n");
            // Remove it from graph
            HashSet<String> neighbors = adjMap.remove(maxVertex);
            // Remove it from its neighbors
            for (String neighbor : neighbors){
                HashSet<String> nextNeighbors = adjMap.get(neighbor);
                nextNeighbors.remove(maxVertex);
                // Delete 0-degree nodes
                if (nextNeighbors.isEmpty()){
                    adjMap.remove(neighbor);
                }
            }
        }
        // Print result
        System.out.print(sb.toString());
    }
    public static void main(String[] args){
        HashMap<String,HashSet<String>> adjMap = parseInput();
        greedy_vertex_cover_heuristic(adjMap);
    }
}