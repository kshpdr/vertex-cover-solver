import java.util.*;

public class ReductionRules {
    private final boolean oneDegreeRule;
    private final boolean twoDegreeRule;
    private final boolean dominationRule;
    private final boolean independentRule;
    private final boolean anyRule;
    private final HashMap<String,ArrayList<String>> mergeMap;
    private final LinkedList<String> mergeOrder;
    public int remainingVertices;

    public ReductionRules(boolean oneDegreeRule, boolean twoDegreeRule, boolean dominationRule, boolean independentRule){
        this.oneDegreeRule = oneDegreeRule;
        this.twoDegreeRule = twoDegreeRule;
        this.dominationRule = dominationRule;
        this.independentRule = independentRule;
        this.anyRule = oneDegreeRule || twoDegreeRule || dominationRule || independentRule;
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
                else if (neighbors.size() == 0) {
                    removeVertex(adjMap, v);
                }
                else if (oneDegreeRule && neighbors.size() == 1) {
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
                        arr.add(0,"degree2-rule");
                        removeVertex(adjMap, u);
                        removeVertex(adjMap, w);
                        removeVertex(adjMap, v);
                        addVertex(adjMap, x, newNeighbors);
                        //System.out.println("#After: "+adjMap);
                    }
                    reduced = true;
                }
                else if (dominationRule){
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
                else if (independentRule && neighbors.size() == 3){
                    Iterator<String> iter = neighbors.iterator();
                    String a = iter.next();
                    String b = iter.next();
                    String c = iter.next();
                    
                    boolean isIndependentSet = !adjMap.get(a).contains(b) && !adjMap.get(b).contains(c) && !adjMap.get(c).contains(a);
                    if (isIndependentSet){
                        //System.out.println("#before: "+adjMap);
                        // Remove v from graph
                        removeVertex(adjMap, v);
                        
                        // Add new edges: a->N(b) and b->N(c) and c->N(a)
                        HashSet<String> Na = new HashSet<>(adjMap.get(a));
                        HashSet<String> Nb = new HashSet<>(adjMap.get(b));
                        HashSet<String> Nc = new HashSet<>(adjMap.get(c));
                        for (String nb : Nb) addEdge(adjMap,edges,a,nb);
                        for (String nc : Nc) addEdge(adjMap,edges,b,nc);
                        for (String na : Na) addEdge(adjMap,edges,c,na);
                        // Add new edges: (a,b) and (b,c)
                        addEdge(adjMap,edges,a,b);
                        addEdge(adjMap,edges,b,c);

                        // Save vertices for future "merge-undo" operation
                        String v2 = v + "-ind";
                        ArrayList<String> arr = new ArrayList<>();
                        arr.add("independent-rule");
                        arr.add(a);
                        arr.add(b);
                        arr.add(c);
                        arr.add(v);

                        mergeMap.put(v2,arr);
                        mergeOrder.add(v2);

                        //System.out.println("#after: "+adjMap);
                        reduced = true;
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

    void undoMerge(HashSet<String> solution){
        //System.out.println("#Result: "+solution);
        while (mergeOrder.size() > 0) {
            String x = mergeOrder.removeLast();
            ArrayList<String> vertices = mergeMap.get(x);

            String type = vertices.get(0);
            
            if (type == "degree2-rule"){
                //System.out.println("#undo (deg2): "+x+" => "+vertices);
                // Case: x in solution
                if (solution.contains(x)){
                    // Remove x from solution
                    solution.remove(x);
                    // Add u and w to solution
                    String u = vertices.get(1);
                    String w = vertices.get(2);
                    solution.add(u);
                    solution.add(w);
                    //System.out.println("#Undo: "+x+" => "+vertices.subList(0, 1));
                }
                // Case: x not in solution
                else {
                    // Add only v to solution
                    String v = vertices.get(3);
                    solution.add(v);
                    //System.out.println("#Undo: "+x+" => "+vertices.get(2));
                }
            }
            else if (type == "independent-rule"){
                String a = vertices.get(1);
                String b = vertices.get(2);
                String c = vertices.get(3);
                String v = vertices.get(4);
                
                // Check how many vertices from {a,b,c} are in solution
                boolean inSolutionA = solution.contains(a);
                boolean inSolutionB = solution.contains(b);
                boolean inSolutionC = solution.contains(c);

                int inSolution = (inSolutionA ? 1 : 0) + (inSolutionB ? 1 : 0) + (inSolutionC ? 1 : 0);

                // Only 1 vertex in solution S'
                if (inSolution == 1){
                    // Replace b with v in solution
                    solution.remove(b);
                    solution.add(v);
                }
                // Two vertices in solution S'
                else if (inSolution == 2){
                    // Replace one of the two with v in solution
                    if (!inSolutionA) solution.remove(b);
                    else if (!inSolutionB) solution.remove(c);
                    else solution.remove(a);
                    solution.add(v);
                }
                // All three vertices in Solution S'
                else if (inSolution == 3){
                    // Nothing to do (S = S')
                }
                else {
                    System.out.println("[ERROR] Unexpected value: inSolution="+inSolution);
                    break;
                }
            }
            else {
                System.out.println("[ERROR] Unexpected merge type: "+type);
                break;
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

    private void addEdge(HashMap<String,HashSet<String>> adjMap, HashSet<String[]> edges, String u, String v){
        //System.out.println("#addEdge("+u+","+v+")");
        if (!adjMap.get(u).contains(v)){
            adjMap.get(u).add(v);
            adjMap.get(v).add(u);

            String[] edge = {u,v};
            edges.add(edge);
        }
    }
}