import java.util.*;

public class ReductionRules {
    private final boolean oneDegreeRule;
    private final boolean twoDegreeRule;
    private final boolean dominationRule;
    private final boolean twinRule;
    private final boolean anyRule;
    private final HashMap<String,ArrayList<String>> mergeMap;
    private final LinkedList<String> mergeOrder;
    public int remainingVertices;
    public int twinCount = 0;

    public ReductionRules(boolean oneDegreeRule, boolean twoDegreeRule, boolean dominationRule, boolean twinRule){
        this.oneDegreeRule = oneDegreeRule;
        this.twoDegreeRule = twoDegreeRule;
        this.dominationRule = dominationRule;
        this.twinRule = twinRule;
        this.anyRule = oneDegreeRule || twoDegreeRule || dominationRule || twinRule;
        this.mergeMap = new HashMap<>();
        this.mergeOrder = new LinkedList<>();
    }

    public LinkedList<String> applyReductionRules(HashSet<String[]> edges) {
        if (!anyRule) return new LinkedList<>();

        // Build Adjacency-Map (to extract node-degree) from HashSet (of Edges)
        HashMap<String, HashSet<String>> adjMap = new HashMap<>();
        for (String[] edge : edges) {
            for (int i = 0; i < 2; i++) {
                String u = edge[i % 2];
                String v = edge[(i + 1) % 2];
                HashSet<String> neighbors = adjMap.computeIfAbsent(u, k -> new HashSet<>());
                neighbors.add(v);

                neighbors = adjMap.computeIfAbsent(v, k -> new HashSet<>());
                neighbors.add(u);
            }
        }
        remainingVertices = adjMap.size();

        // Instatiate solver result (for partial solution obtained from applying
        // reduction rules multiple times)
        LinkedList<String> result = new LinkedList<>();

        // Until no more reduction possible!
        while (true) {
            boolean reduced = false;
            // Loop over all vertices
            for (String v : new LinkedList<>(adjMap.keySet())) {
                // Try to find a vertex v with degree = 1
                HashSet<String> neighbors = adjMap.get(v);
                if (neighbors == null) continue;
                if (oneDegreeRule && neighbors.size() == 1) {
                    for (String singleNeighbor : neighbors) {
                        removeVertex(adjMap, singleNeighbor);
                        reduced = true;
                        result.add(singleNeighbor);
                    }
                }
                else if (twoDegreeRule && neighbors.size() == 2){
                    ArrayList<String> arr = new ArrayList<>(neighbors);
                    String u = arr.get(0);
                    String w = arr.get(1);
                    if (adjMap.get(u).contains(w)){
                        result.add(u);
                        result.add(w);
                        removeVertex(adjMap, u);
                        removeVertex(adjMap, w);
                        removeVertex(adjMap, v);
                    }
                    else {
                        arr.add(v);
                        String x = u + " - "+ v + " - " + w;
                        //System.out.println("#Degree-2 Rule: Case-2 ==> "+x);
                        //System.out.println("#Before: "+adjMap);
                        mergeMap.put(x,arr);
                        mergeOrder.add(x);
                        HashSet<String> newNeighbors = new HashSet<>();
                        newNeighbors.addAll(adjMap.get(u));
                        newNeighbors.addAll(adjMap.get(w));
                        arr.forEach(newNeighbors::remove);
                        for (String n : newNeighbors){
                            String[] edge = {x,n};
                            edges.add(edge);
                        }
                        removeVertex(adjMap, u);
                        removeVertex(adjMap, w);
                        removeVertex(adjMap, v);
                        addVertex(adjMap, x, newNeighbors);
                        //System.out.println("#After: "+adjMap);
                        reduced = true;
                    }
                }
                else if (twinRule && neighbors.size() == 3){
                    HashSet<String> Nv = neighbors;
                    for (String u : adjMap.keySet()){
                        if (v.equals(u)) continue;
                        HashSet<String> Nu = adjMap.get(u);
                        if (Nu.size() == 3){
                            boolean isTwin = false;
                            
                            HashSet<String> intersect = new HashSet<>(Nv);
                            intersect.retainAll(Nu);
                            
                            // Case 1: (u,v) not in E
                            if (intersect.size() == 3) isTwin = true;
                            // Case 2: (u,v) in E
                            else if (intersect.size() == 2 && Nv.contains(u)) isTwin = true;
                            
                            // If twins found: u,v where N(u) = N(v) and |N(u)| = 3
                            if (isTwin){
                                System.out.println("#is-twin: "+u+" <=> "+v+" -> "+Nu+" <- "+Nv);
                                reduced = true;
                                // Check if G[N(u)] has edges
                                boolean hasEdges = false;
                                for (String n : Nu){
                                    for (String n2 : Nu){
                                        if (n.compareTo(n2) <= 0) continue;
                                        if (adjMap.get(n).contains(n2)) {
                                            hasEdges = true;
                                            break;
                                        }
                                    }
                                }
                                // Case 1: G[N(u)] has edges
                                if (hasEdges){
                                    // Add N(u) to solution
                                    result.addAll(Nu);
                                }
                                // Case 2: G[N(u)] does not have edges
                                else {
                                    // Create new vertex w
                                    String w = "twin-"+twinCount++;
                                    // Find 2-neighborhood of vertex u
                                    HashSet<String> twoNeighborhood = new HashSet<>();
                                    for (String n1 : Nu){
                                        for (String n2 : adjMap.get(n1)){
                                            if (n2.equals(v) || n2.equals(u) || Nu.contains(n2)) continue;
                                            if (twoNeighborhood.add(n2)){
                                                String[] edge = {w,n2};
                                                edges.add(edge);
                                            }
                                        }
                                    }
                                    // Make w adjacent with 2-neighborhood of vertex u
                                    addVertex(adjMap, w, twoNeighborhood);
                                    
                                    // Save vertex w and {u,v} for undoing merge later
                                    ArrayList<String> arr = new ArrayList<>();
                                    arr.add(u);
                                    arr.add(v);
                                    mergeOrder.add(w);
                                    mergeMap.put(w,arr);
                                }
                                // Remove v, u, N(u) and N(v) from graph
                                removeVertex(adjMap, v);
                                removeVertex(adjMap, u);
                                for (String n : Nu) removeVertex(adjMap, n);
                            }
                            // Stop searching if twins already found
                            if (reduced) break;
                        }
                    }
                }
                if (dominationRule){
                    boolean delete = false;
                    for (String n : neighbors){
                        HashSet<String> neighbors2 = adjMap.get(n);
                        neighbors2.remove(v);
                        if (neighbors.size() >= neighbors2.size() && neighbors.containsAll(neighbors2)){
                            neighbors2.add(v);
                            delete = true;
                            reduced = true;
                            break;
                        }
                        else neighbors2.add(v);
                    }
                    if (delete){
                        removeVertex(adjMap, v);
                        result.add(v);
                    }
                }
            }
            if (!reduced) break;
        }

        // Loop through initial HashSet of edges and delete (... those which are not
        // used anymore)
        edges.removeIf(edge -> !adjMap.containsKey(edge[0]) || !adjMap.get(edge[0]).contains(edge[1]));

        remainingVertices = adjMap.size();
        // Return partial result obtained from reduction rules
        return result;
    }

    void undoMerge(LinkedList<String> solution){
        //System.out.println("#Result: "+solution);
        while (mergeOrder.size() > 0) {
            String x = mergeOrder.removeLast();
            ArrayList<String> vertices = mergeMap.get(x);
            int index = solution.indexOf(x);
            // Case: Two-Degree Rule
            if (vertices.size() == 3){
                if (index != -1){
                    solution.remove(index);
                    solution.add(vertices.get(0));  // Vertex u
                    solution.add(vertices.get(1));  // Vertex w
                    //System.out.println("#Undo: "+x+" => "+vertices.subList(0, 1));
                }
                else {
                    solution.add(vertices.get(2));  // Vertex v
                    //System.out.println("#Undo: "+x+" => "+vertices.get(2));
                }
            }
            // Case: Twin Rule
            else {
                if (index != -1){
                    solution.remove(index);
                }
                else {
                    solution.addAll(vertices);
                }
            }
        }
    }

    private void removeVertex(HashMap<String,HashSet<String>> adjMap, String vertex){
        HashSet<String> neighbors = adjMap.remove(vertex);
        if (neighbors != null){
            for (String neighbor : neighbors){
                HashSet<String> nextNeighbors = adjMap.get(neighbor);
                if (nextNeighbors != null) nextNeighbors.remove(vertex);
            }
        }
    }

    private void addVertex(HashMap<String, HashSet<String>> adjMap, String v, HashSet<String> neighbors){
        adjMap.put(v,neighbors);
        for (String n : neighbors){
            adjMap.get(n).add(v);
        }
    }
}