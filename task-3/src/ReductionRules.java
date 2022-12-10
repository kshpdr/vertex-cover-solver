import java.util.*;

public class ReductionRules {
    private boolean oneDegreeRule, twoDegreeRule, anyRule;
    private HashMap<String,ArrayList<String>> mergeMap;
    private LinkedList<String> mergeOrder;

    public ReductionRules(boolean oneDegreeRule, boolean twoDegreeRule){
        this.oneDegreeRule = oneDegreeRule;
        this.twoDegreeRule = twoDegreeRule;
        this.anyRule = oneDegreeRule || twoDegreeRule;
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
                HashSet<String> neighbors = adjMap.get(u);
                if (neighbors == null){
                    neighbors = new HashSet<>();
                    adjMap.put(u, neighbors);
                }
                neighbors.add(v);

                neighbors = adjMap.get(v);
                if (neighbors == null){
                    neighbors = new HashSet<>();
                    adjMap.put(v, neighbors);
                }
                neighbors.add(u);
            }
        }

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
                    arr.add(v);
                    if (adjMap.get(u).contains(w)){
                        result.add(u);
                        result.add(w);
                        removeVertex(adjMap, u);
                        removeVertex(adjMap, w);
                    }
                    else {
                        arr.add(v);
                        String x = u + " - "+ v + " - " + w;
                        //System.out.println("#Degree-2 Rule: Case-2 ==> "+x);
                        //System.out.println("#Before: "+adjMap);
                        mergeMap.put(x,arr);
                        mergeOrder.add(x);
                        HashSet<String> newNeighbors = new HashSet<>();
                        for (String n : adjMap.get(u)){
                            newNeighbors.add(n);
                        }
                        for (String n : adjMap.get(w)){
                            newNeighbors.add(n);
                        }
                        newNeighbors.removeAll(arr);
                        for (String n : newNeighbors){
                            String[] edge = {x,n};
                            edges.add(edge);
                        }
                        removeVertex(adjMap, u);
                        removeVertex(adjMap, w);
                        addVertex(adjMap, x, newNeighbors);
                        //System.out.println("#After: "+adjMap);
                        reduced = true;
                    }
                }
            }
            if (!reduced) break;
        }

        // Loop through initial HashSet of edges and delete (... those which are not
        // used anymore)
        edges.removeIf(edge -> !adjMap.containsKey(edge[0]) || !adjMap.get(edge[0]).contains(edge[1]));

        // Return partial result obtained from reduction rules
        return result;
    }

    void undoMerge(LinkedList<String> solution){
        //System.out.println("#Result: "+solution);
        while (mergeOrder.size() > 0) {
            String x = mergeOrder.removeLast();
            ArrayList<String> vertices = mergeMap.get(x);
            int index = solution.indexOf(x);
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