import java.util.*;

public class ReductionRules {
    private boolean oneDegreeRule, twoDegreeRule, anyRule;

    public ReductionRules(boolean oneDegreeRule, boolean twoDegreeRule){
        this.oneDegreeRule = oneDegreeRule;
        this.twoDegreeRule = twoDegreeRule;
        this.anyRule = oneDegreeRule || twoDegreeRule;
    }

    public LinkedList<String> applyReductionRules(HashSet<String[]> edges) {
        // Skip this function, if none of the possible rules should be applied
        if (!anyRule) return new LinkedList<>();

        // Build Adjacency-Map (to extract node-degree) from HashSet (of Edges)
        HashMap<String, HashSet<String>> adjMap = new HashMap<>();
        for (String[] edge : edges) {
            for (int i = 0; i < 2; i++) {
                String u = edge[i % 2];
                String v = edge[(i + 1) % 2];
                HashSet<String> neighbors = adjMap.get(u);
                if (neighbors == null)
                    neighbors = new HashSet<>();
                neighbors.add(v);
                adjMap.put(u, neighbors);
            }
        }

        // Instatiate solver result (for partial solution obtained from applying
        // reduction rules multiple times)
        LinkedList<String> result = new LinkedList<>();

        // Repeatedly apply reduction rules ...
        LinkedList<String> vertices = reductionRules(adjMap);
        // Until no more reduction possible!
        while (vertices != null) {
            for (String v : vertices){
                // Save partial solution
                result.add(v);
            }
            vertices = reductionRules(adjMap);
        }

        // Loop through initial HashSet of edges and delete (... those which are not
        // used anymore)
        edges.removeIf(edge -> !adjMap.containsKey(edge[0]) || !adjMap.get(edge[0]).contains(edge[1]));

        // Return partial result obtained from reduction rules
        return result;
    }

    public LinkedList<String> reductionRules(HashMap<String, HashSet<String>> adjMap) {
        LinkedList<String> result = new LinkedList<>();
        // Loop over all vertices
        for (String v : adjMap.keySet()) {
            // Try to find a vertex v with degree = 1
            HashSet<String> neighbors = adjMap.get(v);
            if (neighbors != null && neighbors.size() == 1 && oneDegreeRule) {
                for (String singleNeighbor : neighbors) {
                    // System.out.println("#add to list: "+singleNeighbor);
                    // Remove N(v) and add it to the VC-result
                    HashSet<String> nextNeighbors = adjMap.get(singleNeighbor);
                    if (nextNeighbors != null) {
                        for (String next : nextNeighbors) {
                            adjMap.get(next).remove(singleNeighbor);
                        }
                    }
                    adjMap.remove(singleNeighbor);
                    result.add(singleNeighbor);
                    return result;
                }
            }
        }
        return null;
    }
}