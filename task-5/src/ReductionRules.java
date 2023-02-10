import java.util.*;

public class ReductionRules {
    private final boolean oneDegreeRule;
    private final boolean twoDegreeRule;
    private final boolean dominationRule;
    private final boolean cliqueRule;
    private final boolean anyRule;
    private final HashMap<String,ArrayList<String>> mergeMap;
    private final LinkedList<String> mergeOrder;
    public int remainingVertices;

    public ReductionRules(boolean oneDegreeRule, boolean twoDegreeRule, boolean dominationRule, boolean cliqueRule){
        this.oneDegreeRule = oneDegreeRule;
        this.twoDegreeRule = twoDegreeRule;
        this.dominationRule = dominationRule;
        this.cliqueRule = cliqueRule;
        this.anyRule = oneDegreeRule || twoDegreeRule || dominationRule || cliqueRule;
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
                    String singleNeighbor = neighbors.iterator().next();
                    removeVertex(adjMap, singleNeighbor);
                    result.add(singleNeighbor);
                    reduced = true;
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
                else if (cliqueRule && neighbors.size() == 5){
                    for (Tuple<Clique,Clique> cliques : findCliques(adjMap,neighbors)){
                        Clique C1 = cliques.getFirst();
                        Clique C2 = cliques.getSecond();
                        if (C1.size() == 3 && C2.size() == 2 && isValidOverlappingCliques(adjMap,v,C1,C2)){
                            // Apply reduction rule
                            System.out.println("#OverlappingClique found: "+C1+","+C2);
                            System.out.println("E:"+adjMap);

                            // Add new edges: c1 -> N(c2)
                            for (String c1 : C1.vertices){
                                for (String c2 : C2.vertices){
                                    if (!adjMap.get(c1).contains(c2)){
                                        System.out.println("#c2: "+c2+", "+adjMap);
                                        for (String n : adjMap.get(c2)){
                                            addEdge(adjMap,edges,c1,n);
                                        }
                                        break;
                                    }
                                }
                            }

                            // Remove C2 and v from graph
                            for (String c2 : C2.vertices) removeVertex(adjMap, c2);
                            removeVertex(adjMap, v);

                            reduced = true;
                            break;
                        }
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

    ArrayList<Tuple<Clique,Clique>> findCliques(HashMap<String, HashSet<String>> adjMap, HashSet<String> neighbors){
        ArrayList<Tuple<Clique,Clique>> poss = new ArrayList<>();
        poss.add(new Tuple<Clique,Clique>(new Clique(),new Clique()));

        while (true){
            ArrayList<Tuple<Clique,Clique>> new_poss = new ArrayList<>();
            for (Tuple<Clique,Clique> tuple : poss){
                System.out.println("# poss: "+tuple);
                Clique C1 = tuple.getFirst();
                Clique C2 = tuple.getSecond();
                for (String v : neighbors){
                    if (!C1.contains(v) && !C2.contains(v)){
                        Clique C1new = new Clique(C1.vertices);
                        C1new.addVertex(v);

                        Clique C2new = new Clique(C2.vertices);
                        C2new.addVertex(v);

                        if (C1new.size() > C2.size() && isClique(adjMap,C1new) && isClique(adjMap,C2) && notInPoss(new_poss,C1new,1)) new_poss.add(new Tuple<Clique,Clique>(C1new, C2));
                        if (C1.size() > C2new.size() && isClique(adjMap,C2new) && isClique(adjMap,C1) && notInPoss(new_poss,C2new,2)) new_poss.add(new Tuple<Clique,Clique>(C1, C2new));
                        
                    }
                }
            }
            if (new_poss.size() == 0) break;
            poss = new_poss;
        }
        if (poss.size() > 0) return poss;
        else return new ArrayList<>();
    } 

    boolean isClique(HashMap<String, HashSet<String>> adjMap, Clique clique){
        for (String u : clique.vertices){
            HashSet<String> neighbors = adjMap.get(u);
            for (String v : clique.vertices){
                if (u.equals(v)) continue;
                if (!neighbors.contains(v)) return false;
            }
        }
        return true;
    }

    boolean notInPoss(ArrayList<Tuple<Clique,Clique>> poss, Clique clique,int idx){
        for (Tuple<Clique,Clique> tuple : poss){
            if ((idx == 1 ? tuple.getFirst() : tuple.getSecond()).vertices.equals(clique.vertices)){
                return false;
            }
        }
        return true;
    }

    boolean isValidOverlappingCliques(HashMap<String, HashSet<String>> adjMap, String v, Clique C1, Clique C2){
        if (!(C1.size() > C2.size())) return false;
        else if (C2.size() == 0) return false;
        for (String c1 : C1.vertices){
            if (!adjMap.get(v).contains(c1)) return false;
        }
        for (String c2 : C2.vertices){
            if (!adjMap.get(v).contains(c2)) return false;
        }
        if (!isClique(adjMap, C1)) return false;
        if (!isClique(adjMap, C2)) return false;

        int l = C2.size()-1;
        for (String c1 : C1.vertices){
            int in_graph = 0;
            for (String c2 : C2.vertices){
                if (adjMap.get(c2).contains(c1)){
                    in_graph++;
                    if (in_graph > l) break;
                }
            }
            if (in_graph != l) return false;
        }
        return true;
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

    private void addEdge(HashMap<String, HashSet<String>> adjMap, HashSet<String[]> edges, String u, String v){
        if (!adjMap.get(u).contains(v)){
            adjMap.get(u).add(v);
            adjMap.get(v).add(u);

            String[] edge = {u,v};
            edges.add(edge);
        }
    }
}

class Clique {
    public HashSet<String> vertices;

    public Clique(){
        this.vertices = new HashSet<>();
    }
    
    public Clique(Collection<String> collection){
        this.vertices = new HashSet<>(collection);
    }

    public void addVertex(String vertex){
        this.vertices.add(vertex);
    }

    public void removeVertex(String vertex){
        this.vertices.remove(vertex);
    }

    public boolean contains(String vertex){
        return this.vertices.contains(vertex);
    }

    public int size(){
        return this.vertices.size();
    }

    public String toString(){
        return this.vertices.toString();
    }
}

class Tuple<U,V> {
    private final U u;
    private final V v;

    public Tuple(U u, V v){
        this.u = u;
        this.v = v;
    }

    public U getFirst(){
        return this.u;
    }

    public V getSecond(){
        return this.v;
    }
    
    public String toString(){
        return "<" + this.u + "," + this.v + ">";
    }
}